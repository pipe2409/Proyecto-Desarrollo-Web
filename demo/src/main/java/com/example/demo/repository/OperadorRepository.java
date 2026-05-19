package com.example.demo.repository;

import com.example.demo.entities.Operador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OperadorRepository extends JpaRepository<Operador, Integer> {
    
    // Busca al operador a través del username de la entidad UserEntity vinculada
    Optional<Operador> findByUser_Username(String username);
}