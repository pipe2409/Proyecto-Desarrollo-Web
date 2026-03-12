package com.example.demo.service;

import com.example.demo.entities.Habitacion;

import java.util.List;

public interface HabitacionService {

    List<Habitacion> findAll();

    List<Habitacion> findByTipoId(Integer tipoId);

    Habitacion findById(Integer id);

    Habitacion save(Habitacion habitacion);

    Habitacion update(Integer id, Habitacion habitacion, Integer tipoHabitacionId);

    void deleteById(Integer id);
}