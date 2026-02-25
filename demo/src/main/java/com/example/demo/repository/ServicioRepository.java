package com.example.demo.repository;

import com.example.demo.entities.Servicio;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ServicioRepository {

    private Map<Integer, Servicio> servicios = new HashMap<>();

    // Constructor → datos iniciales (tu BD falsa)
    public ServicioRepository() {
        servicios.put(1, new Servicio(1, "Spa", "Relajación total", 80, "https://images.pexels.com/photos/3757942/pexels-photo-3757942.jpeg?cs=srgb&dl=pexels-olly-3757942.jpg&fm=jpg"));
        servicios.put(2, new Servicio(2, "Gym", "Acceso al gimnasio", 0, "https://www.johansens.com/wp-content/uploads/2020/08/sopwell-gym-e1597313157723-1920x1042.jpg"));
        servicios.put(3, new Servicio(3, "Restaurante", "Buffet incluido", 120, "https://watermark.lovepik.com/photo/20211121/large/lovepik-hotel-restaurant-picture_500532019.jpg"));
        servicios.put(4, new Servicio(4, "Piscina Climatizada", "Acceso ilimitado a piscina climatizada", 50, "https://purquiza89.github.io/Proyecto-Hotel-/assets/img/piletaclimatizada.jpg"));
        servicios.put(5, new Servicio(5, "Tour Playa Privada", "Recorrido guiado por la playa privada del hotel", 150, "https://img.freepik.com/fotos-premium/imagen-deslumbrante-lujoso-resort-isla-privada-que-cuenta-villas-sobre-agua-playas-virgenes-exuberantes-palmeras-que-combinan-perfeccion-opulencia-serenidad-natural_674594-8608.jpg"));
        servicios.put(6, new Servicio(6, "Bar Premium", "Bebidas premium ilimitadas por 3 horas", 95, "https://tse4.mm.bing.net/th/id/OIP.3mExFmLXNFQI2VyODySObwHaE7?rs=1&pid=ImgDetMain&o=7&rm=3"));
        servicios.put(7, new Servicio(7, "Masaje Terapéutico", "Sesión de masaje relajante de 60 minutos", 110, "https://image-tc.galaxy.tf/wijpeg-cv1tgdcdf80dtalh56f0n25nu/ssp0308.jpg?width=600&height=450"));
        servicios.put(8, new Servicio(8, "Clases de Yoga", "Sesión grupal de yoga frente al mar", 40, "https://erwachsenenhotels-buchen.de/wp-content/uploads/2023/09/yoga-hotels.jpg"));
        servicios.put(9, new Servicio(9, "Servicio a la Habitación VIP", "Atención personalizada 24/7", 70, "https://tse2.mm.bing.net/th/id/OIP.Vs7G5eFric10PIHw2LMxIQHaD_?rs=1&pid=ImgDetMain&o=7&rm=3"));
        servicios.put(10, new Servicio(10, "Transporte Aeropuerto", "Traslado privado aeropuerto-hotel", 130, "https://travel-buddies.com/wp-content/uploads/2024/09/1_london-any-hotel-to-heathrow-airport-private-transfer.jpg"));
    }

    public List<Servicio> findAll() {
        return new ArrayList<>(servicios.values());
    }

    public void save(Servicio servicio) {
        servicios.put(servicio.getId(), servicio);
    }

    public void deleteById(int id) {
        servicios.remove(id);
    }
}