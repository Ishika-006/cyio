package com.ishika.foodwaste.web.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.Donor;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entity.NGOS;
import com.ishika.foodwaste.jpa.entityManager.DeliveryPersonManager;
import com.ishika.foodwaste.jpa.entityManager.FoodDonationManager;
import com.ishika.foodwaste.web.interfaces.DeliveryFacade;

import jakarta.transaction.Transactional;
@Service
public class DeliveryService implements DeliveryFacade {
	

	    @Autowired
	    private DeliveryPersonManager deliveryManager;
	    @Autowired
	    private FoodDonationManager donationrepo;

	    @Override
	    public String login(String email, String password) {
	        DeliveryPerson person = deliveryManager.findByEmail(email);
	        if (person == null) {
	            return "Account doesn't exist";
	        }

	        if (!BCrypt.checkpw(password, person.getPassword())) {
	            return "Invalid password";
	        }

	        return "Login successful";
	    }

	    @Override
	    public String register(DeliveryPerson person) {
	        if (deliveryManager.findByEmail(person.getEmail()) != null) {
	            return "Account already exists";
	        }

	        person.setPassword(BCrypt.hashpw(person.getPassword(), BCrypt.gensalt()));
	        deliveryManager.save(person);
	        return "Registration successful";
	    }

	    @Override
	    public List<FoodDonation> getUnassignedOrders(String city) {
	        return deliveryManager.getUnassignedOrders(city);
	    }

	    @Override
	    public List<FoodDonation> getMyOrders(int deliveryPersonId) {
	        return deliveryManager.getOrdersByDeliveryPerson(deliveryPersonId);
	    }

	    @Override
	    public String takeOrder(int orderId, int deliveryPersonId) {
	        boolean result = deliveryManager.takeOrder(orderId, deliveryPersonId);
	        return result ? "Order taken successfully" : "Order already taken";
	    }

	    public DeliveryPerson getByEmail(String email) {
	        return deliveryManager.findByEmail(email);
	    }
	    
	    public Map<String, Object> getSummary(int deliveryPersonId) {
	        Map<String, Object> summary = new HashMap<>();

	        Optional<DeliveryPerson> optionalPerson = Optional.ofNullable(deliveryManager.findById(deliveryPersonId));

	        if (!optionalPerson.isPresent()) {
	            summary.put("error", "Delivery person not found");
	            summary.put("totalDeliveries", 0);
	            summary.put("weeklyDeliveries", 0);
	            return summary;
	        }

	        DeliveryPerson person = optionalPerson.get();

	        int totalDeliveries = (int) donationrepo.countByDeliveryPerson(person);
	        int weeklyDeliveries = (int) donationrepo.countByDeliveryPersonInLastWeek(person, LocalDateTime.now().minusDays(7));

	        summary.put("totalDeliveries", totalDeliveries);
	        summary.put("weeklyDeliveries", weeklyDeliveries);

	        return summary;
	    }
	    
	    public DeliveryPerson authenticate(String email) {
		  DeliveryPerson dp = deliveryManager.findByEmail(email);
           return dp;
	    }
	    @Transactional
	    public void save(DeliveryPerson dp) {
	    	donationrepo.save3(dp);
	    }
	    
	    public void updateDeliveryPersonLocation(int did, double lat, double lng) {
	        DeliveryPerson dp = deliveryManager.findById(did);

	        if (dp != null) {
	            dp.setLatitude(lat);
	            dp.setLongitude(lng);
	            deliveryManager.save(dp);
	        } else {
	            System.out.println("Delivery person with id " + did + " not found.");
	        }
	    }



}
