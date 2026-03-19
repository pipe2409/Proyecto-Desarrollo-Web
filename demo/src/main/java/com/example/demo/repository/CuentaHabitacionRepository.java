package com.example.demo.repository;

import com.example.demo.entities.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaHabitacionRepository extends JpaRepository<Habitacion, Integer> {
    
}
