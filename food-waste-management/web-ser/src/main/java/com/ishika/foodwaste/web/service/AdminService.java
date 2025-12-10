package com.ishika.foodwaste.web.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entity.NGOS;
import com.ishika.foodwaste.jpa.entity.RecentActivity;
import com.ishika.foodwaste.jpa.entity.UserFeedback;
import com.ishika.foodwaste.jpa.entityManager.AdminManager;
import com.ishika.foodwaste.jpa.entityManager.DeliveryPersonManager;
import com.ishika.foodwaste.jpa.entityManager.DonorManager;
import com.ishika.foodwaste.jpa.entityManager.FoodDonationManager;
import com.ishika.foodwaste.jpa.entityManager.LoginManager;
import com.ishika.foodwaste.jpa.entityManager.NGOManager;
import com.ishika.foodwaste.jpa.entityManager.RecentActivityManager;
import com.ishika.foodwaste.jpa.entityManager.UserFeedbackManager;
import com.ishika.foodwaste.jpa.enums.DonationStatus;
import com.ishika.foodwaste.jpa.utils.TopDonorDTO;
import com.ishika.foodwaste.web.interfaces.AdminFacade;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
@Service
public class AdminService implements AdminFacade {
	   @Autowired
	    private EntityManager entityManager;
	   
	   @Autowired
	    private PasswordEncoder passwordEncoder;

	    @Autowired
	    private FoodDonationManager foodDonationRepo;
	    @Autowired
	    private DonorManager DonorRepo;
	    @Autowired
	    private NGOManager NgoRepo;
	    @Autowired
	    private DeliveryPersonManager DeliveryRepo;

	    @Autowired
	    private UserFeedbackManager feedbackRepo;

	    @Autowired
	    private LoginManager loginRepo;

	    @Autowired
	    private AdminManager adminRepo;
	    @Autowired
	    private RecentActivityManager recentActivityRepo;
	    @PersistenceContext
           EntityManager em;
	    

	    @Override
	    public Admin authenticate(String email) {
	        Admin admin = adminRepo.findByEmail(email);
             return admin;
	    }
	    @Override
	    public boolean registerAdmin(Admin admin) {
	        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
	        return adminRepo.saveAdmin(admin);
	    }
	    @Override
	    public long getTotalUsers() {
	        return loginRepo.count();
	    }

	    @Override
	    public long getTotalFeedbacks() {
	        return feedbackRepo.count();
	    }

	    @Override
	    public long getTotalDonations() {
	        return foodDonationRepo.count();
	    }

	    @Override
	    public Map<String, Long> getDashboardStats() {
	        Map<String, Long> stats = new HashMap<>();
	        stats.put("users", getTotalUsers());
	        stats.put("feedbacks", getTotalFeedbacks());
	        stats.put("donations", getTotalDonations());
	        return stats;
	    }

	    @Override
	    public List<FoodDonation> getUnassignedDonations(String location) {
	        return foodDonationRepo.getDonationsByLocation(location);
	    }

//	    @Transactional
//	    @Override
//	    public boolean assignDonation(int donationId, int deliveryPersonId) {
//	        FoodDonation donation = entityManager.find(FoodDonation.class, donationId);
//	        if (donation == null || donation.getAssignedNGO() != null) return false;
//
//	        donation.setAssignedTo(deliveryPersonId);
//	        entityManager.merge(donation);
//	        return true;
//	    }

	    @Override
	    public List<FoodDonation> getAssignedDonations(int adminId) {
	        TypedQuery<FoodDonation> query = entityManager.createNamedQuery("FoodDonation.findByAssignedTo", FoodDonation.class);
	        query.setParameter("aid", adminId);
	        return query.getResultList();
	    }

	    @Override
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

	    @Override
	    public Map<String, Long> getLocationAnalytics() {
	        List<Object[]> results = entityManager
	            .createNamedQuery("FoodDonation.countByLocation", Object[].class)
	            .getResultList();

	        Map<String, Long> locationMap = new HashMap<>();
	        for (Object[] row : results) {
	            String location = (String) row[0];
	            Long count = (Long) row[1];
	            locationMap.put(location, count);
	        }
	        return locationMap;
	    }

//	    public Map<String, Long> getLocationAnalytics() {
//	        Map<String, Long> result = new HashMap<>();
//	        String[] locations = {"madurai", "chennai", "coimbatore"};
//
//	        for (String loc : locations) {
//	            TypedQuery<Long> query = entityManager.createNamedQuery("FoodDonation.countByLocation", Long.class);
//	            query.setParameter("location", loc);
//	            result.put(loc, query.getSingleResult());
//	        }
//	        return result;
//	    }

