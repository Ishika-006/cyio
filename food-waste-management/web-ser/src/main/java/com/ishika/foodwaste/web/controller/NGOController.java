package com.ishika.foodwaste.web.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.ishika.foodwaste.jpa.entity.Donor;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entity.NGOS;
import com.ishika.foodwaste.jpa.entityManager.RecentActivityManager;
import com.ishika.foodwaste.jpa.enums.DonationStatus;
import com.ishika.foodwaste.web.service.DonorService;
import com.ishika.foodwaste.web.service.NGOService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/NGO")
public class NGOController {
	 @PersistenceContext
	    private EntityManager em;
	 
	   @Autowired
	    private NGOService ngoService;
	    @Autowired
	    private PasswordEncoder passwordEncoder;
	    @Autowired
	    RecentActivityManager manager;
  
	  @PostMapping("/register")
    public ResponseEntity<?> registerNGO(@RequestBody NGOS ngo) {
		 ngo.setPassword(passwordEncoder.encode(ngo.getPassword()));
        boolean success = ngoService.registerNGO(ngo);
        if (success) {
             System.out.println("✅ [REGISTER] Success for " + ngo.getEmail());
            manager.logActivity(ngo.getName(), "registered as ngo", "", "new");
           return ResponseEntity.ok(Map.of("message", "ngo registered successfully"));
        }     
		System.out.println("❌ [REGISTER] Failed for " + ngo.getEmail());
        return ResponseEntity.badRequest()
                .body(Map.of("error", "ngo registration failed"));
    }
	    
	    
	    
	@PostMapping("/login")
public ResponseEntity<?> loginNGO(@RequestBody Map<String, String> body, HttpSession session) {

    System.out.println("🔵 [LOGIN] NGO request received");

    String email = body.get("email");
    String password = body.get("password");

    NGOS ngo = ngoService.authenticate(email);
    if (ngo == null) {
        System.out.println("❌ NGO not found");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "NGO not found"));
    }

    if (!passwordEncoder.matches(password, ngo.getPassword())) {
        System.out.println("❌ Password mismatch for NGO");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Incorrect password"));
    }

    // ✅ SESSION
    session.setAttribute("ngoId", ngo.getId());
    session.setMaxInactiveInterval(60 * 60); // 1 hour
    System.out.println("✅ NGO login successful. Session ID: " + session.getId());
    manager.logActivity(ngo.getName(), "login as NGO", "", "info");

    return ResponseEntity.ok(Map.of("message", "NGO login successful"));
}

   
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
   
   

  // ✅ Get Incoming Donations
    @GetMapping("/my-donations")
    public ResponseEntity<?> getIncomingDonations(HttpSession session) {
        Integer ngoId = (Integer) session.getAttribute("ngoId"); // ✅ use session id
        if (ngoId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NGO not logged in");
        }

        NGOS ngo = ngoService.findById(ngoId); // ✅ fetch from DB

        TypedQuery<FoodDonation> query = em.createQuery(
            "SELECT f FROM FoodDonation f WHERE f.status = 'PENDING' ORDER BY f.date DESC",
            FoodDonation.class
        );

        List<FoodDonation> result = query.getResultList();
        return ResponseEntity.ok(result);
    }

    // ✅ Update Donation Status
    @Transactional
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable int id, @RequestParam DonationStatus status, HttpSession session) {
        Integer ngoId = (Integer) session.getAttribute("ngoId");
        if (ngoId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NGO not logged in");
        }

        NGOS ngo = ngoService.findById(ngoId);

        FoodDonation donation = em.find(FoodDonation.class, id);
        if (donation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donation not found");
        }

        donation.setStatus(status);

        if (status == DonationStatus.ACCEPTED) {
            donation.setAssignedNGO(ngo);
        }

        em.merge(donation);
        manager.logActivity(
            ngo.getName(),
            "updated status of",
            donation.getFood() + " (" + donation.getQuantity() + ")",
            status.name().toLowerCase()
        );

        return ResponseEntity.ok("Status updated");
    }

    // ✅ Stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(HttpSession session) {
        Integer ngoId = (Integer) session.getAttribute("ngoId");
        if (ngoId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "NGO not logged in"));
        }

        Map<String, Object> stats = new HashMap<>();

        stats.put("pending", em.createQuery(
            "SELECT COUNT(f) FROM FoodDonation f WHERE f.status = 'PENDING'", Long.class
        ).getSingleResult());

        stats.put("accepted", em.createQuery(
            "SELECT COUNT(f) FROM FoodDonation f WHERE f.status = 'ACCEPTED'", Long.class
        ).getSingleResult());

        stats.put("totalToday", em.createQuery(
            "SELECT COUNT(f) FROM FoodDonation f WHERE DATE(f.date) = CURRENT_DATE", Long.class
        ).getSingleResult());

        stats.put("peopleServed", em.createQuery(
            "SELECT COALESCE(SUM(CAST(f.quantity AS integer)), 0) FROM FoodDonation f WHERE f.status = 'ACCEPTED'", Long.class
        ).getSingleResult());

        return ResponseEntity.ok(stats);
    }

    // ✅ Verify Email
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) {
        NGOS ngo = ngoService.authenticate(email);
        if (ngo != null) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Email exists"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Email not found"));
        }
    }

    // ✅ Update Password
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        NGOS ngo = ngoService.authenticate(email);
        if (ngo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Email not found"));
        }

        ngo.setPassword(passwordEncoder.encode(newPassword));
        ngoService.save(ngo);

        manager.logActivity(ngo.getName(), "updated password", "", "info");
        return ResponseEntity.ok(Collections.singletonMap("message", "Password updated successfully"));
    }
}
