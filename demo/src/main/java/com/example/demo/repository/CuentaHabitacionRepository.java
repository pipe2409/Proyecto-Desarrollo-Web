package com.example.demo.repository;

import com.example.demo.entities.CuentaHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CuentaHabitacionRepository extends JpaRepository<CuentaHabitacion, Integer> {
    
    // 👇 AGREGAR ESTE MÉTODO
    Optional<CuentaHabitacion> findByReservaId(Integer reservaId);
}