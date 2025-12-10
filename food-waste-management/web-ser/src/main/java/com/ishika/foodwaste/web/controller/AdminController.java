package com.ishika.foodwaste.web.controller;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entity.RecentActivity;
import com.ishika.foodwaste.jpa.entity.UserFeedback;
import com.ishika.foodwaste.jpa.entityManager.RecentActivityManager;
import com.ishika.foodwaste.jpa.utils.TopDonorDTO;
import com.ishika.foodwaste.web.service.AdminService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    RecentActivityManager manager;
  
;
   

//    @PostMapping("/login")
//    public ResponseEntity<Admin> login(@RequestParam String email, @RequestParam String password) {
//        Admin admin = adminService.authenticate(email, password);
//        if (admin != null) return ResponseEntity.ok(admin);
//        return ResponseEntity.status(401).build();
//    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        Admin admin = adminService.authenticate(email);
        Map<String, String> response = new HashMap<>();
        if (admin == null) {
            return ResponseEntity.status(401).body(response);
        }

        if (passwordEncoder.matches(password, admin.getPassword())) {
            session.setAttribute("admin", admin); // store in session
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    
@PostMapping("/register")
public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
    boolean success = adminService.registerAdmin(admin);
    if (success) {
        return ResponseEntity.ok(Map.of("message", "Admin registered successfully"));
    } else {
        return ResponseEntity.badRequest().body(Map.of("message", "Admin registration failed"));
    }
}


    
    
    @PostMapping("/custom-logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // don’t create new session
        if (session != null) {
            session.invalidate(); // destroys session
            return ResponseEntity.ok("Logout successful.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No session found.");
        }
    }

    @GetMapping("/dashboard-stats")
    public Map<String, Long> getDashboardStats(HttpSession session) {
        return adminService.getDashboardStats();
    }

    @GetMapping("/donations/unassigned")
    public List<FoodDonation> getUnassignedDonations(@RequestParam("location") String location,HttpSession session) {
        return adminService.getUnassignedDonations(location);
    }

