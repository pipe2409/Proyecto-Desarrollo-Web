package com.example.demo.entities;

/**
 * Estados posibles de una habitación. Antes era un String libre lo que permitia
 * typos ("Disponible", "DISPONI BLE") y dificultaba filtrar. Como enum:
 *  - DISPONIBLE: libre, lista para asignarse
 *  - OCUPADA: huesped hospedado actualmente
 *  - RESERVADA: tiene reserva confirmada pero el huesped aun no llego
 *  - MANTENIMIENTO: fuera de servicio por reparacion
 */
public enum EstadoHabitacion {
    DISPONIBLE,
    OCUPADA,
    RESERVADA,
    MANTENIMIENTO
}
