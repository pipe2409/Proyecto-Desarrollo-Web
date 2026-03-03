package com.example.demo.repository;

import com.example.demo.entities.Habitacion;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class HabitacionRepository {

    private final Map<Integer, Habitacion> habitaciones = new LinkedHashMap<>();

    public HabitacionRepository() {
        habitaciones.put(1, new Habitacion(1, "101", 1, "DISPONIBLE", 1, "Cerca al ascensor"));
        habitaciones.put(2, new Habitacion(2, "102", 1, "OCUPADA", 1, ""));
        habitaciones.put(3, new Habitacion(3, "201", 2, "DISPONIBLE", 2, "Silenciosa"));
        habitaciones.put(4, new Habitacion(4, "305", 3, "MANTENIMIENTO", 3, "Revisión de aire"));
        habitaciones.put(5, new Habitacion(5, "501", 5, "DISPONIBLE", 4, "Vista parcial"));
        habitaciones.put(6, new Habitacion(6, "PH-01", 12, "DISPONIBLE", 5, "Acceso privado"));
    }

    public List<Habitacion> findAll() {
        return new ArrayList<>(habitaciones.values());
    }

    public List<Habitacion> findByTipoId(int tipoId) {
        return habitaciones.values().stream()
                .filter(h -> h.getTipoHabitacionId() == tipoId)
                .collect(Collectors.toList());
    }

    public Habitacion findById(int id) {
        return habitaciones.get(id);
    }

    public void save(Habitacion habitacion) {
        if (habitacion.getId() <= 0) {
            habitacion.setId(nextId());
        }
        habitaciones.put(habitacion.getId(), habitacion);
    }

    public void deleteById(int id) {
        habitaciones.remove(id);
    }

    private int nextId() {
        return habitaciones.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }
}