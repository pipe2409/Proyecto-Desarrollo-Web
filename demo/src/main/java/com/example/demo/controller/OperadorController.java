package com.example.demo.controller;

import com.example.demo.entities.Operador;
import com.example.demo.service.OperadorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operadores/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class OperadorController {

    @Autowired
    private OperadorService operadorService;

    @GetMapping
    public ResponseEntity<List<Operador>> getAll() {
        return ResponseEntity.ok(operadorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Operador> getById(@PathVariable Integer id) {
        try {
            Operador operador = operadorService.findById(id);
            return ResponseEntity.ok(operador);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Operador> crear(@RequestBody Operador operador) {
        Operador nuevo = operadorService.save(operador);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Operador> actualizar(@PathVariable Integer id,
                                               @RequestBody Operador operador) {
        try {
            Operador actualizado = operadorService.update(id, operador);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Integer id) {
        try {
            operadorService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("err", "Error al eliminar el operador."));
        }
    }
}