package com.example.demo.repository;

import com.example.demo.entities.ItemCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemCuentaRepository extends JpaRepository<ItemCuenta, Integer> {
    
    // 👇 AGREGAR ESTE MÉTODO
    @Query("SELECT COALESCE(SUM(i.subtotal), 0) FROM ItemCuenta i " +
           "WHERE FUNCTION('YEAR', i.cuentaHabitacion.reserva.fechaInicio) = :anio " +
           "AND FUNCTION('MONTH', i.cuentaHabitacion.reserva.fechaInicio) = :mes")
    Double sumSubtotalByMes(@Param("anio") int anio, @Param("mes") int mes);
}