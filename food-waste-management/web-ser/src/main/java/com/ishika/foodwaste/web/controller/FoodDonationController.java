package com.ishika.foodwaste.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.web.service.FoodDonationService;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/food-donations")
public class FoodDonationController {

    @Autowired
    private FoodDonationService service;

    @PostMapping("/add")
    public ResponseEntity<String> addDonation(@RequestBody FoodDonation donation) {
        boolean added = service.addDonation(donation);
        if (added)
            return ResponseEntity.ok("Donation saved successfully!");
        else
            return ResponseEntity.status(500).body("Failed to save donation.");
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
    public ResponseEntity<String> deleteDonation(@PathVariable int id) {
        boolean deleted = service.deleteDonationById(id);
        if (deleted)
            return ResponseEntity.ok("Donation deleted.");
        else
            return ResponseEntity.status(404).body("Donation not found.");
    }

    @GetMapping("/all")
    public List<FoodDonation> getAllDonations() {
        return service.getAllDonations();
    }

    @GetMapping("/location/{location}")
    public List<FoodDonation> getByLocation(@PathVariable String location) {
        return service.getDonationsByLocation(location);
    }

    @GetMapping("/count")
    public long getDonationCount() {
        return service.getDonationCount();
    }
    @GetMapping("/recent")
    public List<FoodDonation> getRecentDonations() {
        return service.getRecentDonations(); // Implement this in service and manager
    }

    @GetMapping("/month")
    public long getThisMonthCount() {
        return service.getMonthlyDonationCount();
    }
    
    @GetMapping("/people-helped")
    public long getPeopleHelped() {
        return service.getPeopleHelpedEstimate();  // Estimate logic from quantity
    }
    
    @GetMapping("/food-saved")
    public long getFoodSaved() {
        return service.getTotalFoodSavedInKg();
    }
    
    @GetMapping("/donor/{donorId}")
    public List<FoodDonation> getByDonor(@PathVariable int donorId) {
        return service.getDonationsByDonor(donorId);
    }




}
