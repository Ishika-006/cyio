package com.ishika.foodwaste.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(auth -> auth
            		 .requestMatchers("/admin/login", "/admin/register","/admin/logout").permitAll()
            	        .requestMatchers("/delivery/login", "/delivery/register", "/delivery/custom-logout").permitAll()
            	        .requestMatchers("/donor/login", "/donor/register","/donor/custom-logout").permitAll()
            	        .requestMatchers("/donor/add").permitAll()
            	        .requestMatchers("/donor/verify-email").permitAll()
            	        .requestMatchers("/donor/update-password").permitAll()
               	        .requestMatchers("/admin/verify-email").permitAll()
            	        .requestMatchers("/admin/update-password").permitAll()
            	        .requestMatchers("/NGO/verify-email").permitAll()
            	        .requestMatchers("/delivery/update-password").permitAll()
            	        .requestMatchers("/delivery/verify-email").permitAll()
            	        .requestMatchers("/NGO/update-password").permitAll()
            	        .requestMatchers("/api/deliveries/**").permitAll()
            	        .requestMatchers("/NGO/login", "/NGO/register", "/NGO/custom-logout").permitAll()
            	        .requestMatchers("/donor/**").hasAuthority("DONOR")
            	        .requestMatchers("/NGO/**").hasAuthority("NGO")
            	        .requestMatchers("/delivery/**").hasAuthority("DELIVERY")
            	        .requestMatchers("/admin/**").hasAuthority("ADMIN")
            	        .requestMatchers("/feedbacks/**").permitAll() 
            	        .anyRequest().authenticated()
            	    )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .addFilterBefore(new SessionAuthenticationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable()
                .httpBasic().disable();

            return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
