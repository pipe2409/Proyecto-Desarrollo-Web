package com.example.demo.controller;

import com.example.demo.dtos.AuthRequestDTO;
import com.example.demo.dtos.UserResponseDTO;
import com.example.demo.entities.Huesped;
import com.example.demo.entities.UserEntity;
import com.example.demo.entities.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
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
import java.util.UUID;
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
    private final EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, HuespedService huespedService,
                          UserMapper userMapper, JwtService jwtService, PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.huespedService = huespedService;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequestDTO authRequest) {
        // Verificacion de cuenta: si el usuario NO ha confirmado el correo, bloqueamos antes de autenticar.
        Optional<UserEntity> opt = userRepository.findByUsername(authRequest.getUsername());
        if (opt.isPresent() && !opt.get().isVerificado()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "err", "Debes verificar tu correo antes de iniciar sesión. Revisa tu bandeja de entrada."
            ));
        }

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
            // Genera token de verificacion ANTES de guardar para que el HuespedService
            // lo persista junto con el resto de campos del UserEntity.
            String token = UUID.randomUUID().toString();
            if (huesped.getUser() != null) {
                huesped.getUser().setTokenVerificacion(token);
                huesped.getUser().setVerificado(false);
            }

            // HuespedService.save() encripta password, asigna rol y valida unicidad.
            Huesped guardado = huespedService.save(huesped);

            // Enviar correo de verificacion (si SMTP no esta configurado solo loguea el link)
            String correo = guardado.getUser() != null ? guardado.getUser().getUsername() : null;
            if (correo != null) {
                emailService.enviarCorreoVerificacion(correo, guardado.getNombre(), token);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "ok", "Registro exitoso. Revisa tu correo para verificar tu cuenta antes de iniciar sesión."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("err", e.getMessage()));
        }
    }

    // Endpoint que invoca el link del correo. Marca verificado=true y borra el token.
    @GetMapping("/verificar")
    public ResponseEntity<Map<String, String>> verificar(@RequestParam("token") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("err", "Token no proporcionado"));
        }
        Optional<UserEntity> opt = userRepository.findByTokenVerificacion(token);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("err", "Token inválido o ya usado"));
        }
        UserEntity user = opt.get();
        if (user.isVerificado()) {
            return ResponseEntity.ok(Map.of("ok", "Tu cuenta ya estaba verificada. Puedes iniciar sesión."));
        }
        user.setVerificado(true);
        user.setTokenVerificacion(null);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("ok", "Cuenta verificada correctamente. Ya puedes iniciar sesión."));
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