package com.example.demo;

import com.example.demo.entities.Habitacion;
import com.example.demo.entities.Huesped;
import com.example.demo.entities.Servicio;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HuespedRepository;
import com.example.demo.repository.ServicioRepository;
import com.example.demo.repository.TipoHabitacionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class DataLoader implements CommandLineRunner {


    @Autowired
    private  TipoHabitacionRepository tipoHabitacionRepository;
    @Autowired
    private  HabitacionRepository habitacionRepository;
    @Autowired
    private  ServicioRepository servicioRepository;
    @Autowired
    private  HuespedRepository huespedRepository;
   



    @Override
    public void run(String... args) {
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        TipoHabitacion simple = null;
        TipoHabitacion doble = null;
        TipoHabitacion suite = null;
        TipoHabitacion economica = null;
        TipoHabitacion familiar = null;

        if (tipoHabitacionRepository.count() == 0) {
            TipoHabitacion[] tipos = cargarTiposHabitacion();
            simple = tipos[0];
            doble = tipos[1];
            suite = tipos[2];
            economica = tipos[3];
            familiar = tipos[4];
            System.out.println("✓ Tipos de habitación cargados correctamente");
        } else {
            System.out.println("✓ Los tipos de habitación ya existen. Saltando carga.");

            simple = tipoHabitacionRepository.findAll().stream()
                    .filter(t -> "Habitación Simple".equals(t.getNombre()))
                    .findFirst()
                    .orElse(null);

            doble = tipoHabitacionRepository.findAll().stream()
                    .filter(t -> "Habitación Doble".equals(t.getNombre()))
                    .findFirst()
                    .orElse(null);

            suite = tipoHabitacionRepository.findAll().stream()
                    .filter(t -> "Suite".equals(t.getNombre()))
                    .findFirst()
                    .orElse(null);

            economica = tipoHabitacionRepository.findAll().stream()
                    .filter(t -> "Habitación Económica".equals(t.getNombre()))
                    .findFirst()
                    .orElse(null);

            familiar = tipoHabitacionRepository.findAll().stream()
                    .filter(t -> "Habitación Familiar".equals(t.getNombre()))
                    .findFirst()
                    .orElse(null);
        }

        if (habitacionRepository.count() == 0) {
            if (simple != null && doble != null && suite != null && economica != null && familiar != null) {
                cargarHabitaciones(simple, doble, suite, economica, familiar);
                System.out.println("✓ Habitaciones cargadas correctamente");
            } else {
                System.out.println("✗ No se pudieron cargar habitaciones porque faltan tipos de habitación");
            }
        } else {
            System.out.println("✓ Las habitaciones ya existen. Saltando carga.");
        }

        if (huespedRepository.count() == 0) {
            cargarHuespedes();
            System.out.println("✓ Huéspedes cargados correctamente");
        } else {
            System.out.println("✓ Los huéspedes ya existen. Saltando carga.");
        }

        if (servicioRepository.count() == 0) {
            cargarServicios();
            System.out.println("✓ Servicios cargados correctamente");
        } else {
            System.out.println("✓ Los servicios ya existen. Saltando carga.");
        }

        System.out.println("✓ Proceso de carga inicial finalizado");
    }

    private TipoHabitacion[] cargarTiposHabitacion() {
        TipoHabitacion simple = new TipoHabitacion();
        simple.setNombre("Habitación Simple");
        simple.setDescripcion("Habitación cómoda para una persona");
        simple.setPrecio(50);
        simple.setImagenUrl("/images/simple.jpg");
        simple.setCapacidad(1);
        simple.setCamas("Cama individual");
        simple.setAmenities("WiFi, TV, Baño privado");
        simple.setDisponible(true);
        simple = tipoHabitacionRepository.save(simple);

        TipoHabitacion doble = new TipoHabitacion();
        doble.setNombre("Habitación Doble");
        doble.setDescripcion("Habitación espaciosa para dos personas");
        doble.setPrecio(80);
        doble.setImagenUrl("/images/doble.jpg");
        doble.setCapacidad(2);
        doble.setCamas("Cama doble");
        doble.setAmenities("WiFi, TV, Aire acondicionado, Baño privado");
        doble.setDisponible(true);
        doble = tipoHabitacionRepository.save(doble);

        TipoHabitacion suite = new TipoHabitacion();
        suite.setNombre("Suite");
        suite.setDescripcion("Habitación de lujo con sala de estar");
        suite.setPrecio(120);
        suite.setImagenUrl("/images/suite.jpg");
        suite.setCapacidad(4);
        suite.setCamas("Cama King + Sofá cama");
        suite.setAmenities("WiFi, TV Smart, Aire acondicionado, Minibar, Bañera");
        suite.setDisponible(true);
        suite = tipoHabitacionRepository.save(suite);

        TipoHabitacion economica = new TipoHabitacion();
        economica.setNombre("Habitación Económica");
        economica.setDescripcion("Habitación básica y asequible");
        economica.setPrecio(30);
        economica.setImagenUrl("/images/economica.jpg");
        economica.setCapacidad(2);
        economica.setCamas("Cama individual o doble");
        economica.setAmenities("WiFi, Baño compartido");
        economica.setDisponible(true);
        economica = tipoHabitacionRepository.save(economica);

        TipoHabitacion familiar = new TipoHabitacion();
        familiar.setNombre("Habitación Familiar");
        familiar.setDescripcion("Habitación amplia ideal para familias");
        familiar.setPrecio(150);
        familiar.setImagenUrl("/images/familiar.jpg");
        familiar.setCapacidad(5);
        familiar.setCamas("2 camas dobles + 1 cama individual");
        familiar.setAmenities("WiFi, TV, Aire acondicionado, Baño privado, Nevera");
        familiar.setDisponible(true);
        familiar = tipoHabitacionRepository.save(familiar);

        return new TipoHabitacion[]{simple, doble, suite, economica, familiar};
    }

    private void cargarHabitaciones(TipoHabitacion simple, TipoHabitacion doble, TipoHabitacion suite,
                                    TipoHabitacion economica, TipoHabitacion familiar) {

        for (int i = 1; i <= 50; i++) {
            Habitacion h = new Habitacion();

            int piso = ((i - 1) / 10) + 1;
            int numeroDentroPiso = ((i - 1) % 10) + 1;
            String codigo = piso + String.format("%02d", numeroDentroPiso);

            h.setCodigo(codigo);
            h.setPiso(piso);

            if (i % 13 == 0) {
                h.setEstado("MANTENIMIENTO");
            } else {
                h.setEstado("DISPONIBLE");
            }

            if (i <= 10) {
                h.setTipoHabitacion(simple);
            } else if (i <= 20) {
                h.setTipoHabitacion(doble);
            } else if (i <= 30) {
                h.setTipoHabitacion(suite);
            } else if (i <= 40) {
                h.setTipoHabitacion(economica);
            } else {
                h.setTipoHabitacion(familiar);
            }

            if (i % 5 == 0) {
                h.setNotas("Vista exterior");
            } else if (i % 7 == 0) {
                h.setNotas("Cerca al ascensor");
            } else if (i % 13 == 0) {
                h.setNotas("En reparación");
            } else {
                h.setNotas(null);
            }

            habitacionRepository.save(h);
        }
    }

    private void cargarHuespedes() {
        for (int i = 1; i <= 10; i++) {
            Huesped h = new Huesped();
            h.setNombre("Huesped" + i);
            h.setApellido("Apellido" + i);
            h.setCorreo("h" + i + "@mail.com");
            h.setContrasena("123");
            h.setCedula("10000000" + i);
            h.setTelefono("300000000" + i);
            h.setDireccion("Calle " + i + " # 10-" + i);
            h.setPais("Colombia");
            huespedRepository.save(h);
        }
    }

    private void cargarServicios() {
        Servicio desayuno = new Servicio();
        desayuno.setNombre("Desayuno Buffet");
        desayuno.setDescripcion("Desayuno completo con opciones variadas");
        desayuno.setPrecio(15);
        desayuno.setImagenUrl("/images/desayuno.jpg");
        desayuno.setCapacidad(50);
        desayuno.setPrecioTipo("Por persona");
        desayuno.setHorario("07:00 - 10:00");
        servicioRepository.save(desayuno);

        Servicio estacionamiento = new Servicio();
        estacionamiento.setNombre("Estacionamiento");
        estacionamiento.setDescripcion("Estacionamiento seguro cubierto");
        estacionamiento.setPrecio(10);
        estacionamiento.setImagenUrl("/images/estacionamiento.jpg");
        estacionamiento.setCapacidad(0);
        estacionamiento.setPrecioTipo("Por noche");
        estacionamiento.setHorario("24 horas");
        servicioRepository.save(estacionamiento);

        Servicio spa = new Servicio();
        spa.setNombre("Servicio Spa");
        spa.setDescripcion("Masajes relajantes y tratamientos");
        spa.setPrecio(60);
        spa.setImagenUrl("/images/spa.jpg");
        spa.setCapacidad(6);
        spa.setPrecioTipo("Por sesión");
        spa.setHorario("09:00 - 20:00");
        servicioRepository.save(spa);

        Servicio limpieza = new Servicio();
        limpieza.setNombre("Limpieza Adicional");
        limpieza.setDescripcion("Limpieza diaria de la habitación");
        limpieza.setPrecio(20);
        limpieza.setImagenUrl("/images/limpieza.jpg");
        limpieza.setCapacidad(0);
        limpieza.setPrecioTipo("Por día");
        limpieza.setHorario("Bajo solicitud");
        servicioRepository.save(limpieza);

        Servicio restaurant = new Servicio();
        restaurant.setNombre("Servicio de Restaurant");
        restaurant.setDescripcion("Comidas y cenas en nuestro restaurant");
        restaurant.setPrecio(0);
        restaurant.setImagenUrl("/images/restaurant.jpg");
        restaurant.setCapacidad(100);
        restaurant.setPrecioTipo("A la carta");
        restaurant.setHorario("12:00 - 23:00");
        servicioRepository.save(restaurant);
    }
}