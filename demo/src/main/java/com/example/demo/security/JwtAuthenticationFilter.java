package com.example.demo.security;

import com.example.demo.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    @SuppressWarnings("unused")
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String method = request.getMethod();
        String uri = request.getRequestURI();

        if (authHeader == null) {
            log.info("[JWT] {} {} sin header Authorization", method, uri);
        } else if (!authHeader.startsWith("Bearer ")) {
            log.warn("[JWT] {} {} con Authorization mal formado: {}", method, uri, authHeader);
        } else {
            String token = authHeader.substring(7);
            boolean valido = jwtService.tokenValido(token);
            if (!valido) {
                log.warn("[JWT] {} {} con token INVALIDO/EXPIRADO", method, uri);
            } else {
                String username = jwtService.obtenerUsername(token);
                List<String> roles = jwtService.obtenerRoles(token);
                log.info("[JWT] {} {} autenticado como '{}' roles={}", method, uri, username, roles);

                if (roles != null) {
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}