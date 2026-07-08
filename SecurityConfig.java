package com.dhms.config;

import com.dhms.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/hospital-admin/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN")
                .requestMatchers("/api/doctor/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN", "DOCTOR")
                .requestMatchers("/api/receptionist/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN", "RECEPTIONIST")
                .requestMatchers("/api/lab/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN", "LAB_TECHNICIAN")
                .requestMatchers("/api/pharmacy/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN", "PHARMACIST")
                .requestMatchers("/api/patient/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN", "DOCTOR", "RECEPTIONIST", "PATIENT")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
