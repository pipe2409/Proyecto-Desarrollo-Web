package com.example.demo.repository;

import com.example.demo.entities.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HuespedRepository extends JpaRepository<Huesped, Integer> {
    Optional<Huesped> findByCorreo(String correo);
    Optional<Huesped> findByCedula(String cedula);
    Optional<Huesped> findByCorreoAndContrasena(String correo, String contrasena);
}