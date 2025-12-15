package com.ishika.foodwaste.web.controller;

import java.awt.PageAttributes.MediaType;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.Donor;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entityManager.FoodDonationManager;
import com.ishika.foodwaste.jpa.entityManager.RecentActivityManager;
import com.ishika.foodwaste.jpa.enums.DonationStatus;
import com.ishika.foodwaste.jpa.utils.DeliveryDto;
import com.ishika.foodwaste.jpa.utils.LocationDto;
import com.ishika.foodwaste.web.service.AdminService;
import com.ishika.foodwaste.web.service.DonorService;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/donor")
public class DonorController {
	   @Autowired
	    private DonorService donorService;
	    @Autowired
	    private PasswordEncoder passwordEncoder;
	    @Autowired
	    RecentActivityManager manager;
    

    @PostMapping("/register")
      public ResponseEntity<?> register(@RequestBody Donor donor) {

        System.out.println("🔵 [REGISTER] Request received");
        System.out.println("Email: " + donor.getEmail());

        // 🔐 Encode password before save
        donor.setPassword(passwordEncoder.encode(donor.getPassword()));

        boolean success = donorService.registerAdmin(donor);

        if (success) {
            System.out.println("✅ [REGISTER] Success for " + donor.getEmail());
            manager.logActivity(donor.getName(), "registered as donor", "", "new");
            return ResponseEntity.ok(Map.of("message", "Donor registered successfully"));
        }

        System.out.println("❌ [REGISTER] Failed for " + donor.getEmail());
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Donor registration failed"));
    }

    
    
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> body,
            HttpSession session) {

        System.out.println("🔵 [LOGIN] Request received");

        String email = body.get("email");
        String password = body.get("password");

        System.out.println("Email: " + email);

        Donor donor = donorService.authenticate(email);

        if (donor == null) {
            System.out.println("❌ [LOGIN] Donor not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email"));
        }

        System.out.println("🔐 Stored Password Hash: " + donor.getPassword());

        if (!passwordEncoder.matches(password, donor.getPassword())) {
            System.out.println("❌ [LOGIN] Password mismatch");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid password"));
        }

        // ✅ SESSION SET
        session.setAttribute("donorId", donor.getId());
        session.setMaxInactiveInterval(60 * 60); // 1 hour

        System.out.println("✅ [LOGIN] Login successful");
        System.out.println("🟢 Session ID: " + session.getId());

        manager.logActivity(donor.getName(), "login as donor", "", "info");

        return ResponseEntity.ok(Map.of("message", "Donor login successful"));
    }


    
    @PostMapping("/custom-logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            System.out.println("🔵 [LOGOUT] Session invalidated: " + session.getId());
            session.invalidate();
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        }

        System.out.println("⚠️ [LOGOUT] No session found");
        return ResponseEntity.badRequest()
                .body(Map.of("error", "No active session"));
    }


    @GetMapping("/month")
    public long getThisMonthCount(HttpSession session) {
        return donorService.getMonthlyDonationCount();
    }
    
    
//    @PostMapping("/add")
//    public ResponseEntity<String> addDonation(@RequestBody FoodDonation donation,HttpSession session) {
//        boolean added = donorService.addDonation(donation);
//        if (added)
//            return ResponseEntity.ok("Donation saved successfully!");
//        else
//            return ResponseEntity.status(500).body("Failed to save donation.");
//    }
    
//    @PostMapping(value = "/add", consumes = "multipart/form-data")
//    public ResponseEntity<String> addDonation(
//            @RequestParam("name") String name,
//            @RequestParam("email") String email,
//            @RequestParam("phoneno") String phoneno,
//            @RequestParam("food") String food,
//            @RequestParam("type") String type,
//            @RequestParam("category") String category,
//            @RequestParam("quantity") String quantity,
//            @RequestParam("price") Integer price,
//            @RequestParam("address") String address,
//            @RequestParam("location") String location,
//            @RequestParam("date") String date,  // Or convert to LocalDateTime if needed
//            @RequestParam("image") MultipartFile image,
//            HttpSession session
//    ) {
//        try {
//            FoodDonation donation = new FoodDonation();
//            donation.setName(name);
//            donation.setEmail(email);
//            donation.setPhoneno(phoneno);
//            donation.setFood(food);
//            donation.setType(type);
//            donation.setCategory(category);
//            donation.setQuantity(quantity);
//            donation.setPrice(price);
//            donation.setAddress(address);
//            donation.setLocation(location);
//            donation.setDate(OffsetDateTime.parse(date).toLocalDateTime());
//            donation.setImage(image.getBytes());
//
//            boolean added = donorService.addDonation(donation);
//
//            return added
//                    ? ResponseEntity.ok("Donation saved successfully!")
//                    : ResponseEntity.status(500).body("Failed to save donation.");
//        } catch (Exception e) {
//        	 e.printStackTrace();
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }

    
    
  @PostMapping(value = "/adddonate", consumes = "multipart/form-data")
