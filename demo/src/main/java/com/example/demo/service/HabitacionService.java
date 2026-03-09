package com.example.demo.service;

import com.example.demo.entities.Habitacion;

import java.util.List;
import java.util.Map;

public interface HabitacionService {

    List<Habitacion> listar(Integer tipoId);

    Habitacion obtenerPorId(int id);

    Habitacion construirNueva(Integer tipoId);

    void crear(Habitacion habitacion);

    void actualizar(int id, Habitacion habitacion);

    void eliminar(int id);

    Map<Integer, String> nombresTipoHabitacion();
}