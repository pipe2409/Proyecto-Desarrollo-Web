package com.example.demo.controller;

import com.example.demo.entities.EstadoReserva;
import com.example.demo.entities.Habitacion;
import com.example.demo.entities.Huesped;
import com.example.demo.entities.Reserva;
import com.example.demo.service.HabitacionService;
import com.example.demo.service.HuespedService;
import com.example.demo.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(
    origins = "http://localhost:4200",
    methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.OPTIONS
    }
)
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private HuespedService huespedService;

    @Autowired
    private HabitacionService habitacionService;

    // Admin: listar todas
    @GetMapping("/admin")
    public ResponseEntity<List<Reserva>> getAll() {
        return ResponseEntity.ok(reservaService.findAll());
    }

    // Admin: obtener una por id
    @GetMapping("/admin/{id}")
    public ResponseEntity<Reserva> getById(@PathVariable Integer id) {
        try {
            Reserva reserva = reservaService.findById(id);
            return ResponseEntity.ok(reserva);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Admin: actualizar reserva
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id,
                                        @RequestBody Map<String, Object> body) {
        try {
            Reserva reserva = reservaService.findById(id);

            Integer huespedId = (Integer) body.get("huespedId");
            Integer habitacionId = (Integer) body.get("habitacionId");
            Integer cantidadPersonas = (Integer) body.get("cantidadPersonas");
            String estado = (String) body.get("estado");
            String fechaInicio = (String) body.get("fechaInicio");
            String fechaFin = (String) body.get("fechaFin");

            LocalDateTime inicio = LocalDate.parse(fechaInicio).atStartOfDay();
            LocalDateTime fin = LocalDate.parse(fechaFin).atStartOfDay();

            if (!fin.isAfter(inicio)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("err", "La fecha de fin debe ser posterior a la fecha de inicio"));
            }

            boolean disponible = reservaService.isHabitacionDisponibleParaEditar(
                    habitacionId, inicio, fin, id
            );

            if (!disponible) {
                return ResponseEntity.badRequest()
                        .body(Map.of("err", "La habitación no está disponible en esas fechas"));
            }

            reserva.setHuesped(huespedService.findById(huespedId));
            reserva.setHabitacion(habitacionService.findById(habitacionId));
            reserva.setCantidadPersonas(cantidadPersonas);
            reserva.setFechaInicio(inicio);
            reserva.setFechaFin(fin);
            
            // ✅ NUEVO: Actualizar el estado de la reserva
            if (estado != null) {
                reserva.setEstado(EstadoReserva.valueOf(estado));
            }

            return ResponseEntity.ok(reservaService.save(reserva));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("err", e.getMessage()));
        }
    }

    // Admin: eliminar reserva
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            reservaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Huésped/Admin: cancelar reserva
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Map<String, String>> cancelarReserva(@PathVariable Integer id) {
        try {
            Reserva reserva = reservaService.findById(id);

            reserva.setEstado(EstadoReserva.CANCELADA);
            reservaService.save(reserva);

            Habitacion habitacion = reserva.getHabitacion();
            habitacionService.save(habitacion);

            return ResponseEntity.ok(Map.of("ok", "Reserva cancelada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("err", e.getMessage()));
        }
    }

    // Huésped: ver sus reservas
    @GetMapping("/mis-reservas/{huespedId}")
    public ResponseEntity<List<Reserva>> misReservas(@PathVariable Integer huespedId) {
        try {
            Huesped huesped = huespedService.findById(huespedId);
            return ResponseEntity.ok(reservaService.findByHuesped(huesped));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint alterno que Angular está usando
    @GetMapping("/huesped/{huespedId}")
    public ResponseEntity<List<Reserva>> listarPorHuesped(@PathVariable Integer huespedId) {
        try {
            Huesped huesped = huespedService.findById(huespedId);
            return ResponseEntity.ok(reservaService.findByHuesped(huesped));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Huésped: crear reserva por habitación específica
    @PostMapping("/crear")
    public ResponseEntity<Map<String, String>> crear(@RequestBody Map<String, Object> body) {
        try {
            Integer habitacionId = (Integer) body.get("habitacionId");
            Integer huespedId = (Integer) body.get("huespedId");
            Integer cantidadPersonas = (Integer) body.get("cantidadPersonas");
            String fechaInicio = (String) body.get("fechaInicio");
            String fechaFin = (String) body.get("fechaFin");

            LocalDateTime inicio = LocalDate.parse(fechaInicio).atStartOfDay();
            LocalDateTime fin = LocalDate.parse(fechaFin).atStartOfDay();

            if (!fin.isAfter(inicio)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("err", "La fecha de fin debe ser posterior a la fecha de inicio"));
            }

            boolean disponible = reservaService.isHabitacionDisponible(habitacionId, inicio, fin);

            if (!disponible) {
                return ResponseEntity.badRequest()
                        .body(Map.of("err", "La habitación no está disponible en esas fechas"));
            }

            reservaService.crearReserva(
                    habitacionId,
                    huespedId,
                    inicio,
                    fin,
                    cantidadPersonas
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("ok", "Reserva creada correctamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("err", e.getMessage()));
        }
    }

    // Huésped: crear reserva por tipo de habitación
    @PostMapping("/crear-por-tipo")
    public ResponseEntity<Map<String, String>> crearPorTipo(@RequestBody Map<String, Object> body) {
        try {
            Integer tipoHabitacionId = (Integer) body.get("tipoHabitacionId");
            Integer huespedId = (Integer) body.get("huespedId");
            Integer cantidadPersonas = (Integer) body.get("cantidadPersonas");
            String fechaInicio = (String) body.get("fechaInicio");
            String fechaFin = (String) body.get("fechaFin");

            LocalDateTime inicio = LocalDate.parse(fechaInicio).atStartOfDay();
            LocalDateTime fin = LocalDate.parse(fechaFin).atStartOfDay();

            if (!fin.isAfter(inicio)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("err", "La fecha de fin debe ser posterior a la fecha de inicio"));
            }

            reservaService.crearReservaPorTipo(
                    tipoHabitacionId,
                    huespedId,
                    inicio,
                    fin,
                    cantidadPersonas
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("ok", "Reserva creada correctamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("err", e.getMessage()));
        }
    }
}