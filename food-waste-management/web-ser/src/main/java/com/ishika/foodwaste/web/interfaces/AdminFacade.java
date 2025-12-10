package com.ishika.foodwaste.web.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entity.UserFeedback;
@Component
public interface AdminFacade {
	  Admin authenticate(String email);
	  boolean registerAdmin(Admin admin);
	    long getTotalUsers();
	    long getTotalFeedbacks();
	    long getTotalDonations();
	    Map<String, Long> getDashboardStats();
	    List<FoodDonation> getUnassignedDonations(String location);
//	    boolean assignDonation(int donationId, int deliveryPersonId);
	    List<FoodDonation> getAssignedDonations(int adminId);
	    Map<String, Long> getGenderAnalytics();
	    Map<String, Long> getLocationAnalytics();
	    List<FoodDonation> getDonationsByLocation(String location);
	    List<UserFeedback> getAllFeedbacks();
}
