package com.example.demo.service;

import com.example.demo.entities.TipoHabitacion;
import java.util.List;

import org.springframework.stereotype.Service;

@Service

public interface TipoHabitacionService {

    List<TipoHabitacion> findAll();

    TipoHabitacion findById(Integer id);

    TipoHabitacion save(TipoHabitacion tipoHabitacion);

    TipoHabitacion update(Integer id, TipoHabitacion tipoHabitacion);

    void deleteById(Integer id);
}