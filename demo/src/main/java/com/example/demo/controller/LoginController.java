package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.service.HuespedService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {

    @Autowired
    private HuespedService huespedService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String correo    = body.get("correo");
        String contrasena = body.get("contrasena");

        Huesped h = huespedService.login(correo, contrasena);

        if (h == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("err", "Correo o contraseña incorrectos."));
        }

        // 👇 retornamos los datos que Angular necesita guardar localmente
        return ResponseEntity.ok(Map.of(
            "huespedId",     h.getId(),
            "nombre",        h.getNombre(),
            "apellido",      h.getApellido(),
            "correo",        h.getCorreo()
        ));
    }

    // 👇 el logout ya no necesita endpoint — Angular solo borra el localStorage
}