	    @Override
	    public List<FoodDonation> getDonationsByLocation(String location) {
	        return foodDonationRepo.getDonationsByLocation(location);
	    }

	    @Override
	    public List<UserFeedback> getAllFeedbacks() {
	        return feedbackRepo.getAllFeedbacks();
	    }
	    
	    public Map<String, Long> getActiveUserCounts() {
	        long donors = DonorRepo.count();
	        long ngos = NgoRepo.count();
	        long deliveryPersons = DeliveryRepo.count();

	        Map<String, Long> response = new HashMap<>();
	        response.put("donors", donors);
	        response.put("ngos", ngos);
	        response.put("deliveryPersons", deliveryPersons);
	        response.put("total", donors + ngos + deliveryPersons);

	        return response;
	    }
	    
	    
	    public Map<String, Long> getDonationStatusCounts() {
	        Map<String, Long> result = new HashMap<>();
	        try {
	            Long pendingCount = entityManager.createQuery(
	                    "SELECT COUNT(f) FROM FoodDonation f WHERE f.status = :status", Long.class)
	                    .setParameter("status", DonationStatus.PENDING)
	                    .getSingleResult();

	            Long acceptedCount = entityManager.createQuery(
	                    "SELECT COUNT(f) FROM FoodDonation f WHERE f.status = :status", Long.class)
	                    .setParameter("status", DonationStatus.ACCEPTED)
	                    .getSingleResult();

	            result.put("pending", pendingCount);
	            result.put("accepted", acceptedCount);

	        } catch (Exception e) {
	            e.printStackTrace();
	            result.put("pending", 0L);
	            result.put("accepted", 0L);
	        }
	        return result;
	    }
	    
	    
	    public long getTotalFoodSavedInKg() {
	        return foodDonationRepo.getAllDonations().stream()
	            .mapToLong(d -> {
	                String qty = d.getQuantity().replaceAll("[^\\d]", "");
	                return qty.isEmpty() ? 0 : Integer.parseInt(qty);
	            })
	            .sum();
	    }
	    
	    
	    public long getTotalDeliveriesCount() {
	        // Assuming your FoodDonation entity has a deliveryPerson field mapped
	        Long totalDeliveries = (Long) entityManager.createQuery(
	            "SELECT COUNT(fd) FROM FoodDonation fd WHERE fd.deliveryPerson IS NOT NULL"
	        ).getSingleResult();
	        
	        return totalDeliveries != null ? totalDeliveries : 0L;
	    }
	    
	    public List<RecentActivity> findTop10ByOrderByTimestampDesc() {
	        return recentActivityRepo.findTop10ByOrderByTimestampDesc();
	    }
	    
	    public List<TopDonorDTO> getTopDonorsThisMonth(){
	    	return foodDonationRepo.getTopDonorsThisMonth();
	    }

	 // ✅ Fetch all donations
	    public List<FoodDonation> getAllDonations() {
	        return foodDonationRepo.getAllDonations();
	    }

	    // ✅ Search donations by location (as example filter)
	    public List<FoodDonation> searchDonations(String query) {
	        String lowerQuery = query.toLowerCase();
	        return foodDonationRepo.getAllDonations().stream()
	            .filter(d ->
	                (d.getLocation() != null && d.getLocation().toLowerCase().contains(lowerQuery)) ||
	                (d.getName() != null && d.getName().toLowerCase().contains(lowerQuery)) ||
	                (d.getFood() != null && d.getFood().toLowerCase().contains(lowerQuery)) ||
	                (d.getCategory() != null && d.getCategory().toLowerCase().contains(lowerQuery)) ||
	                (d.getType() != null && d.getType().toLowerCase().contains(lowerQuery))
	            )
	            .collect(Collectors.toList());
	    }
	    

	    public List<DeliveryPerson> getAllDeliveryPersons() {
	        return em.createQuery("SELECT d FROM DeliveryPerson d", DeliveryPerson.class).getResultList();
	    }

