package com.example.demo.controller;

import com.example.demo.dtos.AuthRequestDTO;
import com.example.demo.dtos.UserResponseDTO;
import com.example.demo.entities.Huesped;
import com.example.demo.entities.UserEntity;
import com.example.demo.entities.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.HuespedService;
import com.example.demo.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HuespedService huespedService;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, 
                          RoleRepository roleRepository, HuespedService huespedService,
                          UserMapper userMapper, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.huespedService = huespedService;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> authenticateUser(@RequestBody AuthRequestDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Buscar el UserEntity completo para mapearlo a DTO
        UserEntity userEntity = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rol = userEntity.getRoles().stream().findFirst().map(r -> r.getName()).orElse("ROLE_CLIENTE");
        
        String token = jwtService.generarToken(userEntity.getUsername(), rol);

        UserResponseDTO response = userMapper.toDto(userEntity);
        response.setToken(token);
        response.setRol(rol);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, String>> registro(@RequestBody Huesped huesped) {
        try {
            // Hashear contraseña antes de guardar
            huesped.getUser().setPassword(passwordEncoder.encode(huesped.getUser().getPassword()));
            // Asignar rol por defecto
            roleRepository.findByName("ROLE_CLIENTE").ifPresent(r -> huesped.getUser().getRoles().add(r));
            
            huespedService.save(huesped);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("ok", "Registro exitoso."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("err", e.getMessage()));
        }
    }

    @GetMapping("/details")
    public ResponseEntity<UserResponseDTO> getDetails(Principal principal) {
        UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow();
        UserResponseDTO response = userMapper.toDto(user);
        String rol = user.getRoles().stream().findFirst().map(r -> r.getName()).orElse("ROLE_CLIENTE");
        response.setRol(rol);
        return ResponseEntity.ok(response);
    }
}