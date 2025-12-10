package com.ishika.foodwaste.web.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entityManager.FoodDonationManager;
import com.ishika.foodwaste.web.interfaces.FoodDonationFacade;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
@Service
public class FoodDonationService implements FoodDonationFacade{
	
    @Autowired
    private FoodDonationManager repo;
	 @PersistenceContext
	    private EntityManager em;

    @Override
    @Transactional
    public boolean addDonation(FoodDonation donation) {
        try {
            repo.save(donation);
            return true;
        } catch (Exception e) {
        	 System.err.println("Error saving donation: " + e.getMessage());
        	    e.printStackTrace();
        	    return false;
        }
    }

//    @Override
//    public boolean updateDonation(int id, FoodDonation updated) {
//        FoodDonation existing = repo.findById(id);
//        if (existing != null) {
//            existing.setEmail(updated.getEmail());
//            existing.setFood(updated.getFood());
//            existing.setType(updated.getType());
//            existing.setCategory(updated.getCategory());
//            existing.setPhoneno(updated.getPhoneno());
//            existing.setLocation(updated.getLocation());
//            existing.setAddress(updated.getAddress());
//            existing.setName(updated.getName());
//            existing.setQuantity(updated.getQuantity());
//
//            repo.save(existing); // OR repo.update(existing) depending on your implementation
//            return true;
//        }
//        return false;
//    }

    @Override
    public boolean deleteDonationById(int id) {
        if (repo.existsById(id)) {
            repo.deleteDonationById(id);
            return true;
        }
        return false;
    }
//    @Override
//    public FoodDonation getDonationById(int id) {
//        return repo.findById(id);
//    }

    @Override
    public List<FoodDonation> getAllDonations() {
        return repo.getAllDonations();
    }

    @Override
    public List<FoodDonation> getDonationsByLocation(String location) {
        return repo.getDonationsByLocation(location);
    }

    @Override
    public long getDonationCount() {
        return repo.count();
    }
    
    public List<FoodDonation> getRecentDonations() {
        List<FoodDonation> all = repo.getAllDonations();
        return all.stream()
                  .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                  .limit(5)
                  .collect(Collectors.toList());
    }
    
    public long getMonthlyDonationCount() {
        return repo.getAllDonations().stream()
               .filter(d -> d.getDate().getMonth().equals(LocalDateTime.now().getMonth()))
               .count();
    }
    
    public long getPeopleHelpedEstimate() {
        return repo.getAllDonations().stream()
               .mapToLong(d -> {
                   String qty = d.getQuantity().replaceAll("[^\\d]", "");
                   int q = qty.isEmpty() ? 0 : Integer.parseInt(qty);
                   return q * 2; // assuming 1kg feeds 2 people
               })
               .sum();
    }

    public long getTotalFoodSavedInKg() {
        return repo.getAllDonations().stream()
               .mapToLong(d -> {
                   String qty = d.getQuantity().replaceAll("[^\\d]", "");
                   return qty.isEmpty() ? 0 : Integer.parseInt(qty);
               }).sum();
    }

    public List<FoodDonation> getDonationsByDonor(int donorId) {
        TypedQuery<FoodDonation> query = em.createQuery(
            "SELECT f FROM FoodDonation f WHERE f.donor.id = :donorId", FoodDonation.class);
        query.setParameter("donorId", donorId);
        return query.getResultList();
    }


}
