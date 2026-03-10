package com.example.demo.repository;

import com.example.demo.entities.RoomType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RoomTypeRepository {

    private final Map<Integer, RoomType> roomTypes = new HashMap<>();
    private int nextId = 1;

    public RoomTypeRepository() {
        // Datos iniciales de ejemplo
        roomTypes.put(1, new RoomType(1, "Habitación 101", "Doble", 2, 101, "Disponible"));
        roomTypes.put(2, new RoomType(2, "Habitación 102", "Suite", 4, 102, "Disponible"));
        roomTypes.put(3, new RoomType(3, "Habitación 201", "Sencilla", 1, 201, "Ocupada"));
        roomTypes.put(4, new RoomType(4, "Habitación 202", "Doble", 2, 202, "Disponible"));
        nextId = 5;
    }









    // Obtener todas las habitaciones
    public List<RoomType> findAll() {
        return new ArrayList<>(roomTypes.values());
    }

    // Obtener habitación por ID
    public RoomType findById(int id) {
        return roomTypes.get(id);
    }

    // Guardar (crear o actualizar)
    public void save(RoomType roomType) {
        if (roomType.getId() == 0) {
            roomType.setId(nextId);
            nextId++;
        }
        roomTypes.put(roomType.getId(), roomType);
    }

    // Eliminar habitación por ID
    public void deleteById(int id) {
        roomTypes.remove(id);
    }

    // Contar habitaciones
    public int count() {
        return roomTypes.size();
    }

    // Obtener próximo ID
    public int getNextId() {
        return nextId;
    }
}













