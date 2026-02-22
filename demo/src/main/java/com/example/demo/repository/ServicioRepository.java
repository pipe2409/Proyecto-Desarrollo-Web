package com.example.demo.repository;

import com.example.demo.entities.Servicio;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ServicioRepository {

    private List<Servicio> servicios = new ArrayList<>();

    // Constructor → datos iniciales (tu BD falsa)
    public ServicioRepository() {
        servicios.add(new Servicio(1, "Spa", "Relajación total", 80));
        servicios.add(new Servicio(2, "Gym", "Acceso al gimnasio", 0));
        servicios.add(new Servicio(3, "Restaurante", "Buffet incluido", 120));
    }

    public List<Servicio> findAll() {
        return servicios;
    }

    public void save(Servicio servicio) {
        servicios.add(servicio);
    }

    public void deleteById(int id) {
        servicios.removeIf(s -> s.getId() == id);
    }
}