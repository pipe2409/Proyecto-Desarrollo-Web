package com.example.demo.repository;

import com.example.demo.entities.Huesped;
import com.example.demo.entities.Reserva;
import com.example.demo.entities.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    
    List<Reserva> findByHuesped(Huesped huesped);
    List<Reserva> findByHabitacionId(Integer habitacionId);
    
    @Query("SELECT r FROM Reserva r WHERE r.habitacion.id = :habitacionId " +
           "AND r.fechaInicio < :fechaFin AND r.fechaFin > :fechaInicio")
    List<Reserva> findReservasConflictivas(@Param("habitacionId") Integer habitacionId,
                                           @Param("fechaInicio") LocalDateTime fechaInicio,
                                           @Param("fechaFin") LocalDateTime fechaFin);
    
    // 👇 CAMBIAR String por EstadoReserva
    long countByEstadoIn(List<EstadoReserva> estados);
    
    List<Reserva> findByEstadoInAndFechaInicioBetweenOrderByFechaInicioAsc(List<EstadoReserva> estados, LocalDateTime inicio, LocalDateTime fin);
}