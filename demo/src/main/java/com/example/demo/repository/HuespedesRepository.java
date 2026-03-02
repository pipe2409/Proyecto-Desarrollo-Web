package com.example.demo.repository;

import com.example.demo.entities.Huesped;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HuespedesRepository {

    private final Map<Integer, Huesped> huespedes = new HashMap<>();

    public HuespedesRepository() {

        huespedes.put(1, new Huesped(
                1, "Andres", "Beltran",
                "andres@gmail.com", "1234",
                "1001234567", "3001234567",
                "Calle 123 #45-67", "Colombia",
                new ArrayList<>()
        ));

        huespedes.put(2, new Huesped(
                2, "Maria", "Lopez",
                "maria@gmail.com", "abcd",
                "1019876543", "3019876543",
                "Carrera 10 #20-30", "Colombia",
                new ArrayList<>()
        ));

        huespedes.put(3, new Huesped(
                3, "Carlos", "Ramirez",
                "carlos@gmail.com", "pass123",
                "1024567890", "3104567890",
                "Avenida 15 #5-60", "Colombia",
                new ArrayList<>()
        ));

        huespedes.put(4, new Huesped(
                4, "Laura", "Torres",
                "laura@gmail.com", "laura2024",
                "1036543210", "3206543210",
                "Calle 50 #12-34", "Colombia",
                new ArrayList<>()
        ));

        huespedes.put(5, new Huesped(
                5, "Sebastian", "Gomez",
                "sebastian@gmail.com", "hotel2024",
                "1049876543", "3159876543",
                "Carrera 7 #33-22", "Colombia",
                new ArrayList<>()
        ));
    }

    public List<Huesped> findAll() {
        return new ArrayList<>(huespedes.values());
    }

    public Huesped findById(int id) {
        return huespedes.get(id);
    }

    public void save(Huesped huesped) {
        huespedes.put(huesped.getId(), huesped);
    }

    public void deleteById(int id) {
        huespedes.remove(id);
    }
    // ✅ LOGIN (BD falsa en Map)
public Huesped findByCorreoAndContrasena(String correo, String contrasena) {
    return huespedes.values().stream()
            .filter(h -> h.getCorreo().equalsIgnoreCase(correo)
                    && h.getContrasena().equals(contrasena))  // ✅ sin ñ
            .findFirst()
            .orElse(null);
}
}