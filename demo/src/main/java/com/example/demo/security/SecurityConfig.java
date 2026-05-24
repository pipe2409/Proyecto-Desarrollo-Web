package com.example.demo.security;

import com.example.demo.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        return new JwtAuthenticationFilter(jwtService, userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()

            // LECTURA PUBLICA DE LA LANDING (sin login)
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/servicios/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/tipos-habitacion/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/habitaciones/**")).permitAll()

            // Operadores admin: solo ADMIN puede ver/crear/editar operadores
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/operadores/admin/**")).hasAuthority("ROLE_ADMIN")

            // Reservas admin: tanto ADMIN como OPERADOR necesitan listar/gestionar reservas
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/reservas/admin/**"))
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_OPERADOR")
            // Las estadisticas del dashboard tambien las usa el OPERADOR en /menu-admin
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/estadisticas/**"))
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_OPERADOR")

            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/cuentas/**"))
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_OPERADOR")

            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/huespedes/admin/**"))
                .hasAuthority("ROLE_ADMIN")

            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/huespedes/**"))
                .hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN")

            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/reservas/**"))
                .hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN", "ROLE_OPERADOR")

            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/habitaciones/**"))
                .hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN", "ROLE_OPERADOR")

            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/tipos-habitacion/**"))
                .hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN", "ROLE_OPERADOR")

            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/servicios/**"))
                .hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN", "ROLE_OPERADOR")

            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .headers(headers -> headers.frameOptions(frame -> frame.disable()));

    return http.build();
}

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}