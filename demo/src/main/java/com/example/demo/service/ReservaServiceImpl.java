package com.example.demo.service;

import com.example.demo.entities.*;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HuespedRepository;
import com.example.demo.repository.ReservaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;    
import java.util.HashMap; 

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

    @Override
    public Reserva save(Reserva reserva) {
        return reservaRepository.save(reserva);
    }
    
    @Override
public void deleteById(Integer id) {
    Reserva reserva = findById(id);
    Habitacion habitacion = reserva.getHabitacion();
    
    reservaRepository.deleteById(id);
    
    // Actualizar el estado de la habitación después de eliminar la reserva
   // if (habitacion != null) {
        //habitacion.actualizarEstado();
        //habitacionRepository.save(habitacion);
    //}
}



   
    @Override
    public Reserva findById(Integer id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }
    
    @Override
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    @Override
    public List<Reserva> findByHuesped(Huesped huesped) {
        return reservaRepository.findByHuesped(huesped);
    }

    @Override
public boolean tieneReservasActivas(Integer huespedId) {
    Huesped huesped = huespedRepository.findById(huespedId)
            .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado"));
    
    List<Reserva> reservas = reservaRepository.findByHuesped(huesped);
    
    return reservas.stream().anyMatch(reserva -> 
        reserva.getEstado() == EstadoReserva.PENDIENTE || 
        reserva.getEstado() == EstadoReserva.CONFIRMADA
    );
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
    
    Reserva savedReserva = reservaRepository.save(reserva);
    
    // Actualizar el estado de la habitación
    //habitacion.actualizarEstado();
    //habitacionRepository.save(habitacion);
    
    return savedReserva;
}

@Override
public Reserva crearReservaPorTipo(Integer tipoHabitacionId,
                                   Integer huespedId,
                                   LocalDateTime fechaInicio,
                                   LocalDateTime fechaFin,
                                   Integer cantidadPersonas) {

    // Buscar huésped
    Huesped huesped = huespedRepository.findById(huespedId)
            .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado"));

    // Buscar habitaciones de ese tipo
    List<Habitacion> habitaciones = habitacionRepository.findByTipoHabitacion_Id(tipoHabitacionId);

    if (habitaciones == null || habitaciones.isEmpty()) {
        throw new RuntimeException("No existen habitaciones para este tipo");
    }

    // Buscar una habitación disponible
    Habitacion habitacionDisponible = null;

    for (Habitacion habitacion : habitaciones) {
        boolean disponible = isHabitacionDisponible(
                habitacion.getId(),
                fechaInicio,
                fechaFin
        );

        if (disponible) {
            habitacionDisponible = habitacion;
            break;
        }
    }

    if (habitacionDisponible == null) {
        throw new RuntimeException("No hay habitaciones disponibles para ese tipo en esas fechas");
    }

    // Crear reserva
    Reserva reserva = new Reserva();
    reserva.setHabitacion(habitacionDisponible);
    reserva.setHuesped(huesped);
    reserva.setFechaInicio(fechaInicio);
    reserva.setFechaFin(fechaFin);
    reserva.setCantidadPersonas(cantidadPersonas);
    reserva.setEstado(EstadoReserva.PENDIENTE);

    return reservaRepository.save(reserva);
}
    
    // ==================== NUEVOS MÉTODOS ====================
    
    @Override
    public boolean isHabitacionDisponible(Integer habitacionId, LocalDateTime inicio, LocalDateTime fin) {
        List<Reserva> reservas = reservaRepository.findByHabitacionId(habitacionId);
        
        for (Reserva reserva : reservas) {
            // Ignorar reservas canceladas
            if (reserva.getEstado() == EstadoReserva.CANCELADA) {
                continue;
            }
            
            // Verificar superposición: NO disponible si hay conflicto
            // Conflicto cuando: reserva existente NO termina antes de la nueva O NO empieza después de la nueva
            boolean hayConflicto = !(fin.isBefore(reserva.getFechaInicio()) || 
                                      inicio.isAfter(reserva.getFechaFin()));
            
            if (hayConflicto) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isHabitacionDisponibleParaEditar(Integer habitacionId, 
                                                     LocalDateTime inicio, 
                                                     LocalDateTime fin, 
                                                     Integer reservaId) {
        List<Reserva> reservas = reservaRepository.findByHabitacionId(habitacionId);
        
        for (Reserva reserva : reservas) {
            // Ignorar la reserva que estamos editando
            if (reserva.getId().equals(reservaId)) {
                continue;
            }
            
            // Ignorar reservas canceladas
            if (reserva.getEstado() == EstadoReserva.CANCELADA) {
                continue;
            }
            
            boolean hayConflicto = !(fin.isBefore(reserva.getFechaInicio()) || 
                                      inicio.isAfter(reserva.getFechaFin()));
            
            if (hayConflicto) {
                return false;
            }
        }
        return true;
    }

@Override
public Map<String, String> finalizarReserva(Integer reservaId) {
    Reserva reserva = findById(reservaId);
    
    // Solo se puede finalizar si está CONFIRMADA o PENDIENTE
    if (reserva.getEstado() != EstadoReserva.CONFIRMADA && 
        reserva.getEstado() != EstadoReserva.PENDIENTE) {
        throw new RuntimeException("Solo se pueden finalizar reservas CONFIRMADAS o PENDIENTES");
    }
    
    // Verificar si tiene cuenta y deuda pendiente
    CuentaHabitacion cuenta = reserva.getCuentaHabitacion();
    if (cuenta != null && cuenta.getTotal() > 0) {
        throw new RuntimeException("❌ No se puede finalizar la reserva. El huésped tiene una deuda pendiente de $" + cuenta.getTotal());
    }
    
    reserva.setEstado(EstadoReserva.FINALIZADA);
    save(reserva);
    
    // Opcional: Actualizar estado de habitación a DISPONIBLE
    Habitacion habitacion = reserva.getHabitacion();
    habitacion.setEstado("DISPONIBLE");
    // Si tienes habitacionRepository inyectado, úsalo
    // habitacionRepository.save(habitacion);
    
    return Map.of("ok", "Reserva finalizada correctamente");
}



















}