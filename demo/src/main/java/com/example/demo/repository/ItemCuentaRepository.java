package com.example.demo.repository;

import com.example.demo.entities.ItemCuenta;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCuentaRepository extends JpaRepository<ItemCuenta, Integer> {
    
}