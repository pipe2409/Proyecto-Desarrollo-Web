package com.example.demo.service;

import com.example.demo.entities.Habitacion;
import com.example.demo.entities.Huesped;
import com.example.demo.entities.Reserva;
import com.example.demo.entities.EstadoReserva;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HuespedRepository;
import com.example.demo.repository.ReservaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private HabitacionRepository habitacionRepository;
    @Autowired
    private HuespedRepository huespedRepository;

    public Reserva crearReserva(Integer habitacionId, Integer huespedId,
                                 LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                 Integer cantidadPersonas) {

        Habitacion habitacion = habitacionRepository.findById(habitacionId)
                .orElseThrow(() -> new EntityNotFoundException("Habitación no encontrada"));

        Huesped huesped = huespedRepository.findById(huespedId)
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado"));

        Reserva reserva = new Reserva();
        reserva.setHabitacion(habitacion);
        reserva.setHuesped(huesped);
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setCantidadPersonas(cantidadPersonas);
        reserva.setEstado(EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }
}
