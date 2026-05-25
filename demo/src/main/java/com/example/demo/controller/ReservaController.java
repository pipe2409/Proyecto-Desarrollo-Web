package com.example.demo.controller;

import com.example.demo.entities.CuentaHabitacion;
import com.example.demo.entities.EstadoReserva;
import com.example.demo.entities.Habitacion;
import com.example.demo.entities.Huesped;
import com.example.demo.entities.ItemCuenta;
import com.example.demo.entities.Reserva;
import com.example.demo.entities.ReservaMapper;
import com.example.demo.dtos.FacturaResumenDTO;
import com.example.demo.dtos.ItemFacturaDTO;
import com.example.demo.dtos.ReservaDetalleDTO;
import com.example.demo.repository.ItemCuentaRepository;
import com.example.demo.service.HabitacionService;
import com.example.demo.service.HuespedService;
import com.example.demo.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    @Autowired
    private ReservaMapper reservaMapper;

    @Autowired
    private ItemCuentaRepository itemCuentaRepository;

    @Autowired
    private com.example.demo.service.CuentaService cuentaService;

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

    // Admin/Operador: marcar manualmente la habitacion como pagada (ej. pago en efectivo).
    // Tambien sirve para regularizar reservas creadas antes de existir el campo.
    @PutMapping("/admin/{id}/habitacion-pagada")
    public ResponseEntity<?> marcarHabitacionPagada(@PathVariable Integer id, @RequestBody Map<String, Boolean> body) {
        try {
            Reserva reserva = reservaService.findById(id);
            boolean pagada = body == null || body.get("pagada") == null ? true : body.get("pagada");
            reserva.setHabitacionPagada(pagada);
            reservaService.save(reserva);
            return ResponseEntity.ok(Map.of(
                "ok", "Habitacion " + (pagada ? "marcada como pagada" : "marcada como pendiente"),
                "reservaId", reserva.getId().toString(),
                "habitacionPagada", String.valueOf(pagada)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("err", e.getMessage()));
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


    // Finalizar reserva (solo si cuenta está pagada)
@PutMapping("/{id}/finalizar")
public ResponseEntity<Map<String, String>> finalizarReserva(@PathVariable Integer id) {
    try {
        Map<String, String> resultado = reservaService.finalizarReserva(id);
        return ResponseEntity.ok(resultado);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("err", e.getMessage()));
    }
}

    // Factura completa de la reserva (habitacion + servicios)
    // La usa el operador antes de finalizar para ver cuanto debe el huesped.
    @GetMapping("/{id}/factura")
    public ResponseEntity<?> getFactura(@PathVariable Integer id) {
        try {
            Reserva reserva = reservaService.findById(id);

            FacturaResumenDTO factura = new FacturaResumenDTO();
            factura.setReservaId(reserva.getId());
            factura.setEstadoReserva(reserva.getEstado().name());

            // Huesped
            if (reserva.getHuesped() != null) {
                factura.setHuespedNombre(
                    (reserva.getHuesped().getNombre() == null ? "" : reserva.getHuesped().getNombre())
                    + " " +
                    (reserva.getHuesped().getApellido() == null ? "" : reserva.getHuesped().getApellido())
                );
            }

            // Habitacion + costo (noches x precio del tipo)
            Habitacion hab = reserva.getHabitacion();
            int precioNoche = 0;
            if (hab != null) {
                factura.setHabitacionCodigo(hab.getCodigo());
                if (hab.getTipoHabitacion() != null && hab.getTipoHabitacion().getPrecio() != null) {
                    precioNoche = hab.getTipoHabitacion().getPrecio();
                }
            }
            factura.setPrecioNoche(precioNoche);

            // Noches: diferencia en dias entre fechaInicio y fechaFin
            int noches = 0;
            if (reserva.getFechaInicio() != null && reserva.getFechaFin() != null) {
                long diff = ChronoUnit.DAYS.between(
                    reserva.getFechaInicio().toLocalDate(),
                    reserva.getFechaFin().toLocalDate()
                );
                noches = (int) Math.max(diff, 1); // minimo 1 noche
            }
            factura.setNoches(noches);

            factura.setFechaInicio(reserva.getFechaInicio() == null ? "" : reserva.getFechaInicio().toLocalDate().toString());
            factura.setFechaFin(reserva.getFechaFin() == null ? "" : reserva.getFechaFin().toLocalDate().toString());

            int subtotalHabitacion = noches * precioNoche;
            factura.setSubtotalHabitacion(subtotalHabitacion);
            factura.setHabitacionPagada(reserva.isHabitacionPagada());

            // Items de servicios (si hay cuenta de habitacion)
            List<ItemFacturaDTO> itemsDto = new ArrayList<>();
            int subtotalServicios = 0;
            // Si la reserva todavia no tiene cuenta, la creamos (asi el operador puede pagarla luego)
            CuentaHabitacion cuenta = cuentaService.getOrCreateCuentaByReserva(reserva);
            if (cuenta != null) {
                factura.setCuentaId(cuenta.getId());
                List<ItemCuenta> items = itemCuentaRepository.findByCuentaHabitacionId(cuenta.getId());
                for (ItemCuenta i : items) {
                    ItemFacturaDTO d = new ItemFacturaDTO();
                    d.setServicioNombre(i.getServicio() != null ? i.getServicio().getNombre() : "Servicio");
                    d.setCantidad(i.getCantidad());
                    int precio = i.getServicio() != null ? i.getServicio().getPrecio() : 0;
                    d.setPrecioUnitario(precio);
                    d.setSubtotal(i.getSubtotal());
                    itemsDto.add(d);
                    subtotalServicios += i.getSubtotal();
                }
            }
            factura.setItemsServicios(itemsDto);
            factura.setSubtotalServicios(subtotalServicios);

            // Total general (incluye habitacion + servicios, para info)
            factura.setTotalGeneral(subtotalHabitacion + subtotalServicios);

            // Deuda PENDIENTE: lo que el huesped todavia debe.
            // Si la habitacion ya fue pagada al reservar (via Stripe), solo se cobran los servicios.
            int deuda = subtotalServicios + (reserva.isHabitacionPagada() ? 0 : subtotalHabitacion);
            factura.setDeudaPendiente(deuda);

            return ResponseEntity.ok(factura);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("err", e.getMessage()));
        }
    }

    // Huésped: ver sus reservas
    @GetMapping("/mis-reservas/{huespedId}")
    public ResponseEntity<List<ReservaDetalleDTO>> misReservas(@PathVariable Integer huespedId) {
        try {
            Huesped huesped = huespedService.findById(huespedId);
            return ResponseEntity.ok(reservaMapper.toDtoList(reservaService.findByHuesped(huesped)));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint alterno que Angular está usando
    @GetMapping("/huesped/{huespedId}")
    public ResponseEntity<List<ReservaDetalleDTO>> listarPorHuesped(@PathVariable Integer huespedId) {
        try {
            Huesped huesped = huespedService.findById(huespedId);
            return ResponseEntity.ok(reservaMapper.toDtoList(reservaService.findByHuesped(huesped)));
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

           Reserva reserva = reservaService.crearReservaPorTipo(
        tipoHabitacionId,
        huespedId,
        inicio,
        fin,
        cantidadPersonas
);

return ResponseEntity.status(HttpStatus.CREATED)
        .body(Map.of(
                "ok", "Reserva creada correctamente.",
                "reservaId", reserva.getId().toString(),
                "habitacionId", reserva.getHabitacion().getId().toString(),
                "habitacionCodigo", reserva.getHabitacion().getCodigo()
        ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("err", e.getMessage()));
        }
    }
}