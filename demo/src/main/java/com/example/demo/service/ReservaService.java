package com.example.demo.service;

import com.example.demo.entities.Huesped;
import com.example.demo.entities.Reserva;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaService {

    Reserva crearReserva(Integer habitacionId,
                         Integer huespedId,
                         LocalDateTime fechaInicio,
                         LocalDateTime fechaFin,
                         Integer cantidadPersonas);

    Reserva crearReservaPorTipo(Integer tipoHabitacionId,
                                Integer huespedId,
                                LocalDateTime fechaInicio,
                                LocalDateTime fechaFin,
                                Integer cantidadPersonas);

    List<Reserva> findByHuesped(Huesped huesped);
    List<Reserva> findAll();
    Reserva findById(Integer id);
    Reserva save(Reserva reserva);
    void deleteById(Integer id);

    boolean tieneReservasActivas(Integer huespedId);
    boolean isHabitacionDisponible(Integer habitacionId, LocalDateTime inicio, LocalDateTime fin);
    boolean isHabitacionDisponibleParaEditar(Integer habitacionId, LocalDateTime inicio, LocalDateTime fin, Integer reservaId);
}