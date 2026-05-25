package com.example.demo.controller;

import com.example.demo.entities.EstadoHabitacion;
import com.example.demo.entities.Habitacion;
import com.example.demo.entities.HabitacionMapper;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.dtos.HabitacionDetalleDTO;
import com.example.demo.service.HabitacionService;
import com.example.demo.service.TipoHabitacionService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habitaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;
    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    @Autowired
    private HabitacionMapper habitacionMapper;

    // Listar todas (con filtro opcional por tipoId)
    @GetMapping
public ResponseEntity<List<HabitacionDetalleDTO>> getAll(
        @RequestParam(value = "tipoId", required = false) Integer tipoId) {
    List<Habitacion> habitaciones = habitacionService.findByTipoId(tipoId);
    return ResponseEntity.ok(habitacionMapper.toDtoList(habitaciones));
}

    // Obtener una por id
    @GetMapping("/{id}")
    public ResponseEntity<Habitacion> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(habitacionService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Crear habitacion
    @PostMapping
    public ResponseEntity<Habitacion> crear(@RequestBody Map<String, Object> body) {
        try {
            Integer tipoHabitacionId = (Integer) body.get("tipoHabitacionId");
            TipoHabitacion tipo = tipoHabitacionService.findById(tipoHabitacionId);

            Habitacion habitacion = new Habitacion();
            habitacion.setCodigo((String) body.get("codigo"));
            habitacion.setPiso((Integer) body.get("piso"));
            habitacion.setEstado(parseEstado(body.get("estado")));
            habitacion.setTipoHabitacion(tipo);
            habitacion.setNotas((String) body.get("notas"));
           

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(habitacionService.save(habitacion));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar habitacion
    @PutMapping("/{id}")
    public ResponseEntity<Habitacion> actualizar(@PathVariable Integer id,
                                                  @RequestBody Map<String, Object> body) {
        try {
            Integer tipoHabitacionId = (Integer) body.get("tipoHabitacionId");
            Habitacion habitacion = habitacionService.findById(id);
             habitacion.setCodigo((String) body.get("codigo"));       
        habitacion.setPiso((Integer) body.get("piso"));           
        habitacion.setEstado(parseEstado(body.get("estado")));
        habitacion.setNotas((String) body.get("notas"));

            habitacionService.update(id, habitacion, tipoHabitacionId);
            return ResponseEntity.ok(habitacion);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar habitacion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            habitacionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Convierte el valor que llega del body (String desde JSON) a la enum.
    // Si llega null o un valor invalido, default = DISPONIBLE en lugar de fallar.
    private EstadoHabitacion parseEstado(Object raw) {
        if (raw == null) return EstadoHabitacion.DISPONIBLE;
        try {
            return EstadoHabitacion.valueOf(raw.toString().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return EstadoHabitacion.DISPONIBLE;
        }
    }
}