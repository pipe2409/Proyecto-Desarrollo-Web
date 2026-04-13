package com.example.demo.controller;

import com.example.demo.entities.TipoHabitacion;
import com.example.demo.entities.Habitacion;
import com.example.demo.service.TipoHabitacionService;
import com.example.demo.service.HabitacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController                                         // 👈 cambia @Controller
@RequestMapping("/api/tipos-habitacion")               // 👈 cambia la ruta base
@CrossOrigin(origins = "http://localhost:4200")        // 👈 permite Angular
public class TipoHabitacionController {

    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    @Autowired
    private HabitacionService habitacionService;

    @GetMapping
    public ResponseEntity<List<TipoHabitacion>> getAll() {
        return ResponseEntity.ok(tipoHabitacionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoHabitacion> getById(@PathVariable Integer id) {
        TipoHabitacion tipo = tipoHabitacionService.findById(id);
        if (tipo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tipo);
    }

    @PostMapping
    public ResponseEntity<TipoHabitacion> crear(@RequestBody TipoHabitacion tipo) {
        TipoHabitacion nuevo = tipoHabitacionService.save(tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoHabitacion> actualizar(@PathVariable Integer id,
                                                      @RequestBody TipoHabitacion tipo) {
        try {
            TipoHabitacion actualizado = tipoHabitacionService.update(id, tipo);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Integer id) {
        // 👇 misma validación que tenías: no eliminar si tiene habitaciones asignadas
        List<Habitacion> habitaciones = habitacionService.findByTipoId(id);
        if (habitaciones != null && !habitaciones.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("err", "No se puede eliminar; hay habitaciones asignadas a este tipo."));
        }
        try {
            tipoHabitacionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("err", "Error al eliminar el tipo de habitación."));
        }
    }
}