package com.ishika.foodwaste.web.security;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.Donor;
import com.ishika.foodwaste.jpa.entity.NGOS;
import com.ishika.foodwaste.jpa.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ishika.foodwaste.web.service.DonorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ishika.foodwaste.web.service.AdminService;
import com.ishika.foodwaste.web.service.NGOService;
import com.ishika.foodwaste.web.service.DeliveryService;
import java.util.List;

import java.io.IOException;
import java.util.Collections;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private DonorService donorService;
     @Autowired
    private AdminService adminService;
    @Autowired
    private NGOService ngoService;
    @Autowired
    private DeliveryService deliveryService;
 @Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

    // 🔹 Bypass login/register endpoints
    String path = request.getRequestURI();
    if (path.contains("/login") || path.contains("/register") || path.contains("/verify-email") || path.contains("/update-password")) {
        filterChain.doFilter(request, response);
        return;
    }

    HttpSession session = request.getSession(false);

    if (session != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        // 🔹 Admin
            Integer adminId = (Integer) session.getAttribute("adminId");
            if (adminId != null) {
                Admin admin = adminService.findById(adminId);
                if (admin != null) {
                    SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                admin,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ADMIN"))
                        )
                    );
                }
            }
            


      // 🔹 Delivery Person
            Integer deliveryId = (Integer) session.getAttribute("deliveryId"); // ✅ use deliveryId like donorId
            if (deliveryId != null) {
                DeliveryPerson delivery = deliveryService.findById(deliveryId);
                if (delivery != null) {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    delivery,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("DELIVERY"))
                            )
                    );
                }
            }
        // 🔹 Donor
      
   Integer donorId = (Integer) session.getAttribute("donorId");
            if (donorId != null) {
                Donor donor = donorService.findById(donorId);
                if (donor != null) {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    donor,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("DONOR"))
                            )
                    );
                }
            }



        // 🔹 NGO
      Integer ngoId = (Integer) session.getAttribute("ngoId");
            if (ngoId != null) {
                NGOS ngo = ngoService.findById(ngoId);
                if (ngo != null) {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    ngo,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("NGO"))
                            )
                    );
                }
            }
        }

    filterChain.doFilter(request, response);
}
}
