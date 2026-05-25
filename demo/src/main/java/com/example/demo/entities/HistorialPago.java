package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registro persistente de cada pago confirmado por Stripe. Resuelve la
 * limitacion previa donde los ItemCuenta se borraban al pagar y entonces
 * desaparecian del calculo de ingresos. Aqui queda traza inmutable de cuanto
 * se cobro, cuando, de que tipo y a que reserva.
 */
@Entity
@Table(name = "historial_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Monto cobrado en unidades enteras (los precios del sistema son int).
    @Column(nullable = false)
    private Integer monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPago tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    @JsonIgnore
    private Reserva reserva;

    @Column(nullable = false)
    private LocalDateTime fechaPago;

    // Stripe session id, para correlacionar contra el dashboard de Stripe.
    @Column(length = 200)
    private String sessionIdStripe;
}
