package com.example.demo.controller;

import com.example.demo.entities.CuentaHabitacion;
import com.example.demo.entities.ItemCuenta;
import com.example.demo.entities.Reserva;
import com.example.demo.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas")
@CrossOrigin(origins = "http://localhost:4200")
public class CuentaHabitacionController {

    @Autowired
    private CuentaService cuentaService;

    // Buscar reserva activa por número de habitación
    @GetMapping("/reserva-por-habitacion/{numeroHabitacion}")
    public ResponseEntity<?> getReservaPorHabitacion(@PathVariable String numeroHabitacion) {
        try {
            Reserva reserva = cuentaService.findReservaActivaByHabitacionCodigo(numeroHabitacion);
            CuentaHabitacion cuenta = cuentaService.getOrCreateCuentaByReserva(reserva);
            
            Map<String, Object> response = new HashMap<>();
            response.put("reserva", reserva);
            response.put("cuenta", cuenta);
            response.put("huesped", reserva.getHuesped());
            response.put("habitacion", reserva.getHabitacion());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Obtener items de una cuenta
    @GetMapping("/{cuentaId}/items")
    public ResponseEntity<?> getItemsCuenta(@PathVariable Integer cuentaId) {
        try {
            List<ItemCuenta> items = cuentaService.getItemsByCuentaId(cuentaId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Agregar servicio a la cuenta
    @PostMapping("/{cuentaId}/agregar-servicio")
    public ResponseEntity<?> agregarServicio(@PathVariable Integer cuentaId,
                                              @RequestBody Map<String, Object> body) {
        try {
            Integer servicioId = (Integer) body.get("servicioId");
            Integer cantidad = (Integer) body.getOrDefault("cantidad", 1);
            
            ItemCuenta item = cuentaService.agregarServicio(cuentaId, servicioId, cantidad);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Servicio agregado correctamente",
                "item", item
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Eliminar item de la cuenta
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> eliminarItem(@PathVariable Integer itemId) {
        try {
            cuentaService.eliminarItem(itemId);
            return ResponseEntity.ok(Map.of("mensaje", "Item eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Pagar todos los servicios de la cuenta
    @PostMapping("/{cuentaId}/pagar")
    public ResponseEntity<?> pagarCuenta(@PathVariable Integer cuentaId) {
        try {
            int totalPagado = cuentaService.pagarCuenta(cuentaId);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Pago realizado correctamente",
                "totalPagado", totalPagado
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}