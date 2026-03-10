package com.example.demo;

import com.example.demo.entities.Habitacion;
import com.example.demo.entities.Servicio;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.ServicioRepository;
import com.example.demo.repository.TipoHabitacionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HabitacionRepository habitacionRepository;
    private final ServicioRepository servicioRepository;

    public DataLoader(TipoHabitacionRepository tipoHabitacionRepository,
                      HabitacionRepository habitacionRepository,
                      ServicioRepository servicioRepository) {
        this.tipoHabitacionRepository = tipoHabitacionRepository;
        this.habitacionRepository = habitacionRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    public void run(String... args) {
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        TipoHabitacion simple = null;
        TipoHabitacion doble = null;
        TipoHabitacion suite = null;

        if (tipoHabitacionRepository.count() == 0) {
            TipoHabitacion[] tipos = cargarTiposHabitacion();
            simple = tipos[0];
            doble = tipos[1];
            suite = tipos[2];
            System.out.println("✓ Tipos de habitación cargados correctamente");
        } else {
            System.out.println("✓ Los tipos de habitación ya existen. Saltando carga.");
        }

        if (habitacionRepository.count() == 0) {
            if (simple == null || doble == null || suite == null) {
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
            }

            if (simple != null && doble != null && suite != null) {
                cargarHabitaciones(simple, doble, suite);
                System.out.println("✓ Habitaciones cargadas correctamente");
            } else {
                System.out.println("✗ No se pudieron cargar habitaciones porque faltan tipos de habitación");
            }
        } else {
            System.out.println("✓ Las habitaciones ya existen. Saltando carga.");
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
        tipoHabitacionRepository.save(economica);

        return new TipoHabitacion[]{simple, doble, suite};
    }

    private void cargarHabitaciones(TipoHabitacion simple, TipoHabitacion doble, TipoHabitacion suite) {
        Habitacion h1 = new Habitacion();
        h1.setCodigo("101");
        h1.setPiso(1);
        h1.setEstado("DISPONIBLE");
        h1.setTipoHabitacion(simple);
        h1.setNotas("Vista a la calle");
        habitacionRepository.save(h1);

        Habitacion h2 = new Habitacion();
        h2.setCodigo("102");
        h2.setPiso(1);
        h2.setEstado("DISPONIBLE");
        h2.setTipoHabitacion(doble);
        h2.setNotas(null);
        habitacionRepository.save(h2);

        Habitacion h3 = new Habitacion();
        h3.setCodigo("103");
        h3.setPiso(1);
        h3.setEstado("DISPONIBLE");
        h3.setTipoHabitacion(suite);
        h3.setNotas("Habitación de lujo");
        habitacionRepository.save(h3);

        Habitacion h4 = new Habitacion();
        h4.setCodigo("201");
        h4.setPiso(2);
        h4.setEstado("DISPONIBLE");
        h4.setTipoHabitacion(doble);
        h4.setNotas(null);
        habitacionRepository.save(h4);

        Habitacion h5 = new Habitacion();
        h5.setCodigo("202");
        h5.setPiso(2);
        h5.setEstado("DISPONIBLE");
        h5.setTipoHabitacion(simple);
        h5.setNotas("Con balcón");
        habitacionRepository.save(h5);

        Habitacion h6 = new Habitacion();
        h6.setCodigo("203");
        h6.setPiso(2);
        h6.setEstado("MANTENIMIENTO");
        h6.setTipoHabitacion(simple);
        h6.setNotas("En reparación");
        habitacionRepository.save(h6);

        Habitacion h7 = new Habitacion();
        h7.setCodigo("301");
        h7.setPiso(3);
        h7.setEstado("DISPONIBLE");
        h7.setTipoHabitacion(suite);
        h7.setNotas(null);
        habitacionRepository.save(h7);

        Habitacion h8 = new Habitacion();
        h8.setCodigo("302");
        h8.setPiso(3);
        h8.setEstado("DISPONIBLE");
        h8.setTipoHabitacion(doble);
        h8.setNotas("Vista al parque");
        habitacionRepository.save(h8);

        Habitacion h9 = new Habitacion();
        h9.setCodigo("303");
        h9.setPiso(3);
        h9.setEstado("DISPONIBLE");
        h9.setTipoHabitacion(simple);
        h9.setNotas(null);
        habitacionRepository.save(h9);
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