package com.example.demo.repository;

import com.example.demo.entities.CuentaHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaHabitacionRepository extends JpaRepository<CuentaHabitacion, Integer> {
    
}
