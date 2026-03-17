package com.example.demo.service;

import com.example.demo.entities.Reserva;
import java.time.LocalDateTime;


public interface ReservaService{

    Reserva crearReserva(Integer habitacionId,
                         Integer huespedId,
                         LocalDateTime fechaInicio,
                         LocalDateTime fechaFin,
                         Integer cantidadPersonas);
}