	    public List<DeliveryPerson> searchDeliveryPersons(String keyword) {
	        return em.createQuery("SELECT d FROM DeliveryPerson d WHERE LOWER(d.name) LIKE :keyword OR LOWER(d.email) LIKE :keyword OR LOWER(d.city) LIKE :keyword", DeliveryPerson.class)
	                .setParameter("keyword", "%" + keyword.toLowerCase() + "%")
	                .getResultList();
	    }

	    public int getTodaysTaskCount(DeliveryPerson person) {
	        return foodDonationRepo.countByDeliveryPersonAndDate(person, LocalDate.now());
	    }
	    
	    public byte[] generateMonthlySummaryPDF() throws IOException {
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        PdfWriter writer = new PdfWriter(out);
	        PdfDocument pdfDoc = new PdfDocument(writer);
	        Document doc = new Document(pdfDoc);

	        List<FoodDonation> donations = foodDonationRepo.findAll().stream()
	                .filter(d -> d.getDate().getMonth() == LocalDate.now().getMonth())
	                .collect(Collectors.toList());

	        doc.add(new Paragraph("Monthly Summary Report"));
	        doc.add(new Paragraph("Total Donations: " + donations.size()));
	        for (FoodDonation d : donations) {
	            doc.add(new Paragraph(d.getName() + " - " + d.getFood() + " - " + d.getQuantity()));
	        }

	        doc.close();
	        return out.toByteArray();
	    }

	    // NGO Performance as Excel
	    public byte[] generateNGOPerformanceExcel() throws java.io.IOException {
	        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	            Sheet sheet = workbook.createSheet("NGO Performance");

	            Row header = sheet.createRow(0);
	            header.createCell(0).setCellValue("NGO Name");
	            header.createCell(1).setCellValue("Total Donations");

	            List<Object[]> performance = foodDonationRepo.countByNGO(); // ✅ update to use manager

	            int rowIdx = 1;
	            for (Object[] obj : performance) {
	                Row row = sheet.createRow(rowIdx++);
	                row.createCell(0).setCellValue(obj[0] != null ? obj[0].toString() : "Unknown NGO");
	                row.createCell(1).setCellValue(obj[1] != null ? ((Long) obj[1]) : 0);
	            }

	            workbook.write(out);
	            return out.toByteArray();
	        } catch (IOException e) {
	            e.printStackTrace();
	            return new byte[0]; // return empty array if error occurs
	        }
	    }

	    // Delivery Efficiency as CSV
	    public byte[] generateDeliveryEfficiencyCSV() {
	        StringBuilder sb = new StringBuilder();
	        sb.append("DeliveryPerson,DonationsCompleted\n");


	        List<DeliveryPerson> allPersons = DeliveryRepo.getAll();  // Replace with your method

	        // ✅ Step 2: Loop through each person and get count
	        for (DeliveryPerson person : allPersons) {
	            long count = foodDonationRepo.countByDeliveryPerson(person);  // Your existing method
	            sb.append(person.getName()).append(",").append(count).append("\n");
	        }

	        return sb.toString().getBytes(StandardCharsets.UTF_8);  // Recommended encoding
	    }
	    
	    public List<Object[]> getCountByLocation() {
	        return foodDonationRepo.countByLocation();
	    }

	  
	    public List<Object[]> getStatusDistribution() {
	        return foodDonationRepo.countByStatus(); // assume you implement this
	    }

	    public List<TopDonorDTO> getTopDonors() {
	        return foodDonationRepo.getTopDonorsThisMonth();
	    }

	 // Example DTO or Map return kar sakte ho
	    public List<Map<String, Object>> getNGODistribution() {
	        // Sample dummy data, real mein DB se fetch karo
	        List<Map<String, Object>> distribution = new ArrayList<>();
	        
	        Map<String, Object> ngo1 = new HashMap<>();
	        ngo1.put("ngoName", "NGO A");
	        ngo1.put("foodDistributedKg", 150L);
	        distribution.add(ngo1);
	        
	        Map<String, Object> ngo2 = new HashMap<>();
	        ngo2.put("ngoName", "NGO B");
	        ngo2.put("foodDistributedKg", 200L);
	        distribution.add(ngo2);
	        
	        // Real case: fetch from DB and aggregate food distributed per NGO
	        
	        return distribution;
	    }
	    
	    
	    public List<Map<String, Object>> getMonthlyDonations() {
	        // Directly call manager method and return the result
	        return foodDonationRepo.countDonationsGroupedByMonth();
	    }
	    
	    @Transactional
	    public void save(Admin a) {
	    	foodDonationRepo.save4(a);
	    }


}
