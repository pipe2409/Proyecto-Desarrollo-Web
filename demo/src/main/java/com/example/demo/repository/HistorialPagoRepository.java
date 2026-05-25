package com.example.demo.repository;

import com.example.demo.entities.HistorialPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HistorialPagoRepository extends JpaRepository<HistorialPago, Integer> {

    // Total cobrado en el mes indicado (suma ambos tipos: habitacion + servicios).
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM HistorialPago p " +
           "WHERE FUNCTION('YEAR', p.fechaPago) = :anio " +
           "AND FUNCTION('MONTH', p.fechaPago) = :mes")
    Long sumIngresosByMes(@Param("anio") int anio, @Param("mes") int mes);

    // Pagos en un rango de fechas, mas reciente primero. Se usa para el reporte
    // Excel descargable desde el panel del operador.
    List<HistorialPago> findByFechaPagoBetweenOrderByFechaPagoDesc(LocalDateTime desde, LocalDateTime hasta);
}
