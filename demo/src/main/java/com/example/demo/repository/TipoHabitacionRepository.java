package com.example.demo.repository;

import com.example.demo.entities.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Integer> {

    @Query(value = "SELECT * FROM tipo_habitacion WHERE capacidad >= :personas ORDER BY precio ASC LIMIT 1", nativeQuery = true)
    Optional<TipoHabitacion> encontrarMasBaratoParaCapacidadNative(@Param("personas") Integer personas);
}