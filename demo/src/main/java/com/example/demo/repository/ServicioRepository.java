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
        servicios.put(1, new Servicio(1, "Spa Revitalizante", "Ritual de relajación con aromaterapia y sauna privada — renueva cuerpo y mente en 90 minutos.", 80, "https://images.pexels.com/photos/3757942/pexels-photo-3757942.jpeg?cs=srgb&dl=pexels-olly-3757942.jpg&fm=jpg", 2, "por persona", "09:00 - 21:00 (citas cada 30 min)"));
        servicios.put(2, new Servicio(2, "Gym Privado", "Acceso ilimitado al gimnasio equipado con entrenadores personales disponibles bajo reserva.", 0, "https://www.johansens.com/wp-content/uploads/2020/08/sopwell-gym-e1597313157723-1920x1042.jpg", 10, "por grupo", "06:00 - 22:00"));
        servicios.put(3, new Servicio(3, "Restaurante Gourmet", "Buffet y menú a la carta con ingredientes locales y maridaje recomendado por nuestro chef.", 120, "https://watermark.lovepik.com/photo/20211121/large/lovepik-hotel-restaurant-picture_500532019.jpg", 6, "por persona", "07:00 - 23:00"));
        servicios.put(4, new Servicio(4, "Piscina Climatizada VIP", "Piscina climatizada con solárium y cóctel de bienvenida — acceso privado por turnos.", 50, "https://purquiza89.github.io/Proyecto-Hotel-/assets/img/piletaclimatizada.jpg", 20, "por grupo", "08:00 - 20:00"));
        servicios.put(5, new Servicio(5, "Tour Playa Privada", "Recorrido guiado con fotógrafo y picnic gourmet en una cala privada — experiencia única.", 150, "https://img.freepik.com/fotos-premium/imagen-deslumbrante-lujoso-resort-isla-privada-que-cuenta-villas-sobre-agua-playas-virgenes-exuberantes-palmeras-que-combinan-perfeccion-opulencia-serenidad-natural_674594-8608.jpg", 8, "por persona", "10:00 / 15:00 (reservar con 24h de antelación)"));
        servicios.put(6, new Servicio(6, "Bar Premium", "Selección de cócteles de autor y barra libre de bebidas premium durante 3 horas.", 95, "https://tse4.mm.bing.net/th/id/OIP.3mExFmLXNFQI2VyODySObwHaE7?rs=1&pid=ImgDetMain&o=7&rm=3", 15, "por grupo", "18:00 - 22:00"));
        servicios.put(7, new Servicio(7, "Masaje Terapéutico", "Sesión personalizada de 60 minutos con técnicas relajantes y descontracturantes.", 110, "https://image-tc.galaxy.tf/wijpeg-cv1tgdcdf80dtalh56f0n25nu/ssp0308.jpg?width=600&height=450", 1, "por persona", "09:00 - 19:00 (citas cada 60 min)"));
        servicios.put(8, new Servicio(8, "Clases de Yoga", "Yoga al amanecer frente al mar: sesión guiada para todos los niveles.", 40, "https://erwachsenenhotels-buchen.de/wp-content/uploads/2023/09/yoga-hotels.jpg", 20, "por persona", "06:30 - 08:00 / 17:30 - 19:00"));
        servicios.put(9, new Servicio(9, "Servicio a la Habitación VIP", "Menú exclusivo servido en la habitación con atención personalizada 24/7.", 70, "https://tse2.mm.bing.net/th/id/OIP.Vs7G5eFric10PIHw2LMxIQHaD_?rs=1&pid=ImgDetMain&o=7&rm=3", 4, "por pedido", "Disponible 24 horas"));
        servicios.put(10, new Servicio(10, "Transporte Privado", "Traslado privado desde/hacia el aeropuerto con chófer bilingüe y maletero incluido.", 130, "https://travel-buddies.com/wp-content/uploads/2024/09/1_london-any-hotel-to-heathrow-airport-private-transfer.jpg", 4, "por grupo", "Servicio según vuelo (reserva con 12h de antelación)"));
    }

    public List<Servicio> findAll() {
        return new ArrayList<>(servicios.values());
    }

    public Servicio findById(int id) {
        return servicios.get(id);
    }

    public void save(Servicio servicio) {
        servicios.put(servicio.getId(), servicio);
    }

    public void deleteById(int id) {
        servicios.remove(id);
    }
}