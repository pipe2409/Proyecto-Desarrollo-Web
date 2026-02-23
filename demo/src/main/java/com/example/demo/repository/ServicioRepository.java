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
        servicios.add(new Servicio(1, "Spa", "Relajación total", 80, "https://example.com/spa.jpg"));
        servicios.add(new Servicio(2, "Gym", "Acceso al gimnasio", 0, "https://example.com/gym.jpg"));
        servicios.add(new Servicio(3, "Restaurante", "Buffet incluido", 120, "https://example.com/restaurante.jpg"));
        servicios.add(new Servicio(4, "Piscina Climatizada", "Acceso ilimitado a piscina climatizada", 50, "https://example.com/piscina.jpg"));
        servicios.add(new Servicio(5, "Tour Playa Privada", "Recorrido guiado por la playa privada del hotel", 150, "https://example.com/playa.jpg"));
        servicios.add(new Servicio(6, "Bar Premium", "Bebidas premium ilimitadas por 3 horas", 95, "https://example.com/bar.jpg"));
        servicios.add(new Servicio(7, "Masaje Terapéutico", "Sesión de masaje relajante de 60 minutos", 110, "https://example.com/masaje.jpg"));
        servicios.add(new Servicio(8, "Clases de Yoga", "Sesión grupal de yoga frente al mar", 40, "https://example.com/yoga.jpg"));
        servicios.add(new Servicio(9, "Servicio a la Habitación VIP", "Atención personalizada 24/7", 70, "https://example.com/roomservice.jpg"));
        servicios.add(new Servicio(10, "Transporte Aeropuerto", "Traslado privado aeropuerto-hotel", 130, "https://example.com/transporte.jpg"));
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