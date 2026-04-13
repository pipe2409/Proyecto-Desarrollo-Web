package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.service.HuespedService;
import com.example.demo.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "http://localhost:4200")
public class HuespedesController {

    @Autowired
    private HuespedService huespedService;

    @Autowired
    private ReservaService reservaService;

    // Admin: listar todos
    @GetMapping("/admin")
    public ResponseEntity<List<Huesped>> getAll() {
        return ResponseEntity.ok(huespedService.findAll());
    }

    // Obtener huesped por id (👇 ya no usa sesión, Angular envía el id)
    @GetMapping("/{id}")
    public ResponseEntity<Huesped> getById(@PathVariable Integer id) {
        Huesped huesped = huespedService.findById(id);
        if (huesped == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(huesped);
    }

    // Actualizar datos personales
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> actualizar(@PathVariable Integer id,
                                                           @RequestBody Map<String, String> body) {
        try {
            huespedService.update(
                id,
                body.get("nombre"),
                body.get("apellido"),
                body.get("correo"),
                body.get("telefono"),
                body.get("direccion"),
                body.get("pais")
            );
            return ResponseEntity.ok(Map.of("ok", "Datos actualizados correctamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("err", e.getMessage()));
        }
    }

    // Cambiar contraseña
    @PutMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<Map<String, String>> cambiarContrasena(@PathVariable Integer id,
                                                                   @RequestBody Map<String, String> body) {
        try {
            huespedService.cambiarContrasena(
                id,
                body.get("actual"),
                body.get("nueva"),
                body.get("confirmar")
            );
            return ResponseEntity.ok(Map.of("ok", "Contraseña actualizada correctamente."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("err", e.getMessage()));
        }
    }

    // Eliminar cuenta
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarCuenta(@PathVariable Integer id) {
        // 👇 misma validación que tenías: no eliminar si tiene reservas activas
        if (reservaService.tieneReservasActivas(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("err", "No puedes eliminar tu cuenta porque tienes reservas activas o pendientes."));
        }

        Huesped huesped = huespedService.findById(id);
        if (huesped == null) return ResponseEntity.notFound().build();

        huespedService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}