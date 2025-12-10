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
@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
    	DeliveryPerson person = deliveryService.getByEmail(email);
    	 Map<String, String> response = new HashMap<>();
        if (person == null) {
            return ResponseEntity.status(401).body(response);
        }

        if (passwordEncoder.matches(password, person.getPassword())) {
            session.setAttribute("delivery", person); // store in session
            manager.logActivity(person.getName(), "login as delivery man", "", "new");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerdelivery(@RequestBody DeliveryPerson person) {
        String result = deliveryService.register(person);
        if (result.contains("successful")) {
        	  manager.logActivity(person.getName(), "registered as delivery man", "", "new");
            // Wrap in a JSON object
            return ResponseEntity.ok(Map.of("message", result));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", result));
        }
    }

    
    @PostMapping("/custom-logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // don’t create new session
        if (session != null) {
            session.invalidate(); // destroys session
            return ResponseEntity.ok("Logout successful.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No session found.");
        }
    }


    @GetMapping("/unassigned")
    public List<DeliveryRequestDTO> getUnassignedOrders(HttpSession session) {
        DeliveryPerson dp = (DeliveryPerson) session.getAttribute("delivery");

        if (dp == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Delivery person not logged in");
        }

        String deliveryPersonCity = dp.getCity();
        System.out.println("Delivery person city: " + deliveryPersonCity);

        List<FoodDonation> donations = entityManager.createQuery(
            "SELECT f FROM FoodDonation f " +
            "WHERE f.status = :status " +
            "AND f.deliveryPerson IS NULL " +
            "AND f.location LIKE :cityPattern", 
            FoodDonation.class)
            .setParameter("status", DonationStatus.ACCEPTED)
            .setParameter("cityPattern", deliveryPersonCity + "%")
            .getResultList();

        System.out.println("Donations fetched: " + donations.size());

        return donations.stream().map(f -> {
            System.out.println("Processing FoodDonation id: " + f.getFid());

            DeliveryRequestDTO dto = new DeliveryRequestDTO();
            dto.setFid(f.getFid());

            if (f.getAssignedNGO() != null) {
                System.out.println("Assigned NGO: " + f.getAssignedNGO());
                System.out.println("NGO Name: " + f.getAssignedNGO().getName());
                System.out.println("NGO Address: " + f.getAssignedNGO().getAddress());
                System.out.println("NGO City: " + f.getAssignedNGO().getCity());

                dto.setNgoName(f.getAssignedNGO().getName());
                dto.setNgoAddress(f.getAssignedNGO().getAddress());
                dto.setNgoCity(f.getAssignedNGO().getCity());
            } else {
                System.out.println("No NGO assigned for donation id: " + f.getFid());
            }

            // Use donation-level address/location instead of donor registration info
            System.out.println("Donor Name: " + f.getDonor().getName());
            System.out.println("Pickup Address (donation): " + f.getAddress());
            System.out.println("Pickup City (donation): " + f.getLocation());

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







//    @GetMapping("/unassigned")
//    public ResponseEntity<?> getUnassigned(HttpSession session) {
//        DeliveryPerson deliveryPerson = (DeliveryPerson) session.getAttribute("delivery");
//        if (deliveryPerson == null) {
//            return ResponseEntity.status(403).body("Unauthorized");
//        }
//
//        String city = deliveryPerson.getCity();  // assuming getCity() method exists in DeliveryPerson class
//        return ResponseEntity.ok(deliveryService.getUnassignedOrders(city));
//    }

    // ✅ My orders
    @GetMapping("/myorders")
    public List<FoodDonation> getMyOrders(HttpSession session) {
        DeliveryPerson dp = (DeliveryPerson) session.getAttribute("delivery");
        if (dp == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Delivery person not logged in");
        }
        int did = dp.getDid();

        return entityManager.createQuery(
            "SELECT f FROM FoodDonation f WHERE f.deliveryPerson.did = :did AND f.status IN (:statuses)",
            FoodDonation.class)
            .setParameter("did", did)
            .setParameter("statuses", List.of(DonationStatus.ASSIGNED, DonationStatus.IN_PROGRESS))
            .getResultList();
    }



//    public ResponseEntity<?> getMyOrders(HttpSession session) {
//    	
//    	DeliveryPerson deliveryPerson = (DeliveryPerson) session.getAttribute("delivery");
//        if (deliveryPerson == null) {
//            return ResponseEntity.status(403).body("Unauthorized");
//        }
//
//        int did = deliveryPerson.getDid();
//        return ResponseEntity.ok(deliveryService.getMyOrders(did));
//    	
//    }

    // ✅ Take order
    @PostMapping("/takeorder")
    @Transactional
    public String takeOrder(@RequestParam Integer orderId, HttpSession session) {
        DeliveryPerson person = (DeliveryPerson) session.getAttribute("delivery");
        if (person == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Delivery person not logged in");
        }

        if (orderId == null) {
            return "Missing orderId.";
        }

        FoodDonation donation = entityManager.find(FoodDonation.class, orderId);

        if (donation == null) {
            return "Order not found.";
        }

        if (donation.getDeliveryPerson() != null) {
            return "This order is already assigned.";
        }

        donation.setDeliveryPerson(person);
        donation.setStatus(DonationStatus.ASSIGNED);
        entityManager.merge(donation);
        
        manager.logActivity(
        	    person.getName(),                  // Who
        	    "picked up order",                // What
        	    donation.getFood() + " (" + donation.getQuantity() + ")",  // Which order
        	    "assigned"                        // Status
        	);

        return "Order successfully assigned.";
    }



//    public ResponseEntity<?> takeOrder(@RequestBody Map<String, Integer> data, HttpSession session) {
//        DeliveryPerson deliveryPerson = (DeliveryPerson) session.getAttribute("delivery");
//        if (deliveryPerson == null) {
//            return ResponseEntity.status(403).body("Unauthorized");
//        }
//
//        int did = deliveryPerson.getDid();
//        String result = deliveryService.takeOrder(data.get("orderId"), did);
//        return result.contains("successfully")
//            ? ResponseEntity.ok(result)
//            : ResponseEntity.badRequest().body(result);
//    }
    
    
    @GetMapping("/summary")
    public Map<String, Object> getDeliverySummary(HttpSession session) {
        DeliveryPerson person = (DeliveryPerson) session.getAttribute("delivery");
        if (person == null) {
            throw new RuntimeException("User not logged in");
        }
        int deliveryPersonId = person.getDid();
        return deliveryService.getSummary(deliveryPersonId);
    }
    
    
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) {
        DeliveryPerson dp = deliveryService.authenticate(email);
        if (dp != null) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Email exists"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Email not found"));
        }
    }
    
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        DeliveryPerson dp = deliveryService.authenticate(email);
        if (dp == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Email not found"));
        }

        dp.setPassword(passwordEncoder.encode(newPassword));
        deliveryService.save(dp);

        manager.logActivity(dp.getName(), "updated password", "", "info");
        return ResponseEntity.ok(Collections.singletonMap("message", "Password updated successfully"));
    }
    
    @PostMapping("/{did}/location")
    public ResponseEntity<?> updateLocation(@PathVariable int did, @RequestBody LocationDto locationDto) {
    	deliveryService.updateDeliveryPersonLocation(did, locationDto.getLatitude(), locationDto.getLongitude());
        return ResponseEntity.ok().build();
    }


}
