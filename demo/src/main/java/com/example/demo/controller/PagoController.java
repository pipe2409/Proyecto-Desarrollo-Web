package com.example.demo.controller;

import com.example.demo.entities.CuentaHabitacion;
import com.example.demo.entities.EstadoReserva;
import com.example.demo.entities.Reserva;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.CuentaHabitacionRepository;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.service.CuentaService;
import com.example.demo.service.ReservaService;
import com.example.demo.service.TipoHabitacionService;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Pasarela de pagos con Stripe Checkout (modo hosted).
 *
 * Flujo:
 *  1. Front llama POST /api/pagos/{tipo}/checkout con los datos.
 *  2. Back crea una Stripe Checkout Session y devuelve { url }.
 *  3. Front redirige al usuario a esa url (la pagina de pago de Stripe).
 *  4. Usuario paga con tarjeta de prueba (4242 4242 4242 4242).
 *  5. Stripe redirige a /pago-exitoso?session_id=...&tipo=...&extra=...
 *  6. Front llama POST /api/pagos/{tipo}/confirmar con el session_id.
 *  7. Back verifica con Stripe que payment_status == "paid" y entonces ejecuta
 *     la accion real (crear reserva / pagar cuenta).
 */
@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "http://localhost:4200")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);

    @Value("${stripe.api.key:}")
    private String stripeKey;

    @Value("${app.front.url:http://localhost:4200}")
    private String frontUrl;

    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaHabitacionRepository cuentaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @PostConstruct
    public void init() {
        if (stripeKey != null && !stripeKey.isBlank()) {
            Stripe.apiKey = stripeKey;
            log.info("Stripe configurado correctamente (modo test)");
        } else {
            log.warn("STRIPE_SECRET_KEY no configurado. Los endpoints de pago no funcionarán.");
        }
    }

    // ================================================================
    // RESERVA: cliente paga la habitacion antes de que se cree la reserva
    // ================================================================

    @PostMapping("/reserva/checkout")
    public ResponseEntity<?> crearCheckoutReserva(@RequestBody Map<String, Object> body) {
        try {
            ensureStripe();

            Integer tipoHabitacionId = toInt(body.get("tipoHabitacionId"));
            Integer huespedId = toInt(body.get("huespedId"));
            Integer cantidadPersonas = toInt(body.get("cantidadPersonas"));
            String fechaInicio = (String) body.get("fechaInicio");
            String fechaFin = (String) body.get("fechaFin");

            if (tipoHabitacionId == null || huespedId == null || fechaInicio == null || fechaFin == null) {
                return ResponseEntity.badRequest().body(Map.of("err", "Faltan datos para crear el pago."));
            }

            TipoHabitacion tipo = tipoHabitacionService.findById(tipoHabitacionId);
            if (tipo == null || tipo.getPrecio() == null) {
                return ResponseEntity.badRequest().body(Map.of("err", "Tipo de habitación inválido."));
            }

            long noches = Math.max(1, ChronoUnit.DAYS.between(LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin)));
            int total = (int) noches * tipo.getPrecio();

            // amount en Stripe va en CENTAVOS de la moneda
            long amount = (long) total * 100;

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontUrl + "/pago-exitoso?tipo=reserva&session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontUrl + "/pago-cancelado")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(amount)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Hotel Praia - " + tipo.getNombre())
                                            .setDescription(noches + " noche(s) | " + cantidadPersonas + " persona(s)")
                                            .build())
                                    .build())
                            .build())
                    // Guardamos los datos en metadata para crear la reserva al confirmar.
                    .putMetadata("tipoHabitacionId", String.valueOf(tipoHabitacionId))
                    .putMetadata("huespedId", String.valueOf(huespedId))
                    .putMetadata("cantidadPersonas", String.valueOf(cantidadPersonas == null ? 1 : cantidadPersonas))
                    .putMetadata("fechaInicio", fechaInicio)
                    .putMetadata("fechaFin", fechaFin)
                    .build();

            Session session = Session.create(params);

            Map<String, String> resp = new HashMap<>();
            resp.put("url", session.getUrl());
            resp.put("sessionId", session.getId());
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            log.error("Error creando checkout de reserva", e);
            return ResponseEntity.internalServerError().body(Map.of("err", "Error iniciando el pago: " + e.getMessage()));
        }
    }

    @PostMapping("/reserva/confirmar")
    public ResponseEntity<?> confirmarReserva(@RequestBody Map<String, String> body) {
        try {
            ensureStripe();
            String sessionId = body.get("sessionId");
            if (sessionId == null || sessionId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("err", "sessionId requerido"));
            }

            Session session = Session.retrieve(sessionId);
            if (!"paid".equals(session.getPaymentStatus())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "err", "El pago no se completó. Estado: " + session.getPaymentStatus()
                ));
            }

            // Recuperar metadata y crear la reserva real
            Map<String, String> meta = session.getMetadata();
            Integer tipoHabitacionId = Integer.valueOf(meta.get("tipoHabitacionId"));
            Integer huespedId = Integer.valueOf(meta.get("huespedId"));
            Integer cantidadPersonas = Integer.valueOf(meta.get("cantidadPersonas"));
            LocalDateTime inicio = LocalDate.parse(meta.get("fechaInicio")).atStartOfDay();
            LocalDateTime fin = LocalDate.parse(meta.get("fechaFin")).atStartOfDay();

            Reserva reserva = reservaService.crearReservaPorTipo(
                    tipoHabitacionId, huespedId, inicio, fin, cantidadPersonas
            );

            // El huesped acaba de pagar via Stripe -> reserva CONFIRMADA y habitacion pagada
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reserva.setHabitacionPagada(true);
            reservaRepository.save(reserva);

            return ResponseEntity.ok(Map.of(
                "ok", "Pago confirmado y reserva creada.",
                "reservaId", reserva.getId().toString(),
                "habitacionId", reserva.getHabitacion().getId().toString(),
                "habitacionCodigo", reserva.getHabitacion().getCodigo()
            ));
        } catch (Exception e) {
            log.error("Error confirmando reserva", e);
            return ResponseEntity.internalServerError().body(Map.of("err", "Error confirmando pago: " + e.getMessage()));
        }
    }

    // ================================================================
    // SERVICIOS: operador cobra los servicios consumidos de una cuenta
    // ================================================================

    @PostMapping("/servicios/checkout")
    public ResponseEntity<?> crearCheckoutServicios(@RequestBody Map<String, Object> body) {
        try {
            ensureStripe();
            Integer cuentaId = toInt(body.get("cuentaId"));
            if (cuentaId == null) {
                return ResponseEntity.badRequest().body(Map.of("err", "cuentaId requerido"));
            }

            CuentaHabitacion cuenta = cuentaRepository.findById(cuentaId).orElse(null);
            if (cuenta == null) {
                return ResponseEntity.badRequest().body(Map.of("err", "Cuenta no encontrada"));
            }
            if (cuenta.getTotal() == 0) {
                return ResponseEntity.badRequest().body(Map.of("err", "La cuenta no tiene servicios pendientes de pago"));
            }

            long amount = (long) cuenta.getTotal() * 100;
            String descripcion = "Servicios consumidos en habitación "
                    + (cuenta.getReserva() != null && cuenta.getReserva().getHabitacion() != null
                       ? cuenta.getReserva().getHabitacion().getCodigo()
                       : "");

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontUrl + "/pago-exitoso?tipo=servicios&session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontUrl + "/pago-cancelado")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(amount)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Hotel Praia - Servicios")
                                            .setDescription(descripcion)
                                            .build())
                                    .build())
                            .build())
                    .putMetadata("cuentaId", String.valueOf(cuentaId))
                    .build();

            Session session = Session.create(params);
            Map<String, String> resp = new HashMap<>();
            resp.put("url", session.getUrl());
            resp.put("sessionId", session.getId());
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            log.error("Error creando checkout de servicios", e);
            return ResponseEntity.internalServerError().body(Map.of("err", "Error iniciando el pago: " + e.getMessage()));
        }
    }

    @PostMapping("/servicios/confirmar")
    public ResponseEntity<?> confirmarServicios(@RequestBody Map<String, String> body) {
        try {
            ensureStripe();
            String sessionId = body.get("sessionId");
            if (sessionId == null || sessionId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("err", "sessionId requerido"));
            }
            Session session = Session.retrieve(sessionId);
            if (!"paid".equals(session.getPaymentStatus())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "err", "El pago no se completó. Estado: " + session.getPaymentStatus()
                ));
            }
            Integer cuentaId = Integer.valueOf(session.getMetadata().get("cuentaId"));
            int totalPagado = cuentaService.pagarCuenta(cuentaId);
            return ResponseEntity.ok(Map.of(
                "ok", "Pago confirmado. Servicios cobrados.",
                "totalPagado", String.valueOf(totalPagado)
            ));
        } catch (Exception e) {
            log.error("Error confirmando servicios", e);
            return ResponseEntity.internalServerError().body(Map.of("err", "Error confirmando pago: " + e.getMessage()));
        }
    }

    // Helpers

    private void ensureStripe() {
        if (stripeKey == null || stripeKey.isBlank()) {
            throw new IllegalStateException("Stripe no está configurado. Setea STRIPE_SECRET_KEY.");
        }
    }

    private Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return null; }
    }
}
