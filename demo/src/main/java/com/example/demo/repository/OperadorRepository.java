package com.example.demo.repository;

import com.example.demo.entities.Operador;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OperadorRepository extends JpaRepository<Operador, Integer> {
    
}
