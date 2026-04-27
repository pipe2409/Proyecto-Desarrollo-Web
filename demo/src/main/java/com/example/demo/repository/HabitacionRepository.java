package com.example.demo.repository;

import com.example.demo.entities.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

    List<Habitacion> findByTipoHabitacion_Id(Integer tipoId);
    
    long countByEstadoIn(List<String> estados);
}