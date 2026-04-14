package com.example.demo.controller;

import com.example.demo.entities.EstadoReserva;
import com.example.demo.entities.Habitacion;
import com.example.demo.entities.Huesped;
import com.example.demo.entities.Reserva;
import com.example.demo.service.ReservaService;
import com.example.demo.service.HuespedService;
import com.example.demo.service.HabitacionService;

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
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
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
        Reserva reserva = reservaService.findById(id);
        if (reserva == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(reserva);
    }

    // Admin: actualizar reserva
    @PutMapping("/admin/{id}")
    public ResponseEntity<Reserva> actualizar(@PathVariable Integer id,
                                              @RequestBody Map<String, Object> body) {
        try {
            Reserva reserva = reservaService.findById(id);
            if (reserva == null) return ResponseEntity.notFound().build();

            Integer huespedId = (Integer) body.get("huespedId");
            Integer habitacionId = (Integer) body.get("habitacionId");
            String fechaInicio = (String) body.get("fechaInicio");
            String fechaFin = (String) body.get("fechaFin");

            reserva.setHuesped(huespedService.findById(huespedId));
            reserva.setHabitacion(habitacionService.findById(habitacionId));
            reserva.setFechaInicio(LocalDateTime.parse(fechaInicio));
            reserva.setFechaFin(LocalDateTime.parse(fechaFin));

            return ResponseEntity.ok(reservaService.save(reserva));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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

    @PutMapping("/{id}/cancelar")
public ResponseEntity<Map<String, String>> cancelarReserva(@PathVariable Integer id) {
    try {
        Reserva reserva = reservaService.findById(id);
        if (reserva == null) {
            return ResponseEntity.notFound().build();
        }
        
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaService.save(reserva);
        
        // Actualizar el estado de la habitación
       Habitacion habitacion = reserva.getHabitacion();
       // habitacion.actualizarEstado();
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
        Huesped huesped = huespedService.findById(huespedId);
        if (huesped == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(reservaService.findByHuesped(huesped));
    }

    // 👇 NUEVO: endpoint que Angular está esperando
    @GetMapping("/huesped/{huespedId}")
    public ResponseEntity<List<Reserva>> listarPorHuesped(@PathVariable Integer huespedId) {
        Huesped huesped = huespedService.findById(huespedId);
        if (huesped == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(reservaService.findByHuesped(huesped));
    }

    // Huésped: crear reserva
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

            //reservaService.crearReserva(habitacionId, huespedId, inicio, fin, cantidadPersonas);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("ok", "Reserva creada correctamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("err", e.getMessage()));
        }
    }




}