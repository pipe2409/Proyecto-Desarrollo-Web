package com.example.demo.controller;

import com.example.demo.entities.Testimonio;
import com.example.demo.repository.TestimonioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/testimonios")
@CrossOrigin(origins = "http://localhost:4200")
public class TestimonioController {

    @Autowired
    private TestimonioRepository testimonioRepository;

    // Listar todos (publico - sin login)
    @GetMapping
    public ResponseEntity<List<Testimonio>> getAll() {
        return ResponseEntity.ok(testimonioRepository.findAllByOrderByFechaCreacionDesc());
    }

    // Crear testimonio (requiere login)
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Testimonio testimonio) {
        try {
            if (testimonio.getAutor() == null || testimonio.getAutor().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("err", "El autor es obligatorio"));
            }
            if (testimonio.getTexto() == null || testimonio.getTexto().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("err", "El comentario es obligatorio"));
            }
            if (testimonio.getEstrellas() == null || testimonio.getEstrellas() < 1 || testimonio.getEstrellas() > 5) {
                return ResponseEntity.badRequest().body(Map.of("err", "Las estrellas deben ser entre 1 y 5"));
            }

            testimonio.setFechaCreacion(LocalDateTime.now());
            Testimonio guardado = testimonioRepository.save(testimonio);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("err", e.getMessage()));
        }
    }
}
