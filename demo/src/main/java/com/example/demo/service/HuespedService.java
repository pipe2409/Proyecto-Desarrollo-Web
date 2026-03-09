package com.example.demo.service;

import com.example.demo.entities.Huesped;

import java.util.List;

public interface HuespedService {

    List<Huesped> findAll();

    Huesped findById(Integer id);

    Huesped findByCorreo(String correo);

    Huesped login(String correo, String contrasena);

    Huesped save(Huesped huesped);

    Huesped update(Integer id, Huesped huesped);

    void deleteById(Integer id);
}