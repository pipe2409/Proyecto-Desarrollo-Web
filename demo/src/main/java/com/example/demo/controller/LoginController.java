package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.entities.Operador;
import com.example.demo.service.HuespedService;
import com.example.demo.service.OperadorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {

    @Autowired
    private HuespedService huespedService;
    
    @Autowired
    private OperadorService operadorService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String contrasena = body.get("contrasena");

        // 1️⃣ Intentar como HUESPED (cliente)
        Huesped h = huespedService.login(correo, contrasena);
        if (h != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", h.getId());
            response.put("nombre", h.getNombre());
            response.put("apellido", h.getApellido());
            response.put("correo", h.getCorreo());
            response.put("rol", "CLIENTE");
            response.put("tipo", "HUESPED");
            return ResponseEntity.ok(response);
        }
        
        // 2️⃣ Intentar como OPERADOR
        Operador op = operadorService.login(correo, contrasena);
        if (op != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", op.getId());
            response.put("correo", op.getCorreo());
            response.put("rol", "OPERADOR");
            response.put("tipo", "OPERADOR");
            // Para operador, enviar nombre y apellido vacíos o el correo como nombre
            response.put("nombre", "Operador");
            response.put("apellido", "");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("err", "Correo o contraseña incorrectos."));
    }
}