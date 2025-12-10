package com.ishika.foodwaste.web.interfaces;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.Donor;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
@Component
public interface DonorFacade {
//	   void save(Donor donor);
//	    Donor findByEmail(String email);
//	    List<Donor> getAllDonors();
//	    Donor findById(int id);
	    Donor authenticate(String email);
	    boolean registerAdmin(Donor donor);
	    public boolean addDonation(FoodDonation donation);
	    public boolean deleteDonationById(int id);
	    public List<FoodDonation> getAllDonations();
	    public List<FoodDonation> getDonationsByLocation(String location);
	    public long getDonationCount();
	    public List<FoodDonation> getRecentDonations();
	    public long getMonthlyDonationCount();
	    public long getPeopleHelpedEstimate();
	    public long getTotalFoodSavedInKg();
	    public List<FoodDonation> getDonationsByDonor(int donorId);
}
