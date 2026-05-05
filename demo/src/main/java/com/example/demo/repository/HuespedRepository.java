package com.example.demo.repository;

import com.example.demo.entities.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HuespedRepository extends JpaRepository<Huesped, Integer> {
    Optional<Huesped> findByCorreo(String correo);
    Optional<Huesped> findByCedula(String cedula);
    Optional<Huesped> findByCorreoAndContrasena(String correo, String contrasena);

    @Query("SELECT h FROM Huesped h WHERE h.correo LIKE %:dominio%")
    List<Huesped> buscarPorDominioCorreo(@Param("dominio") String dominio);
}