package com.example.demo.repository;

import com.example.demo.entities.HistorialPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistorialPagoRepository extends JpaRepository<HistorialPago, Integer> {

    // Total cobrado en el mes indicado (suma ambos tipos: habitacion + servicios).
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM HistorialPago p " +
           "WHERE FUNCTION('YEAR', p.fechaPago) = :anio " +
           "AND FUNCTION('MONTH', p.fechaPago) = :mes")
    Long sumIngresosByMes(@Param("anio") int anio, @Param("mes") int mes);
}
