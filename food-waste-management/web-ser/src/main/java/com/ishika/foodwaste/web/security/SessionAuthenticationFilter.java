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

import java.io.IOException;
import java.util.Collections;

public class SessionAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 🔹 Admin
            Object adminObj = session.getAttribute("admin");
            if (adminObj instanceof Admin admin) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                admin,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ADMIN"))
                        )
                );
            }

            // 🔹 Delivery Person
            Object deliveryObj = session.getAttribute("delivery");
            if (deliveryObj instanceof DeliveryPerson delivery) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                delivery,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("DELIVERY"))
                        )
                );
            }

            // 🔹 Donor
            Object donorObj = session.getAttribute("donor");
            if (donorObj instanceof Donor donor && donor.getRole() == Role.DONOR) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                donor,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("DONOR"))
                        )
                );
            }

            // (Optional) 🔹 NGO
            Object ngoObj = session.getAttribute("ngo");
            if (ngoObj instanceof NGOS ngo && ngo.getRole() == Role.NGO) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                ngo,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("NGO"))
                        )
                );
            }
        }

        filterChain.doFilter(request, response);
    }
}
