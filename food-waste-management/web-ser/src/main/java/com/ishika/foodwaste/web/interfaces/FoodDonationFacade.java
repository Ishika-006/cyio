package com.ishika.foodwaste.web.interfaces;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ishika.foodwaste.jpa.entity.FoodDonation;
@Component
public interface FoodDonationFacade {
	  boolean addDonation(FoodDonation donation);
//	    boolean updateDonation(int id, FoodDonation updated);
	    boolean deleteDonationById(int id);
//	    FoodDonation getDonationById(int id);
	    List<FoodDonation> getAllDonations();
	    List<FoodDonation> getDonationsByLocation(String location);
	    long getDonationCount();
}