public ResponseEntity<String> addDonation(
        @RequestParam("name") String name,
        @RequestParam("email") String email,
        @RequestParam("phoneno") String phoneno,
        @RequestParam("food") String food,
        @RequestParam("type") String type,
        @RequestParam("category") String category,
        @RequestParam("quantity") String quantity,
        @RequestParam("address") String address,
        @RequestParam("location") String location,
        @RequestParam("date") String date,
        @RequestParam("image") MultipartFile image,
        HttpSession session
) {
    try {
        // ✅ Get donor from session
		Integer donorId = (Integer) session.getAttribute("donorId");
		if (donorId == null) {
		    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not logged in");
		}

        FoodDonation donation = new FoodDonation();
        donation.setName(name);
        donation.setEmail(email);
        donation.setPhoneno(phoneno);
        donation.setFood(food);
        donation.setType(type);
        donation.setCategory(category);
        donation.setQuantity(quantity);
        donation.setAddress(address);
        donation.setLocation(location);
	       try {
	    donation.setDate(OffsetDateTime.parse(date).toLocalDateTime());
	} catch (Exception e) {
	    donation.setDate(LocalDateTime.now());
	}
        donation.setImage(image.getBytes());

        // ✅ Set donor & status
		  Donor donor = donorService.findById(donorId);
		donation.setDonor(donor);

        donation.setStatus(DonationStatus.PENDING);

        boolean added = donorService.addDonation(donation);
        if (added) {
            System.out.println("✅ [ADD DONATE] Donation saved: " + food);
            manager.logActivity(donor.getName(), "donated food", food + " (" + quantity + ")", "pending");
            return ResponseEntity.ok("Donation saved successfully!");
        } else {
            System.out.println("❌ [ADD DONATE] Failed to save donation");
            return ResponseEntity.status(500).body("Failed to save donation.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}

@PostMapping(value = "/addSell", consumes = "multipart/form-data")
public ResponseEntity<String> addSell(
        @RequestParam("name") String name,
        @RequestParam("email") String email,
        @RequestParam("phoneno") String phoneno,
        @RequestParam("food") String food,
        @RequestParam("type") String type,
        @RequestParam("category") String category,
        @RequestParam("quantity") String quantity,
        @RequestParam("price") Integer price,
        @RequestParam("address") String address,
        @RequestParam("location") String location,
        @RequestParam("date") String date,
        @RequestParam("image") MultipartFile image,
        HttpSession session
) {
    try {
		       Integer donorId = (Integer) session.getAttribute("donorId");
		if (donorId == null) {
		    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not logged in");
		}

        FoodDonation donation = new FoodDonation();
        donation.setName(name);
        donation.setEmail(email);
        donation.setPhoneno(phoneno);
        donation.setFood(food);
        donation.setType(type);
        donation.setCategory(category);
        donation.setQuantity(quantity);
        donation.setPrice(price);
        donation.setAddress(address);
        donation.setLocation(location);
     try {
    donation.setDate(OffsetDateTime.parse(date).toLocalDateTime());
} catch (Exception e) {
    donation.setDate(LocalDateTime.now());
}
        donation.setImage(image.getBytes());

    Donor donor = donorService.findById(donorId); // managed entity
     donation.setDonor(donor);;
        donation.setStatus(DonationStatus.PENDING);

        boolean added = donorService.addDonation(donation);
        if (added) {
            System.out.println("✅ [ADD SELL] Sell saved: " + food);
            manager.logActivity(donor.getName(), "sold food", food + " (" + quantity + ")", "pending");
            return ResponseEntity.ok("Sell saved successfully!");
        } else {
            System.out.println("❌ [ADD SELL] Failed to save sell");
            return ResponseEntity.status(500).body("Failed to save sell.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}

    
    @GetMapping("/deliveries")
public ResponseEntity<List<DeliveryDto>> getDonorDeliveries(HttpSession session) {
    Integer donorId = (Integer) session.getAttribute("donorId");
    if (donorId == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<FoodDonation> recentDonations =
            donorService.getRecentDonationsByDonor(donorId);

    List<DeliveryDto> deliveryDtos = new ArrayList<>();
    for (FoodDonation donation : recentDonations) {
        deliveryDtos.add(new DeliveryDto(donation.getFid(), donation.getName()));
    }

    return deliveryDtos.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(deliveryDtos);
}


@GetMapping("/deliveries/{fid}/location")
public ResponseEntity<LocationDto> getDeliveryLocation(
        @PathVariable String fid,
        HttpSession session) {

   Integer donorId = (Integer) session.getAttribute("donorId");
    if (donorId == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    int deliveryId;
    try {
        deliveryId = Integer.parseInt(fid);
    } catch (NumberFormatException e) {
        return ResponseEntity.badRequest().build();
    }

    LocationDto location = donorService.getLocationByDeliveryId(deliveryId);
    return location == null
            ? ResponseEntity.notFound().build()
            : ResponseEntity.ok(location);
}




//    @GetMapping("/getById/{id}")
//    public ResponseEntity<FoodDonation> getDonation(@PathVariable int id) {
//        FoodDonation donation = service.getDonationById(id);
//        if (donation != null)
//            return ResponseEntity.ok(donation);
//        else
//            return ResponseEntity.notFound().build();
//    }

//    @PutMapping("/updateById/{id}")
//    public ResponseEntity<String> updateDonation(@PathVariable int id, @RequestBody FoodDonation updated) {
//        boolean updatedStatus = service.updateDonation(id, updated);
//        if (updatedStatus)
//            return ResponseEntity.ok("Donation updated successfully.");
//        else
//            return ResponseEntity.status(404).body("Donation not found.");
//    }

    @DeleteMapping("/DeleteById/{id}")
    public ResponseEntity<String> deleteDonation(@PathVariable int id,HttpSession session) {
        boolean deleted = donorService.deleteDonationById(id);
        if (deleted)
            return ResponseEntity.ok("Donation deleted.");
        else
            return ResponseEntity.status(404).body("Donation not found.");
    }

    @GetMapping("/all")
    public List<FoodDonation> getAllDonations(HttpSession session) {
        return donorService.getAllDonations();
    }

    @GetMapping("/location/{location}")
    public List<FoodDonation> getByLocation(@PathVariable String location,HttpSession session) {
        return donorService.getDonationsByLocation(location);
    }

    @GetMapping("/count")
    public long getDonationCount(HttpSession session) {
        return donorService.getDonationCount();
    }
@GetMapping("/recent")
public List<FoodDonation> getRecentDonations(HttpSession session) {
   Integer donorId = (Integer) session.getAttribute("donorId");
    if (donorId == null) {
        return Collections.emptyList();
    }
    return donorService.getRecentDonationsByDonor(donorId);
}


    
    @GetMapping("/people-helped")
    public long getPeopleHelped(HttpSession session) {
        return donorService.getPeopleHelpedEstimate();  // Estimate logic from quantity
    }
    
    @GetMapping("/food-saved")
    public long getFoodSaved(HttpSession session) {
        return donorService.getTotalFoodSavedInKg();
    }
    
    @GetMapping("/donor/{donorId}")
    public List<FoodDonation> getByDonor(@PathVariable int donorId,HttpSession session) {
        return donorService.getDonationsByDonor(donorId);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) {
        Donor donor = donorService.authenticate(email);
        if (donor != null) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Email exists"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Email not found"));
        }
    }
    
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        Donor donor = donorService.authenticate(email);
        if (donor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Email not found"));
        }

        donor.setPassword(passwordEncoder.encode(newPassword));
        donorService.save(donor);

        manager.logActivity(donor.getName(), "updated password", "", "info");
        return ResponseEntity.ok(Collections.singletonMap("message", "Password updated successfully"));
    }



}
