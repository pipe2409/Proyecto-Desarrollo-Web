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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

        // TODOS los roles del usuario (el admin tiene ROLE_ADMIN + ROLE_OPERADOR)
        List<String> roles = userEntity.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toList());

        // Rol "principal" para la redireccion del front (prioridad ADMIN > OPERADOR > CLIENTE)
        String rolPrincipal;
        if (roles.contains("ROLE_ADMIN")) {
            rolPrincipal = "ROLE_ADMIN";
        } else if (roles.contains("ROLE_OPERADOR")) {
            rolPrincipal = "ROLE_OPERADOR";
        } else {
            rolPrincipal = "ROLE_CLIENTE";
        }

        // El token JWT lleva TODOS los roles, asi el admin puede acceder
        // tanto a endpoints de ADMIN como de OPERADOR.
        String token = jwtService.generarToken(userEntity.getUsername(), roles);

        UserResponseDTO response = userMapper.toDto(userEntity);
        response.setToken(token);
        // El front compara con "ADMIN"/"OPERADOR"/"CLIENTE" (sin prefijo).
        response.setRol(rolPrincipal.replace("ROLE_", ""));

        // Si es CLIENTE, devolver el ID y datos del Huesped (no del UserEntity)
        // porque "Mi perfil" del front llama a /api/huespedes/{id} y necesita el id correcto.
        if ("ROLE_CLIENTE".equals(rolPrincipal)) {
            Huesped huesped = huespedService.findByCorreo(userEntity.getUsername());
            if (huesped != null) {
                response.setId(huesped.getId());
                response.setNombre(huesped.getNombre());
                response.setApellido(huesped.getApellido());
            }
        }

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
        response.setRol(rol.replace("ROLE_", ""));
        return ResponseEntity.ok(response);
    }
}