package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.service.HuespedService;
import com.example.demo.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/huespedes")
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
public class HuespedesController {

    @Autowired
    private HuespedService huespedService;

    @Autowired
    private ReservaService reservaService;

    private String getUsernameAutenticado() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    private boolean esDuenioDelRecurso(Huesped huesped) {
        String username = getUsernameAutenticado();

        return huesped != null &&
                huesped.getUser() != null &&
                huesped.getUser().getUsername().equals(username);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Huesped>> getAll() {
        return ResponseEntity.ok(huespedService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {

        Huesped huesped = huespedService.findById(id);

        if (huesped == null) {
            return ResponseEntity.notFound().build();
        }

        if (!esDuenioDelRecurso(huesped)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("err", "No tienes permiso para acceder a este usuario."));
        }

        return ResponseEntity.ok(huesped);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> actualizar(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body
    ) {
        try {
            Huesped huesped = huespedService.findById(id);

            if (huesped == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("err", "Usuario no encontrado."));
            }

            if (!esDuenioDelRecurso(huesped)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("err", "No tienes permiso para actualizar este usuario."));
            }

            huespedService.update(
                    id,
                    body.get("nombre"),
                    body.get("apellido"),
                    body.get("correo"),
                    body.get("cedula"),
                    body.get("telefono"),
                    body.get("direccion"),
                    body.get("nacionalidad")
            );

            return ResponseEntity.ok(Map.of("ok", "Datos actualizados correctamente."));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("err", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<Map<String, String>> cambiarContrasena(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body
    ) {
        try {
            Huesped huesped = huespedService.findById(id);

            if (huesped == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("err", "Usuario no encontrado."));
            }

            if (!esDuenioDelRecurso(huesped)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("err", "No tienes permiso para cambiar esta contraseña."));
            }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarCuenta(@PathVariable Integer id) {

        Huesped huesped = huespedService.findById(id);

        if (huesped == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("err", "Usuario no encontrado."));
        }

        if (!esDuenioDelRecurso(huesped)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("err", "No tienes permiso para eliminar este usuario."));
        }

        if (reservaService.tieneReservasActivas(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("err", "No puedes eliminar tu cuenta porque tienes reservas activas o pendientes."));
        }

        huespedService.deleteById(id);

        return ResponseEntity.ok(Map.of("ok", "Cuenta eliminada correctamente."));
    }
}