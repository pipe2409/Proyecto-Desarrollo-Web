package com.example.demo.entities;

/**
 * Que se pago en una transaccion registrada en HistorialPago.
 * HABITACION = checkout inicial via Stripe del precio de las noches.
 * SERVICIOS  = cobro al finalizar la estancia de los items de la cuenta.
 */
public enum TipoPago {
    HABITACION,
    SERVICIOS
}
