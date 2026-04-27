package com.example.demo.repository;

import com.example.demo.entities.Operador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OperadorRepository extends JpaRepository<Operador, Integer> {
    
    // 👇 Método para buscar por correo
    Optional<Operador> findByCorreo(String correo);
}