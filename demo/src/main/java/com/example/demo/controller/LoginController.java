package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.entities.Operador;
import com.example.demo.security.JwtService;
import com.example.demo.service.HuespedService;
import com.example.demo.service.OperadorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {

    @Autowired
    private HuespedService huespedService;

    @Autowired
    private OperadorService operadorService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {

        String correo = body.get("correo");
        String contrasena = body.get("contrasena");

        Huesped h = huespedService.login(correo, contrasena);

        if (h != null) {

            String rol = "CLIENTE";
            String token = jwtService.generarToken(
                    h.getUser().getUsername(),
                    rol
            );

            Map<String, Object> response = new HashMap<>();

            response.put("id", h.getId());
            response.put("nombre", h.getNombre());
            response.put("apellido", h.getApellido());
            response.put("correo", h.getUser() != null ? h.getUser().getUsername() : null);
            response.put("rol", rol);
            response.put("tipo", "HUESPED");
            response.put("token", token);

            return ResponseEntity.ok(response);
        }

        Operador op = operadorService.login(correo, contrasena);

        if (op != null) {

            String rol = "OPERADOR";
            String token = jwtService.generarToken(
                    op.getUser().getUsername(),
                    rol
            );

            Map<String, Object> response = new HashMap<>();

            response.put("id", op.getId());
            response.put("correo", op.getUser() != null ? op.getUser().getUsername() : null);
            response.put("rol", rol);
            response.put("tipo", "OPERADOR");
            response.put("nombre", "Operador");
            response.put("apellido", "");
            response.put("token", token);

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("err", "Correo o contraseña incorrectos."));
    }
}   