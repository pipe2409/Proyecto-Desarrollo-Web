package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

            .csrf(csrf -> csrf.disable())

            .headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin())
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // H2
                .requestMatchers("/h2-console/**").permitAll()

                // LOGIN Y REGISTRO
                .requestMatchers("/api/auth/**").permitAll()

                // ENDPOINTS ADMIN / OPERADOR
                .requestMatchers("/api/operadores/**")
                    .hasAnyAuthority("OPERADOR", "ADMIN")

                .requestMatchers("/api/estadisticas/**")
                    .hasAnyAuthority("OPERADOR", "ADMIN")

                
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}