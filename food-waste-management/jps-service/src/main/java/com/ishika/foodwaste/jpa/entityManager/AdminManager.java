package com.ishika.foodwaste.jpa.entityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entity.NGOS;
import com.ishika.foodwaste.jpa.entity.UserFeedback;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
@Repository
public class AdminManager {
	   @PersistenceContext
	    private EntityManager entityManager;
	   @Autowired
	    private FoodDonationManager foodDonationRepo;

	    @Autowired
	    private UserFeedbackManager feedbackRepo;

	    @Autowired
	    private LoginManager loginRepo;
	    
	    @Autowired
	    private DeliveryPersonManager deliveryPersonRepo;
	    
	    @Autowired
	    private NGOManager ngosRepo;
	    
	    

	    // 1. Count total users
	    public long getTotalUsers() {
	        return loginRepo.count();
	    }

	    // 2. Count total feedbacks
	    public long getTotalFeedbacks() {
	        return feedbackRepo.count();
	    }

	    // 3. Count total donations
	    public long getTotalDonations() {
	        return foodDonationRepo.count();
	    }

	    // 4. Get unassigned donations by location
	    public List<FoodDonation> getUnassignedDonationsByLocation(String location) {
	        return foodDonationRepo.getDonationsByLocation(location);
	    }

	    // 5. Assign delivery person to a donation
	    @Transactional
	    public boolean assignDelivery(int donationId, int deliveryPersonId) {
	        FoodDonation donation = entityManager.find(FoodDonation.class, donationId);
	        DeliveryPerson deliveryPerson = entityManager.find(DeliveryPerson.class, deliveryPersonId);

	        if (donation == null || deliveryPerson == null) return false;
	        if (donation.getAssignedNGO() != null) return false;

	        donation.setAssignedNGO(null);  // ✅ set object
	        entityManager.merge(donation);

	        return true;
	    }


//	    public void assignDelivery(int donationId, int deliveryPersonId) {
//	        FoodDonation donation = foodDonationRepo.findById(donationId).orElseThrow();
//	        if (donation.getAssignedTo() == null) {
//	            donation.setAssignedTo(deliveryPersonId);
//	            foodDonationRepo.save(donation);
//	        } else {
//	            throw new RuntimeException("Already assigned");
//	        }
//	    }
	    
	    // 1. Dashboard Stats (users, feedbacks, donations)
	    public Map<String, Long> getDashboardStats() {
	        Map<String, Long> stats = new HashMap<>();

	        Long users = (Long) entityManager.createQuery("SELECT COUNT(l) FROM Login l").getSingleResult();
	        Long feedbacks = (Long) entityManager.createQuery("SELECT COUNT(f) FROM UserFeedback f").getSingleResult();
	        Long donations = (Long) entityManager.createQuery("SELECT COUNT(d) FROM FoodDonation d").getSingleResult();

	        stats.put("users", users);
	        stats.put("feedbacks", feedbacks);
	        stats.put("donations", donations);

	        return stats;
	    }
	    
	    public Admin findByEmail(String email) {
	    	TypedQuery<Admin> query = entityManager.createQuery(
	    		    "SELECT a FROM Admin a WHERE a.email = :email", Admin.class);
	    		query.setParameter("email", email);

	    		List<Admin> result = query.getResultList();
	    		if (!result.isEmpty()) {
	    		    return result.get(0);
	    		}
	    		return null;
	    }

	    // 2. Get unassigned food donations for a specific location
	    public List<FoodDonation> getUnassignedDonations(String location) {
	        TypedQuery<FoodDonation> query = entityManager.createNamedQuery("FoodDonation.findUnassignedByLocation", FoodDonation.class);
	        query.setParameter("location", location);
	        return query.getResultList();
	    }

	    // 3. Assign donation to ngos 
	    @Transactional
	    public String assignNGO(int donationId, int ngoId) {
	        Optional<FoodDonation> optionalDonation = foodDonationRepo.findById(donationId);
	        Optional<NGOS> optionalNGO = ngosRepo.findById(ngoId);

	        if (optionalDonation.isEmpty()) {
	            return "Donation not found";
	        }

	        if (optionalNGO.isEmpty()) {
	            return "NGO not found";
	        }

	        FoodDonation donation = optionalDonation.get();

	        if (donation.getAssignedNGO() != null) {
	            return "Donation already assigned to an NGO";
	        }

	        donation.setAssignedNGO(optionalNGO.get());
	        foodDonationRepo.save(donation);

	        return "NGO assigned successfully";
	    }

	    // 4. Get donations assigned to an admin (delivery person)
	    public List<FoodDonation> getAssignedDonations(int adminId) {
	        TypedQuery<FoodDonation> query = entityManager.createNamedQuery("FoodDonation.findByAssignedTo", FoodDonation.class);
	        query.setParameter("aid", adminId);
	        return query.getResultList();
	    }

	    // 5. Gender-wise user analytics
	    public Map<String, Long> getGenderAnalytics() {
	        Map<String, Long> result = new HashMap<>();

	        TypedQuery<Long> maleQuery = entityManager.createNamedQuery("Login.countByGender", Long.class);
	        maleQuery.setParameter("gender", "male");
	        result.put("male", maleQuery.getSingleResult());

	        TypedQuery<Long> femaleQuery = entityManager.createNamedQuery("Login.countByGender", Long.class);
	        femaleQuery.setParameter("gender", "female");
	        result.put("female", femaleQuery.getSingleResult());

	        return result;
	    }

	    // 6. Location-wise donation analytics
	    public Map<String, Long> getLocationAnalytics() {
	        Map<String, Long> result = new HashMap<>();
	        String[] locations = {"madurai", "chennai", "coimbatore"};

	        for (String loc : locations) {
	            TypedQuery<Long> query = entityManager.createNamedQuery("FoodDonation.countByLocation", Long.class);
	            query.setParameter("location", loc);
	            result.put(loc, query.getSingleResult());
	        }

	        return result;
	    }
	    
	    public Admin authenticate(String email, String password) {
	        TypedQuery<Admin> query = entityManager.createQuery(
	            "SELECT a FROM Admin a WHERE a.email = :email", Admin.class);
	        query.setParameter("email", email);

	        List<Admin> result = query.getResultList();
	        if (!result.isEmpty()) {
	            Admin admin = result.get(0);
	            if (BCrypt.checkpw(password, admin.getPassword())) {
	                return admin;
	            }
	        }
	        return null;
	    }
	    @Transactional
	    public boolean saveAdmin(Admin admin) {
	        try {
//	        	entityManager.getTransaction().begin();
	        	entityManager.persist(admin);
//	        	entityManager.getTransaction().commit();
	            return true;
	        } catch (Exception e) {
//	        	entityManager.getTransaction().rollback();
	            e.printStackTrace();
	            return false;
	        }
	    }

	    public List<FoodDonation> getDonationsByLocation(String location) {
	        TypedQuery<FoodDonation> query = entityManager.createQuery(
	            "SELECT f FROM FoodDonation f WHERE f.location = :location", FoodDonation.class);
	        query.setParameter("location", location);
	        return query.getResultList();
	    }
	    
	    public List<UserFeedback> getAllFeedbacks() {
	        TypedQuery<UserFeedback> query = entityManager.createQuery(
	            "SELECT f FROM UserFeedback f", UserFeedback.class);
	        return query.getResultList();
	    }
	}
