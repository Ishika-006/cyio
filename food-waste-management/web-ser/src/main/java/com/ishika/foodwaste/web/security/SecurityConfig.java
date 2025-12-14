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
              .cors()
               .and()
            .csrf(csrf -> csrf.disable())
            
              .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
            .authorizeHttpRequests(auth -> auth
            	             .requestMatchers(
                    "/admin/login", "/admin/register",
                    "/donor/login", "/donor/register",
                    "/delivery/login", "/delivery/register",
                    "/NGO/login", "/NGO/register"
                ).permitAll()

                // ---------- EMAIL / PASSWORD ----------
                .requestMatchers(
                    "/admin/verify-email", "/admin/update-password",
                    "/donor/verify-email", "/donor/update-password",
                    "/delivery/verify-email", "/delivery/update-password",
                    "/NGO/verify-email", "/NGO/update-password"
                ).permitAll()

                // ---------- ROLE PROTECTED ----------
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/donor/**").hasAuthority("DONOR")
                .requestMatchers("/delivery/**").hasAuthority("DELIVERY")
                .requestMatchers("/NGO/**").hasAuthority("NGO")

                // ---------- EVERYTHING ELSE ----------
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
