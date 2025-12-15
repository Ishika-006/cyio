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
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    RecentActivityManager manager;
    @Autowired
private PasswordEncoder passwordEncoder;

   

//    @PostMapping("/login")
//    public ResponseEntity<Admin> login(@RequestParam String email, @RequestParam String password) {
//        Admin admin = adminService.authenticate(email, password);
//        if (admin != null) return ResponseEntity.ok(admin);
//        return ResponseEntity.status(401).build();
//    }
    
@PostMapping("/login")
public ResponseEntity<?> login(
        @RequestBody Map<String, String> body,
        HttpSession session) {

    System.out.println("🔵 [ADMIN LOGIN] Request received");

    String email = body.get("email");
    String password = body.get("password");

    Admin admin = adminService.authenticate(email);

    if (admin == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid email"));
    }

    if (!passwordEncoder.matches(password, admin.getPassword())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid password"));
    }

    // ✅ SAME AS DONOR
    session.setAttribute("adminId", admin.getAid());
    session.setMaxInactiveInterval(60 * 60);

    manager.logActivity(admin.getName(), "login as admin", "", "info");

    System.out.println("🟢 Admin Session ID: " + session.getId());

    return ResponseEntity.ok(Map.of("message", "Admin login successful"));
}


    
@PostMapping("/register")
public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {

    boolean success = adminService.registerAdmin(admin);

    if (success) {
        manager.logActivity(admin.getName(), "registered as admin", "", "new");
        return ResponseEntity.ok(Map.of("message", "Admin registered successfully"));
    }
    return ResponseEntity.badRequest()
            .body(Map.of("message", "Admin registration failed"));
}



    
    
@PostMapping("/custom-logout")
public ResponseEntity<String> logout(HttpServletRequest request) {

    HttpSession session = request.getSession(false);
    if (session != null) {
        System.out.println("🔵 [ADMIN LOGOUT] Session invalidated: " + session.getId());
        session.invalidate();
        return ResponseEntity.ok("Logout successful.");
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("No session found.");
}


 // ================= DASHBOARD =================
    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> dashboardStats(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // ================= DONATIONS =================
    @GetMapping("/donations/unassigned")
    public ResponseEntity<?> unassigned(@RequestParam String location, HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getUnassignedDonations(location));
    }

    @GetMapping("/donations/assigned")
    public ResponseEntity<?> assigned(HttpSession session) {
        Integer adminId = getAdminId(session);
        if (adminId == null) return unauthorized();
        return ResponseEntity.ok(adminService.getAssignedDonations(adminId));
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(@RequestParam(required = false) String search, HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(
                (search != null && !search.isEmpty())
                        ? adminService.searchDonations(search)
                        : adminService.getAllDonations()
        );
    }

    // ================= ANALYTICS =================
    @GetMapping("/analytics/gender")
    public ResponseEntity<?> gender(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getGenderAnalytics());
    }

    @GetMapping("/analytics/location")
    public ResponseEntity<?> location(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getLocationAnalytics());
    }

    @GetMapping("/donation-status-counts")
    public ResponseEntity<?> statusCounts(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getDonationStatusCounts());
    }

    // ================= STATS =================
    @GetMapping("/total-donations")
    public ResponseEntity<?> totalDonations(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getTotalDonations());
    }

    @GetMapping("/food-saved")
    public ResponseEntity<?> foodSaved(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getTotalFoodSavedInKg());
    }

    @GetMapping("/active-users")
    public ResponseEntity<?> activeUsers(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getActiveUserCounts());
    }

    @GetMapping("/delivery-summary")
    public ResponseEntity<?> deliverySummary(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(Map.of("totalDeliveries",
                adminService.getTotalDeliveriesCount()));
    }

    // ================= RECENT ACTIVITY =================
    @GetMapping("/recent-activity")
    public ResponseEntity<?> recentActivity(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.findTop10ByOrderByTimestampDesc());
    }

    // ================= TOP DONORS =================
    @GetMapping("/top-donors")
    public ResponseEntity<?> topDonorsMonth(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getTopDonorsThisMonth());
    }

    @GetMapping("/top-donor")
    public ResponseEntity<?> topDonors(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(adminService.getTopDonors());
    }

    // ================= DELIVERY PERSON =================
    @GetMapping("/getAllDeliveryPersons")
    public ResponseEntity<?> deliveryPersons(HttpSession session) {
        if (!isAdminLoggedIn(session)) return unauthorized();

        List<Map<String, Object>> list = new ArrayList<>();
        for (DeliveryPerson p : adminService.getAllDeliveryPersons()) {
            list.add(Map.of(
                    "id", p.getDid(),
                    "name", p.getName(),
                    "email", p.getEmail(),
                    "city", p.getCity(),
                    "isAvailable", adminService.getTodaysTaskCount(p) == 0
            ));
        }
        return ResponseEntity.ok(list);
    }

    // ================= FILE EXPORT =================
    @GetMapping("/export")
    public void exportCSV(HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + URLEncoder.encode("donations.csv", "UTF-8") + "\"");

            PrintWriter writer = response.getWriter();
            writer.println("ID,Name,Email,Food,Quantity,Location,Status");

            for (FoodDonation d : adminService.getAllDonations()) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s%n",
                        d.getFid(), d.getName(), d.getEmail(),
                        d.getFood(), d.getQuantity(),
                        d.getLocation(), d.getStatus());
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= PASSWORD =================
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> req) {
        Admin admin = adminService.authenticate(req.get("email"));
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        admin.setPassword(passwordEncoder.encode(req.get("newPassword")));
        adminService.save(admin);
        manager.logActivity(admin.getName(), "updated password", "", "info");
        return ResponseEntity.ok(Map.of("message", "Password updated"));
    }

    // ================= UTILS =================
    private boolean isAdminLoggedIn(HttpSession session) {
        return session.getAttribute("adminId") != null;
    }

    private Integer getAdminId(HttpSession session) {
        return (Integer) session.getAttribute("adminId");
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized"));
    }
}
