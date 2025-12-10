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
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
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
	    public ResponseEntity<?> registerAdmin(@RequestBody NGOS ngo) {
	        boolean success = ngoService.registerNGO(ngo);

	        Map<String, Object> response = new HashMap<>();
	        if (success) {
	            response.put("message", "NGO registered successfully");
	            manager.logActivity(ngo.getName(), "registered as ngo", "", "new");
	            return ResponseEntity.ok(response);
	        } else {
	            response.put("message", "NGO registration failed");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }
	    }
	    
	    
	    
	    @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
	        NGOS ngo = ngoService.authenticate(email);
	        
	        Map<String, String> response = new HashMap<>();
	        
	        if (ngo == null) {
	            response.put("error", "NGO not found");
	            return ResponseEntity.status(401).body(response); // ❌ was returning plain string
	        }

	        if (passwordEncoder.matches(password, ngo.getPassword())) {
	            session.setAttribute("ngo", ngo);

	            // ✅ Set Spring Security authentication manually
	            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
	                ngo.getEmail(), null, List.of(new SimpleGrantedAuthority("NGO"))
	            );
	            SecurityContextHolder.getContext().setAuthentication(auth);

	            response.put("message", "NGO login successful");
	            manager.logActivity(ngo.getName(), "login as ngo", "", "new");
	            return ResponseEntity.ok(response);
	        }
               else {
	            response.put("error", "Incorrect password");
	            return ResponseEntity.status(401).body(response); // ✅ return JSON
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
   
   

   @GetMapping("/my-donations")
   public ResponseEntity<?> getIncomingDonations(HttpSession session) {
       NGOS ngo = (NGOS) session.getAttribute("ngo");
       if (ngo == null) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NGO not logged in");
       }

       // ✅ Show all pending donations instead of filtering by donorId
       TypedQuery<FoodDonation> query = em.createQuery(
           "SELECT f FROM FoodDonation f WHERE f.status = 'PENDING' ORDER BY f.date DESC", 
           FoodDonation.class
       );

       List<FoodDonation> result = query.getResultList();
       return ResponseEntity.ok(result);
   }


//   @PutMapping("/{id}/status")
//   public ResponseEntity<?> updateStatus(@PathVariable int id, @RequestParam DonationStatus status,HttpSession session) {
//       FoodDonation donation = em.find(FoodDonation.class, id);
//       if (donation == null) {
//           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donation not found");
//       }
//
//       donation.setStatus(status);
//       em.merge(donation);
//       return ResponseEntity.ok("Status updated");
//   }
   
   @Transactional
   @PutMapping("/{id}/status")
   public ResponseEntity<?> updateStatus(@PathVariable int id, @RequestParam DonationStatus status, HttpSession session) {
       NGOS ngo = (NGOS) session.getAttribute("ngo");
       if (ngo == null) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NGO not logged in");
       }

       FoodDonation donation = em.find(FoodDonation.class, id);
       if (donation == null) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donation not found");
       }

       donation.setStatus(status);
       
       // Assign NGO only if accepting the donation
       if (status == DonationStatus.ACCEPTED) {
           donation.setAssignedNGO(ngo);  // Make sure setter exists
       }
       
       em.merge(donation);
       manager.logActivity(
    	        ngo.getName(),                                     // Name of NGO
    	        "updated status of",                               // Action
    	        donation.getFood() + " (" + donation.getQuantity() + ")", // Target
    	        status.name().toLowerCase()                        // Status
    	    );
       return ResponseEntity.ok("Status updated");
   }


   @GetMapping("/stats")
   public ResponseEntity<Map<String, Object>> getStats(HttpSession session) {
       NGOS ngo = (NGOS) session.getAttribute("ngo");
       if (ngo == null) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "NGO not logged in"));
       }

       Map<String, Object> stats = new HashMap<>();
       
       stats.put("pending", em.createQuery(
           "SELECT COUNT(f) FROM FoodDonation f WHERE f.status = 'PENDING'", Long.class)
           .getSingleResult());

       stats.put("accepted", em.createQuery(
           "SELECT COUNT(f) FROM FoodDonation f WHERE f.status = 'ACCEPTED'", Long.class)
           .getSingleResult());

       stats.put("totalToday", em.createQuery(
           "SELECT COUNT(f) FROM FoodDonation f WHERE DATE(f.date) = CURRENT_DATE", Long.class)
           .getSingleResult());

       stats.put("peopleServed", em.createQuery(
           "SELECT COALESCE(SUM(CAST(f.quantity AS integer)), 0) FROM FoodDonation f WHERE f.status = 'ACCEPTED'", Long.class)
           .getSingleResult());

       return ResponseEntity.ok(stats);
   }
   
   @GetMapping("/verify-email")
   public ResponseEntity<?> verifyEmail(@RequestParam String email) {
       NGOS ngo = ngoService.authenticate(email);
       if (ngo != null) {
           return ResponseEntity.ok(Collections.singletonMap("message", "Email exists"));
       } else {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Email not found"));
       }
   }
   
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
