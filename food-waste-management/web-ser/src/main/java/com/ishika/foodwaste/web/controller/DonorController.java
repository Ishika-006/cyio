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

@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<?> registerAdmin(@RequestBody Donor donor) {
        boolean success = donorService.registerAdmin(donor);
        if (success) {
        	 manager.logActivity(donor.getName(), "registered as donor", "", "new");
            return ResponseEntity.ok(Collections.singletonMap("message", "donor registered successfully"));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "donor registration failed"));
        }
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        Donor donor = donorService.authenticate(email);
        if (donor == null) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "donor not found"));
        }

        if (passwordEncoder.matches(password, donor.getPassword())) {
            session.setAttribute("donor", donor); // store in session
            manager.logActivity(donor.getName(), "login as donor", "", "new");
            return ResponseEntity.ok(Collections.singletonMap("message", "donor login successful"));
        } else {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Incorrect password"));
        }
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

    
    
    @PostMapping(value = "/addSell", consumes = "multipart/form-data")
    public ResponseEntity<String> addDonationSell(
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
            // ✅ Get donor from session
            Donor donor = (Donor) session.getAttribute("donor");
            if (donor == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not logged in.");
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
            donation.setDate(OffsetDateTime.parse(date).toLocalDateTime());
            donation.setImage(image.getBytes());

            // ✅ Set donor
            donation.setDonor(donor);
            // **Set status to PENDING here**
            donation.setStatus(DonationStatus.PENDING);

            boolean added = donorService.addDonation(donation);
        

            if (added) {
                manager.logActivity(
                    donor.getName(),
                    "donated food",
                    food + " (" + quantity + ")",
                    "pending"
                );
                return ResponseEntity.ok("Donation saved successfully!");
            } else {
                return ResponseEntity.status(500).body("Failed to save donation.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    
    
    
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
            Donor donor = (Donor) session.getAttribute("donor");
            if (donor == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not logged in.");
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
            donation.setDate(OffsetDateTime.parse(date).toLocalDateTime());
            donation.setImage(image.getBytes());

            // ✅ Set donor
            donation.setDonor(donor);
            // Set donor

            // **Set status to PENDING here**
            donation.setStatus(DonationStatus.PENDING);


            boolean added = donorService.addDonation(donation);
            

             if (added) {
                manager.logActivity(
                        donor.getName(),
                        "donated food",
                        food + " (" + quantity + ")",
                        "pending"
                    );
                    return ResponseEntity.ok("Donation saved successfully!");
                } else {
                    return ResponseEntity.status(500).body("Failed to save donation.");
                }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/deliveries")
    public ResponseEntity<List<DeliveryDto>> getDonorDeliveries(HttpSession session) {
        Donor loggedInDonor = (Donor) session.getAttribute("donor");
        if (loggedInDonor == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 1. Recent donations fetch karo
        List<FoodDonation> recentDonations = donorService.getRecentDonationsByDonor(loggedInDonor.getId());

        // 2. Use DeliveryDto me convert karo
        List<DeliveryDto> deliveryDtos = new ArrayList<>();
        for (FoodDonation donation : recentDonations) {
            DeliveryDto dto = new DeliveryDto(donation.getFid(), donation.getName());
            deliveryDtos.add(dto);
        }

        return deliveryDtos.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(deliveryDtos);
    }


    @GetMapping("/deliveries/{fid}/location")
    public ResponseEntity<LocationDto> getDeliveryLocation(@PathVariable String fid,HttpSession session) {
        Donor loggedInDonor = (Donor) session.getAttribute("donor");
        Long deliveryId;
        try {
            deliveryId = Long.parseLong(fid);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
        LocationDto location = donorService.getLocationByDeliveryId(deliveryId);
        if (location == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(location);
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
        Donor donor = (Donor) session.getAttribute("donor");
        if (donor != null) {
            return donorService.getRecentDonationsByDonor(donor.getId());
        }
        return Collections.emptyList();
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
