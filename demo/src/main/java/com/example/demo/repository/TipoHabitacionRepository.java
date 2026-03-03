package com.example.demo.repository;

import com.example.demo.entities.TipoHabitacion;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TipoHabitacionRepository {

    private final Map<Integer, TipoHabitacion> tiposHabitacion = new LinkedHashMap<>();

    public TipoHabitacionRepository() {
        // seed (tus 5 tipos)
        tiposHabitacion.put(1, new TipoHabitacion(1, "Habitación Estándar",
                "Cómoda y funcional.", 180000,
                "https://images.pexels.com/photos/271624/pexels-photo-271624.jpeg?cs=srgb&dl=pexels-pixabay-271624.jpg&fm=jpg",
                2, "1 Queen", "Wi-Fi • TV • Aire", true));

        tiposHabitacion.put(2, new TipoHabitacion(2, "Habitación Individual",
                "Ideal para una persona.", 120000,
                "https://images.pexels.com/photos/271618/pexels-photo-271618.jpeg?cs=srgb&dl=pexels-pixabay-271618.jpg&fm=jpg",
                1, "1 Sencilla", "Wi-Fi • Escritorio", true));

        tiposHabitacion.put(3, new TipoHabitacion(3, "Habitación con Vista al Mar",
                "Balcón y vista al océano.", 240000,
                "https://images.pexels.com/photos/189296/pexels-photo-189296.jpeg?cs=srgb&dl=pexels-pixabay-189296.jpg&fm=jpg",
                2, "1 King", "Balcón • Vista al mar", true));

        tiposHabitacion.put(4, new TipoHabitacion(4, "Suite de Lujo",
                "Suite amplia con sala.", 420000,
                "https://images.pexels.com/photos/164595/pexels-photo-164595.jpg?cs=srgb&dl=pexels-pixabay-164595.jpg&fm=jpg",
                3, "King + sofá cama", "Sala • Minibar", true));

        tiposHabitacion.put(5, new TipoHabitacion(5, "Suite Presidencial",
                "La experiencia más exclusiva.", 850000,
                "https://images.pexels.com/photos/262048/pexels-photo-262048.jpeg?cs=srgb&dl=pexels-pixabay-262048.jpg&fm=jpg",
                4, "King + 2 dobles", "Jacuzzi • Sala • Comedor", true));
    }

    public List<TipoHabitacion> findAll() {
        return new ArrayList<>(tiposHabitacion.values());
    }

    public TipoHabitacion findById(int id) {
        return tiposHabitacion.get(id);
    }

    public void save(TipoHabitacion tipoHabitacion) {
        // Auto-id si viene sin id (útil para "Crear" desde formulario)
        if (tipoHabitacion.getId() <= 0) {
            tipoHabitacion.setId(nextId());
        }
        tiposHabitacion.put(tipoHabitacion.getId(), tipoHabitacion);
    }

    public void deleteById(int id) {
        tiposHabitacion.remove(id);
    }

    private int nextId() {
        return tiposHabitacion.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }
}