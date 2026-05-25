package com.example.demo.security;

import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        return new JwtAuthenticationFilter(jwtService, userRepository);
    }

    // Evita la auto-registracion del JwtAuthenticationFilter como servlet filter.
    // Spring Boot auto-registra TODOS los @Bean de tipo Filter como servlet filters,
    // lo que hace que corra ANTES de la cadena de Spring Security: setea la auth
    // pero luego SecurityContextHolderFilter la borra (porque la sesion es STATELESS),
    // y como OncePerRequestFilter ya marco el request, el filtro DENTRO de la cadena
    // (addFilterBefore) se salta. Resultado: 403 en todo endpoint autenticado.
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
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

            // Chatbot publico (vive en la landing, no requiere login)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/chatbot/**")).permitAll()

            // LECTURA PUBLICA DE LA LANDING (sin login)
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/servicios/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/tipos-habitacion/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/habitaciones/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/testimonios/**")).permitAll()

            // Crear testimonio: requiere estar logueado (cualquier rol)
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/testimonios/**"))
                .hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN", "ROLE_OPERADOR")

            // Pagos Stripe:
            //  - /confirmar es PUBLICO porque viene del redirect de Stripe y el back
            //    ya verifica con Stripe que el pago realmente se completo.
            //  - /checkout requiere estar logueado.
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/pagos/reserva/confirmar")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/pagos/servicios/confirmar")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/pagos/**"))
                .hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN", "ROLE_OPERADOR")

            // Operadores admin: solo ADMIN puede ver/crear/editar operadores
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/operadores/admin/**")).hasAuthority("ROLE_ADMIN")

            // Reservas admin: tanto ADMIN como OPERADOR necesitan listar/gestionar reservas
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/reservas/admin/**"))
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_OPERADOR")
            // Las estadisticas del dashboard tambien las usa el OPERADOR en /menu-admin
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/estadisticas/**"))
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_OPERADOR")

            // Reportes Excel descargables: solo admin/operador
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/reportes/**"))
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
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}