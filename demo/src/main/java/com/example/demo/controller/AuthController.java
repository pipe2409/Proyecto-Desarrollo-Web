package com.example.demo.controller;

import com.example.demo.dtos.AuthRequestDTO;
import com.example.demo.dtos.UserResponseDTO;
import com.example.demo.entities.UserEntity;
import com.example.demo.entities.UserMapper;
import com.example.demo.repository.HuespedRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository; // Asumiendo que tienes un UserRepository
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository; // Necesario para buscar usuarios
    private final UserMapper userMapper;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> authenticateUser(@RequestBody AuthRequestDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Buscar el UserEntity completo para mapearlo a DTO
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(authRequest.getUsername());
        UserEntity userEntity = userEntityOptional.orElseThrow(() -> new RuntimeException("User not found after authentication"));

        return ResponseEntity.ok(userMapper.toDto(userEntity));
    }
}