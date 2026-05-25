package com.example.demo.repository;

import com.example.demo.entities.EstadoHabitacion;
import com.example.demo.entities.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

    @Query("SELECT h FROM Habitacion h WHERE h.estado = :estado AND h.piso = :piso")
    List<Habitacion> findByEstadoYPiso(@Param("estado") EstadoHabitacion estado, @Param("piso") Integer piso);

    List<Habitacion> findByTipoHabitacion_Id(Integer tipoId);

    long countByEstadoIn(List<EstadoHabitacion> estados);
}