package com.ishika.foodwaste.web.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.DeliveryLocation;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.Donor;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entityManager.DeliveryLocationManager;
import com.ishika.foodwaste.jpa.entityManager.DeliveryPersonManager;
import com.ishika.foodwaste.jpa.entityManager.DonorManager;
import com.ishika.foodwaste.jpa.entityManager.FoodDonationManager;
import com.ishika.foodwaste.jpa.utils.DeliveryDto;
import com.ishika.foodwaste.jpa.utils.LocationDto;
import com.ishika.foodwaste.web.interfaces.DonorFacade;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Service
public class DonorService implements DonorFacade{
	@Autowired
	DonorManager dm;
    @Autowired
    private FoodDonationManager repo;
	   @Autowired
	    private EntityManager em; 
	   @Autowired
	   DeliveryLocationManager deliveryLocationRepo;
	   @Autowired
	    private PasswordEncoder passwordEncoder;
	   @Autowired
	   DeliveryPersonManager dpi;
	   
	   
	   
	  @Override
	    public Donor authenticate(String email) {
	        Donor donor = dm.findByEmail(email);
           return donor;
	    }
	    @Override
	    public boolean registerAdmin(Donor donor) {
	    	donor.setPassword(passwordEncoder.encode(donor.getPassword()));
	        return dm.save(donor);
	    }
	    
//	    public long getMonthlyDonationCount() {
//	        return repo.getAllDonations().stream()
//	               .filter(d -> d.getDate().getMonth().equals(LocalDateTime.now().getMonth()))
//	               .count();
//	    }
	    

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

//	    @Override
//	    public boolean updateDonation(int id, FoodDonation updated) {
//	        FoodDonation existing = repo.findById(id);
//	        if (existing != null) {
//	            existing.setEmail(updated.getEmail());
//	            existing.setFood(updated.getFood());
//	            existing.setType(updated.getType());
//	            existing.setCategory(updated.getCategory());
//	            existing.setPhoneno(updated.getPhoneno());
//	            existing.setLocation(updated.getLocation());
//	            existing.setAddress(updated.getAddress());
//	            existing.setName(updated.getName());
//	            existing.setQuantity(updated.getQuantity());
	//
//	            repo.save(existing); // OR repo.update(existing) depending on your implementation
//	            return true;
//	        }
//	        return false;
//	    }

	    @Override
	    public boolean deleteDonationById(int id) {
	        if (repo.existsById(id)) {
	            repo.deleteDonationById(id);
	            return true;
	        }
	        return false;
	    }
//	    @Override
//	    public FoodDonation getDonationById(int id) {
//	        return repo.findById(id);
//	    }

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
	    @Override
	    public List<FoodDonation> getRecentDonations() {
	        List<FoodDonation> all = repo.getAllDonations();
	        return all.stream()
	                  .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
	                  .limit(5)
	                  .collect(Collectors.toList());
	    }
	    @Override
	    public long getMonthlyDonationCount() {
	        return repo.getAllDonations().stream()
	               .filter(d -> d.getDate().getMonth().equals(LocalDateTime.now().getMonth()))
	               .count();
	    }
	    @Override
	    public long getPeopleHelpedEstimate() {
	        return repo.getAllDonations().stream()
	               .mapToLong(d -> {
	                   String qty = d.getQuantity().replaceAll("[^\\d]", "");
	                   int q = qty.isEmpty() ? 0 : Integer.parseInt(qty);
	                   return q * 2; // assuming 1kg feeds 2 people
	               })
	               .sum();
	    }
	    @Override
	    public long getTotalFoodSavedInKg() {
	        return repo.getAllDonations().stream()
	               .mapToLong(d -> {
	                   String qty = d.getQuantity().replaceAll("[^\\d]", "");
	                   return qty.isEmpty() ? 0 : Integer.parseInt(qty);
	               }).sum();
	    }
	    @Override
	    public List<FoodDonation> getDonationsByDonor(int donorId) {
	        TypedQuery<FoodDonation> query = em.createQuery(
	            "SELECT f FROM FoodDonation f WHERE f.donor.id = :donorId", FoodDonation.class);
	        query.setParameter("donorId", donorId);
	        return query.getResultList();
	    }
	    
	    public List<FoodDonation> getRecentDonationsByDonor(Long donorId) {
	        return repo.getRecentDonationsByDonor(donorId);
	    }