//    @PostMapping("/donations/assign")
//    public ResponseEntity<String> assignDonation(@RequestParam int donationId, @RequestParam int deliveryPersonId,HttpSession session) {
//        boolean assigned = adminService.assignDonation(donationId, deliveryPersonId);
//        if (assigned) return ResponseEntity.ok("Donation assigned.");
//        return ResponseEntity.badRequest().body("Assignment failed.");
//    }

    @GetMapping("/donations/assigned")
    public List<FoodDonation> getAssignedDonations(@RequestParam int adminId,HttpSession session) {
        return adminService.getAssignedDonations(adminId);
    }

    @GetMapping("/analytics/gender")
    public Map<String, Long> getGenderAnalytics(HttpSession session) {
        return adminService.getGenderAnalytics();
    }

    @GetMapping("/analytics/location")
    public Map<String, Long> getLocationAnalytics(HttpSession session) {
        return adminService.getLocationAnalytics();
    }

    @GetMapping("/feedbacks")
    public List<UserFeedback> getAllFeedbacks(HttpSession session) {
        return adminService.getAllFeedbacks();
    }
    
    @GetMapping("/total-donations")
    public ResponseEntity<?> getTotalDonations(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        long total = adminService.getTotalDonations();
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/active-users")
    public ResponseEntity<?> getActiveUsers(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Map<String, Long> counts = adminService.getActiveUserCounts();
        return ResponseEntity.ok(counts);
    }
    
    @GetMapping("/donation-status-counts")
    public ResponseEntity<?> getDonationStatusCounts(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Map<String, Long> statusCounts = adminService.getDonationStatusCounts();
        return ResponseEntity.ok(statusCounts);
    }
    
    @GetMapping("/food-saved")
    public ResponseEntity<Long> getTotalFoodSaved(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        long totalKg = adminService.getTotalFoodSavedInKg();
        return ResponseEntity.ok(totalKg);
    }

    @GetMapping("/delivery-summary")
    public ResponseEntity<?> getTotalDeliveries(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        long totalDeliveries = adminService.getTotalDeliveriesCount();
        Map<String, Long> response = Map.of("totalDeliveries", totalDeliveries);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent-activity")
    public List<RecentActivity> getRecentActivity(HttpSession session) {
    	  Admin admin = (Admin) session.getAttribute("admin");

        return adminService.findTop10ByOrderByTimestampDesc();
    }

    @GetMapping("/top-donors")
    public ResponseEntity<List<TopDonorDTO>> getTopDonorsThisMonth(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 instead of 403
        }
        return ResponseEntity.ok(adminService.getTopDonorsThisMonth());
    }

    @GetMapping("/getAll")
    public List<FoodDonation> getAll(@RequestParam(required = false) String search,HttpSession session) {
    	   Admin admin = (Admin) session.getAttribute("admin");
        if (search != null && !search.isEmpty()) {
            return adminService.searchDonations(search);
        }
        return adminService.getAllDonations();
    }

    // ✅ Export donations as CSV
    @GetMapping("/export")
    public void exportCSV(HttpServletResponse response) {
        try {
            String filename = "donations.csv";
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"");

            PrintWriter writer = response.getWriter();
            writer.println("ID,Name,Email,Food,Type,Category,Quantity,Location,Phone,Status,Date");

            for (FoodDonation d : adminService.getAllDonations()) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        d.getFid(), d.getName(), d.getEmail(), d.getFood(), d.getType(),
                        d.getCategory(), d.getQuantity(), d.getLocation(), d.getPhoneno(),
                        d.getStatus(), d.getDate());
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @GetMapping("/getAllDeliveryPersons")
    public ResponseEntity<List<Map<String, Object>>> getAllDeliveryPersons(HttpSession session) {
    	 Admin admin = (Admin) session.getAttribute("admin");
        List<DeliveryPerson> persons = adminService.getAllDeliveryPersons();

        List<Map<String, Object>> result = new ArrayList<>();
        for (DeliveryPerson person : persons) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", person.getDid());
            map.put("name", person.getName());
            map.put("email", person.getEmail());
            map.put("city", person.getCity());
//            map.put("tasks", adminService.getTodaysTaskCount(person));
            boolean isAvailable = (adminService.getTodaysTaskCount(person) == 0);
            map.put("isAvailable", isAvailable); 
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    // ✅ Search delivery personnel
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchDeliveryPersons(@RequestParam String keyword,HttpSession session) {
    	 Admin admin = (Admin) session.getAttribute("admin");
    	List<DeliveryPerson> persons = adminService.searchDeliveryPersons(keyword);

        List<Map<String, Object>> result = new ArrayList<>();
        for (DeliveryPerson person : persons) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", person.getDid());
            map.put("name", person.getName());
            map.put("email", person.getEmail());
            map.put("city", person.getCity());
            map.put("tasks", adminService.getTodaysTaskCount(person));
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<byte[]> downloadMonthlySummaryPDF(HttpSession session) {
    	 Admin admin = (Admin) session.getAttribute("admin");
        try {
            byte[] data = adminService.generateMonthlySummaryPDF();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=MonthlySummary.pdf")
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ngo-performance")
    public ResponseEntity<byte[]> downloadNGOPerformanceExcel(HttpSession session) {
    	 Admin admin = (Admin) session.getAttribute("admin");
        try {
            byte[] data = adminService.generateNGOPerformanceExcel();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=NGOPerformance.xlsx")
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/delivery-efficiency")
    public ResponseEntity<byte[]> downloadDeliveryEfficiencyCSV(HttpSession session) {
    	 Admin admin = (Admin) session.getAttribute("admin");
        byte[] data = adminService.generateDeliveryEfficiencyCSV();
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=DeliveryEfficiency.csv")
                .body(data);
    }
    
    @GetMapping("/count-by-location")
    public List<Object[]> getCountByLocation(HttpSession session) {
   	 Admin admin = (Admin) session.getAttribute("admin");
        return adminService.getCountByLocation();
    }

    @GetMapping("/status-distribution")
    public List<Object[]> getStatusDistribution(HttpSession session) {
   	 Admin admin = (Admin) session.getAttribute("admin");
        return adminService.getStatusDistribution(); // assume you implement this
    }

    @GetMapping("/top-donor")
    public List<TopDonorDTO> getTopDonors(HttpSession session) {
   	 Admin admin = (Admin) session.getAttribute("admin");
        return adminService.getTopDonors();
    }
    @GetMapping("/ngo-distribution")
    public ResponseEntity<List<Map<String, Object>>> getNGODistribution(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Map<String, Object>> distribution = adminService.getNGODistribution();
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyDonations(HttpSession session) {
    	 Admin admin = (Admin) session.getAttribute("admin");
        List<Map<String, Object>> monthlyDonations = adminService.getMonthlyDonations();
        return ResponseEntity.ok(monthlyDonations);
    }
    
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) {
        Admin a = adminService.authenticate(email);
        if (a != null) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Email exists"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Email not found"));
        }
    }
    
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        Admin a = adminService.authenticate(email);
        if (a == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Email not found"));
        }

        a.setPassword(passwordEncoder.encode(newPassword));
        adminService.save(a);

        manager.logActivity(a.getName(), "updated password", "", "info");
        return ResponseEntity.ok(Collections.singletonMap("message", "Password updated successfully"));
    }


}
