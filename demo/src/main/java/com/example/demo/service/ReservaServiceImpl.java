package com.example.demo.service;

import com.example.demo.entities.*;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HuespedRepository;
import com.example.demo.repository.ReservaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

@Service
@Transactional
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private HabitacionRepository habitacionRepository;
    @Autowired
    private HuespedRepository huespedRepository;

    public Reserva save(Reserva reserva) {
    return reservaRepository.save(reserva);
    }
   
    public Reserva findById(Integer id) {
    return reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }
    public List<Reserva> findAll() {
    return reservaRepository.findAll();
    }

    @Override
    public List<Reserva> findByHuesped(Huesped huesped) {
    return reservaRepository.findByHuesped(huesped);
        }

    @Override
    public Reserva crearReserva(Integer habitacionId,
                                Integer huespedId,
                                LocalDateTime fechaInicio,
                                LocalDateTime fechaFin,
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