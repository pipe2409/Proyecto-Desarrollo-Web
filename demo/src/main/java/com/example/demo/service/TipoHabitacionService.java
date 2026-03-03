package com.example.demo.service;

import com.example.demo.entities.TipoHabitacion;

import java.util.List;

public interface TipoHabitacionService {

    List<TipoHabitacion> findAll();

    TipoHabitacion findById(int id);

    void save(TipoHabitacion tipoHabitacion);

    void update(int id, TipoHabitacion tipoHabitacion);

    void deleteById(int id);
}