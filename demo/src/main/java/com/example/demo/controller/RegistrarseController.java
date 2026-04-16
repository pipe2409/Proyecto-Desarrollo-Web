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
public class RegistrarseController {

    @Autowired
    private HuespedService huespedService;

    @PostMapping("/registro")
public ResponseEntity<Map<String, String>> registro(@RequestBody Huesped huesped) {
    try {
        huespedService.save(huesped);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("ok", "Registro exitoso."));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest()
                .body(Map.of("err", e.getMessage()));
    }
}
}