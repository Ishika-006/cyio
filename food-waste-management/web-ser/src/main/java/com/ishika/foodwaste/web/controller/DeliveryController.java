package com.ishika.foodwaste.web.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.Donor;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entity.NGOS;
import com.ishika.foodwaste.jpa.entityManager.RecentActivityManager;
import com.ishika.foodwaste.jpa.enums.DonationStatus;
import com.ishika.foodwaste.jpa.utils.LocationDto;
import com.ishika.foodwaste.web.dto.DeliveryRequestDTO;
import com.ishika.foodwaste.web.service.DeliveryService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
@RestController
@RequestMapping("/delivery")
public class DeliveryController {
	
	@PersistenceContext
	EntityManager entityManager;

    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    RecentActivityManager manager;
    
   @PostMapping("/login")
public ResponseEntity<?> loginDelivery(@RequestBody Map<String, String> body, HttpSession session) {

    System.out.println("🔵 [LOGIN] DeliveryPerson request received");

    String email = body.get("email");
    String password = body.get("password");

    DeliveryPerson person = deliveryService.authenticate(email);
    if (person == null) {
        System.out.println("❌ DeliveryPerson not found");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Delivery person not found"));
    }

    if (!passwordEncoder.matches(password, person.getPassword())) {
        System.out.println("❌ Password mismatch for delivery person");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Incorrect password"));
    }

    // ✅ SESSION
    session.setAttribute("deliveryId", person.getDid());
    session.setMaxInactiveInterval(60 * 60); // 1 hour

    System.out.println("✅ DeliveryPerson login successful. Session ID: " + session.getId());
    manager.logActivity(person.getName(), "login as delivery person", "", "info");

    return ResponseEntity.ok(Map.of("message", "DeliveryPerson login successful"));
}


    // ✅ Register
    @PostMapping("/register")
    public ResponseEntity<?> registerDelivery(@RequestBody DeliveryPerson person) {
		  person.setPassword(passwordEncoder.encode(person.getPassword()));
        String result = deliveryService.register(person);
        if (result.contains("successful")) {
            manager.logActivity(person.getName(), "registered as delivery person", "", "new");
            return ResponseEntity.ok(Map.of("message", result));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", result));
        }
    }

    // ✅ Logout
    @PostMapping("/custom-logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            return ResponseEntity.ok("Logout successful.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No session found.");
        }
    }

    // ✅ Unassigned orders
    @GetMapping("/unassigned")
    public List<DeliveryRequestDTO> getUnassignedOrders(HttpSession session) {
        Integer deliveryId = (Integer) session.getAttribute("deliveryId");
        if (deliveryId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Delivery person not logged in");
        }

        DeliveryPerson dp = deliveryService.findById(deliveryId);
        String deliveryPersonCity = dp.getCity();

        List<FoodDonation> donations = entityManager.createQuery(
                "SELECT f FROM FoodDonation f WHERE f.status = :status AND f.deliveryPerson IS NULL AND f.location LIKE :cityPattern",
                FoodDonation.class)
                .setParameter("status", DonationStatus.ACCEPTED)
                .setParameter("cityPattern", deliveryPersonCity + "%")
                .getResultList();

        return donations.stream().map(f -> {
            DeliveryRequestDTO dto = new DeliveryRequestDTO();
            dto.setFid(f.getFid());

            if (f.getAssignedNGO() != null) {
                dto.setNgoName(f.getAssignedNGO().getName());
                dto.setNgoAddress(f.getAssignedNGO().getAddress());
                dto.setNgoCity(f.getAssignedNGO().getCity());
            }

            dto.setDonorName(f.getDonor().getName());
            dto.setDonorAddress(f.getAddress());
            dto.setDonorCity(f.getLocation());

            dto.setFood(f.getFood());
            dto.setType(f.getType());
            dto.setCategory(f.getCategory());
            dto.setQuantity(f.getQuantity());

            return dto;
        }).toList();
    }

    // ✅ My orders
    @GetMapping("/myorders")
    public List<FoodDonation> getMyOrders(HttpSession session) {
        Integer deliveryId = (Integer) session.getAttribute("deliveryId");
        if (deliveryId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Delivery person not logged in");
        }

        return entityManager.createQuery(
                        "SELECT f FROM FoodDonation f WHERE f.deliveryPerson.did = :did AND f.status IN (:statuses)",
                        FoodDonation.class)
                .setParameter("did", deliveryId)
                .setParameter("statuses", List.of(DonationStatus.ASSIGNED, DonationStatus.IN_PROGRESS))
                .getResultList();
    }

    // ✅ Take order
    @PostMapping("/takeorder")
    @Transactional
    public String takeOrder(@RequestParam Integer orderId, HttpSession session) {
        Integer deliveryId = (Integer) session.getAttribute("deliveryId");
        if (deliveryId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Delivery person not logged in");
        }

        DeliveryPerson person = deliveryService.findById(deliveryId);

        FoodDonation donation = entityManager.find(FoodDonation.class, orderId);
        if (donation == null) return "Order not found.";
        if (donation.getDeliveryPerson() != null) return "This order is already assigned.";

        donation.setDeliveryPerson(person);
        donation.setStatus(DonationStatus.ASSIGNED);
        entityManager.merge(donation);

        manager.logActivity(
                person.getName(),
                "picked up order",
                donation.getFood() + " (" + donation.getQuantity() + ")",
                "assigned"
        );

        return "Order successfully assigned.";
    }

    // ✅ Summary
    @GetMapping("/summary")
    public Map<String, Object> getDeliverySummary(HttpSession session) {
        Integer deliveryId = (Integer) session.getAttribute("deliveryId");
        if (deliveryId == null) throw new RuntimeException("Delivery person not logged in");
        return deliveryService.getSummary(deliveryId);
    }

    // ✅ Verify email
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) {
        DeliveryPerson dp = deliveryService.authenticate(email);
        if (dp != null) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Email exists"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Email not found"));
        }
    }

    // ✅ Update password
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        DeliveryPerson dp = deliveryService.authenticate(email);
        if (dp == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Email not found"));
        }

        dp.setPassword(passwordEncoder.encode(newPassword));
        deliveryService.save(dp);

        manager.logActivity(dp.getName(), "updated password", "", "info");
        return ResponseEntity.ok(Collections.singletonMap("message", "Password updated successfully"));
    }

    // ✅ Update location
    @PostMapping("/{did}/location")
    public ResponseEntity<?> updateLocation(@PathVariable int did, @RequestBody LocationDto locationDto) {
        deliveryService.updateDeliveryPersonLocation(did, locationDto.getLatitude(), locationDto.getLongitude());
        return ResponseEntity.ok().build();
    }
}