	    @Transactional
	    public void save(Donor donor) {
	        repo.save1(donor);
	    }
	    
	    public List<DeliveryDto> getDeliveriesByDonorIds(Long donorId) {
	        List<FoodDonation> donations = repo.findDeliveriesByDonorId(donorId);
	        List<DeliveryDto> deliveryDtos = new ArrayList<>();
	        for (FoodDonation donation : donations) {
	            DeliveryDto dto = new DeliveryDto(donation.getFid(), donation.getName());
	            dto.setFid(donation.getFid());
	            dto.setName(donation.getName());
	            // aur fields set karo
	            deliveryDtos.add(dto);
	        }
	        return deliveryDtos;
	    }
	    

	    public LocationDto getLatLngFromAddress(String address) {
	        try {
	            String url = "https://nominatim.openstreetmap.org/search?q=" + 
	                         URLEncoder.encode(address, StandardCharsets.UTF_8) + 
	                         "&format=json&limit=1";

	            HttpClient client = HttpClient.newHttpClient();
	            HttpRequest request = HttpRequest.newBuilder()
	                                    .uri(URI.create(url))
	                                    .header("User-Agent", "Java App") // Nominatim ke liye user agent important hai
	                                    .build();

	            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	            JSONArray results = new JSONArray(response.body());
	            if (results.length() == 0) return null;

	            JSONObject location = results.getJSONObject(0);
	            double lat = location.getDouble("lat");
	            double lon = location.getDouble("lon");

	            return new LocationDto(lat, lon);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    
	    private static final Map<String, LocationDto> cityCoordinates = Map.of(
	    	    "Jaipur", new LocationDto(26.9124, 75.7873),
	    	    "Ajmer", new LocationDto(26.4499, 74.6399),
	    	    "Kota", new LocationDto(25.2138, 75.8648),
	    	    "Bhilwara", new LocationDto(25.3470, 74.6400),
	    	    "Sikar", new LocationDto(27.6094, 75.1399),
	    	    "Udaipur", new LocationDto(24.5854, 73.7125),
	    	    "Jodhpur", new LocationDto(26.2389, 73.0243)
	    	);


	    public LocationDto getLocationByDeliveryPersonId(int did) {
	        DeliveryLocation liveLoc = deliveryLocationRepo.findByDid(did);
	        if (liveLoc != null) {
	            return new LocationDto(liveLoc.getLatitude(), liveLoc.getLongitude());
	        }

	        DeliveryPerson dp = dpi.findById(did);
	        if (dp == null) return null;

	        if (dp.getLatitude() != null && dp.getLongitude() != null) {
	            return new LocationDto(dp.getLatitude(), dp.getLongitude());
	        }

	        if (dp.getCity() != null) {
	            LocationDto fallback = cityCoordinates.get(dp.getCity());
	            if (fallback != null) {
	                return fallback;
	            }
	        }

	        return null;
	    }



	
	    
	    public LocationDto getLocationByDeliveryId(Long fid) {
	        Optional<FoodDonation> deliveryOpt = repo.findById(fid);
	        if (deliveryOpt.isEmpty()) {
	            System.out.println("❌ No donation found with id: " + fid);
	            return null;
	        }

	        FoodDonation donation = deliveryOpt.get();
	        DeliveryPerson deliveryPerson = donation.getDeliveryPerson();

	        if (deliveryPerson != null) {
	            System.out.println("✅ Delivery person found: " + deliveryPerson.getName());
	            return getLocationByDeliveryPersonId(deliveryPerson.getDid());
	        }

	        // Fallback to location string
	        String locationName = donation.getLocation();
	        System.out.println("📍 Fallback location string: " + locationName);

	        if (locationName == null || locationName.isBlank()) return null;

	        // Try parsing as lat,lng string
	        if (locationName.contains(",")) {
	            String[] parts = locationName.split(",");
	            try {
	                double lat = Double.parseDouble(parts[0].trim());
	                double lng = Double.parseDouble(parts[1].trim());
	                return new LocationDto(lat, lng);
	            } catch (NumberFormatException e) {
	                System.out.println("❌ Location string is not in lat,lng format. Trying geocoding...");
	            }
	        }

	        // Fallback to geocoding using Nominatim if location is a textual address
	        return getLatLngFromAddress(locationName);
	    }




}
