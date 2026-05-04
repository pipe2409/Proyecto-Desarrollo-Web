package com.example.demo.repository;

import com.example.demo.entities.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

    @Query(value = "SELECT * FROM servicio WHERE precio <= :precioMax AND precio_tipo = :tipo", nativeQuery = true)
    List<Servicio> buscarEconomicosPorTipoNative(@Param("precioMax") Integer precioMax, @Param("tipo") String tipo);
}