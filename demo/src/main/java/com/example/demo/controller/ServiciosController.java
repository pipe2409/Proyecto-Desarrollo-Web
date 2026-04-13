package com.example.demo.controller;

import com.example.demo.entities.Servicio;
import com.example.demo.service.ServicioService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                              // 👈 cambia @Controller
@RequestMapping("/api/servicios")            // 👈 cambia la ruta base
@CrossOrigin(origins = "http://localhost:4200") // 👈 permite llamadas desde Angular
public class ServiciosController {

    @Autowired
    private ServicioService serviciosService;

    @GetMapping
    public ResponseEntity<List<Servicio>> getAll() {
        return ResponseEntity.ok(serviciosService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicio> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(serviciosService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Servicio> crear(@RequestBody Servicio servicio) {
        Servicio nuevo = serviciosService.save(servicio);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servicio> actualizar(@PathVariable Integer id,
                                               @RequestBody Servicio servicio) {
        try {
            return ResponseEntity.ok(serviciosService.update(id, servicio));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        serviciosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
