package com.example.demo;

import com.example.demo.entities.Habitacion;
import com.example.demo.entities.Huesped;
import com.example.demo.entities.ItemCuenta;
import com.example.demo.entities.Operador;
import com.example.demo.entities.Reserva;
import com.example.demo.entities.Servicio;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.CuentaHabitacionRepository;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HuespedRepository;
import com.example.demo.repository.ItemCuentaRepository;
import com.example.demo.repository.ServicioRepository;
import com.example.demo.repository.TipoHabitacionRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.entities.CuentaHabitacion;
import com.example.demo.entities.EstadoReserva;
import java.time.LocalDateTime;
import java.util.List;

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
    @Autowired
    private  ReservaRepository reservaRepository;
    @Autowired
    private ItemCuentaRepository itemCuentaRepository;
    @Autowired
    private CuentaHabitacionRepository cuentaHabitacionRepository;
    @Autowired
    private OperadorRepository operadorRepository;
   



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

        if (reservaRepository.count() == 0) {
    if (habitacionRepository.count() > 0 && huespedRepository.count() > 0) {
        cargarReservas();
        System.out.println("✓ Reservas cargadas correctamente");
    } else {
        System.out.println("✗ No se pudieron cargar reservas porque faltan habitaciones o huéspedes");
    }
    } else {
        System.out.println("✓ Las reservas ya existen. Saltando carga.");
    }

    if (cuentaHabitacionRepository.count() == 0) {
        if (reservaRepository.count() > 0 && servicioRepository.count() > 0) {
            cargarCuentasHabitacion();
            System.out.println("✓ Cuentas de habitación cargadas correctamente");
        } else {
            System.out.println("✗ No se pudieron cargar cuentas porque faltan reservas o servicios");
        }
        } else {
        System.out.println("✓ Las cuentas ya existen. Saltando carga.");
        }
        
    if (operadorRepository.count() == 0) {
    cargarOperadores();
        System.out.println("✓ Operadores cargados correctamente");
    } else {
        System.out.println("✓ Los operadores ya existen. Saltando carga.");
    }
    }

    private void cargarCuentasHabitacion() {
    List<Reserva> reservas = reservaRepository.findAll();
    List<Servicio> servicios = servicioRepository.findAll();

    for (int i = 0; i < 5; i++) {
        CuentaHabitacion cuenta = new CuentaHabitacion();
        cuenta.setReserva(reservas.get(i));
        cuenta.setTotal(0); // se calculará al agregar items
        cuenta = cuentaHabitacionRepository.save(cuenta);

        cargarItemsCuenta(cuenta, servicios, i);
    }

}

private void cargarOperadores() {
    String[][] datos = {
        {"admin@hotel.com",    "admin123"},
        {"recepcion@hotel.com","recep456"},
        {"caja@hotel.com",     "caja789"},
        {"supervisor@hotel.com","super321"},
        {"soporte@hotel.com",  "soporte000"}
    };

    for (String[] d : datos) {
        Operador op = new Operador();
        op.setCorreo(d[0]);
        op.setContrasena(d[1]);
        operadorRepository.save(op);
    }
}

private void cargarItemsCuenta(CuentaHabitacion cuenta, List<Servicio> servicios, int index) {
    int totalCuenta = 0;

    // Cada cuenta tendrá entre 1 y 3 items usando servicios distintos
    int[][] combinaciones = {
        {0, 1},       // cuenta 0: servicios 0 y 1
        {1, 2},       // cuenta 1: servicios 1 y 2
        {0, 2, 3},    // cuenta 2: servicios 0, 2 y 3
        {3, 4},       // cuenta 3: servicios 3 y 4
        {0, 1, 4}     // cuenta 4: servicios 0, 1 y 4
    };

    for (int servicioIndex : combinaciones[index]) {
        Servicio servicio = servicios.get(servicioIndex);
        int cantidad = index + 1; // cantidad varía por cuenta (1 a 5)
        int subtotal = servicio.getPrecio() * cantidad;

        ItemCuenta item = new ItemCuenta();
        item.setCuentaHabitacion(cuenta);
        item.setServicio(servicio);
        item.setCantidad(cantidad);
        item.setSubtotal(subtotal);
        itemCuentaRepository.save(item);

        totalCuenta += subtotal;
    }

    // Actualizar el total de la cuenta
    cuenta.setTotal(totalCuenta);
    cuentaHabitacionRepository.save(cuenta);
}



    
    private TipoHabitacion[] cargarTiposHabitacion() {
        TipoHabitacion simple = new TipoHabitacion();
        simple.setNombre("Habitación Simple");
        simple.setDescripcion("Habitación cómoda para una persona");
        simple.setPrecio(50);
        simple.setImagenUrl("https://plus.unsplash.com/premium_photo-1675615667752-2ccda7042e7e?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
        simple.setCapacidad(1);
        simple.setCamas("Cama individual");
        simple.setAmenities("WiFi, TV, Baño privado");
        simple = tipoHabitacionRepository.save(simple);

        TipoHabitacion doble = new TipoHabitacion();
        doble.setNombre("Habitación Doble");
        doble.setDescripcion("Habitación espaciosa para dos personas");
        doble.setPrecio(80);
        doble.setImagenUrl("https://images.unsplash.com/photo-1673687782286-674e29c9bf9e?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8aGFiaXRhY2lvbiUyMGRvYmxlfGVufDB8fDB8fHww");
        doble.setCapacidad(2);
        doble.setCamas("Cama doble");
        doble.setAmenities("WiFi, TV, Aire acondicionado, Baño privado");
        doble = tipoHabitacionRepository.save(doble);

        TipoHabitacion suite = new TipoHabitacion();
        suite.setNombre("Suite");
        suite.setDescripcion("Habitación de lujo con sala de estar");
        suite.setPrecio(120);
        suite.setImagenUrl("https://plus.unsplash.com/premium_photo-1661923086373-73176f7c004a?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8U3VpdGV8ZW58MHx8MHx8fDA%3D");
        suite.setCapacidad(4);
        suite.setCamas("Cama King + Sofá cama");
        suite.setAmenities("WiFi, TV Smart, Aire acondicionado, Minibar, Bañera");
        suite = tipoHabitacionRepository.save(suite);

        TipoHabitacion economica = new TipoHabitacion();
        economica.setNombre("Habitación Económica");
        economica.setDescripcion("Habitación básica y asequible");
        economica.setPrecio(30);
        economica.setImagenUrl("https://images.unsplash.com/photo-1765464184843-105e144bd54b?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTF8fGhhYml0YWNpb24lMjBzaW1wbGV8ZW58MHx8MHx8fDA%3D");
        economica.setCapacidad(2);
        economica.setCamas("Cama individual o doble");
        economica.setAmenities("WiFi, Baño compartido");
        economica = tipoHabitacionRepository.save(economica);

        TipoHabitacion familiar = new TipoHabitacion();
        familiar.setNombre("Habitación Familiar");
        familiar.setDescripcion("Habitación amplia ideal para familias");
        familiar.setPrecio(150);
        familiar.setImagenUrl("https://plus.unsplash.com/premium_photo-1674035036549-67b8ad6d0be3?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8aGFiaXRhY2lvbiUyMGZhbWlsaWFyfGVufDB8fDB8fHww");
        familiar.setCapacidad(5);
        familiar.setCamas("2 camas dobles + 1 cama individual");
        familiar.setAmenities("WiFi, TV, Aire acondicionado, Baño privado, Nevera");
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
    Object[][] datos = {
        {"Desayuno Buffet",         "Desayuno completo con opciones variadas",         15,  "https://media.istockphoto.com/id/531306158/es/foto/disfrute-de-desayuno.webp?a=1&b=1&s=612x612&w=0&k=20&c=RlHxf3SAmvnHzJmzs0kgQgGi9h_nVxxdVz7YfIgBj2c=",        50,  "Por persona",  "07:00 - 10:00"},
        {"Estacionamiento",         "Estacionamiento seguro cubierto",                 10,  "https://plus.unsplash.com/premium_photo-1661902046698-40bba703f396?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8ZXN0YWNpb25hbWllbnRvfGVufDB8fDB8fHww",  0,  "Por noche",    "24 horas"},
        {"Servicio Spa",            "Masajes relajantes y tratamientos",               60,  "https://plus.unsplash.com/premium_photo-1723867490491-10519f8ed969?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NXx8c2VydmljaW8lMjBzcGF8ZW58MHx8MHx8fDA%3D",              6,  "Por sesión",   "09:00 - 20:00"},
        {"Limpieza Adicional",      "Limpieza diaria de la habitación",                20,  "https://plus.unsplash.com/premium_photo-1663011218145-c1d0c3ba3542?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8bGltcGllemF8ZW58MHx8MHx8fDA%3D",         0,  "Por día",      "Bajo solicitud"},
        {"Servicio de Restaurant",  "Comidas y cenas en nuestro restaurante",           0,  "https://plus.unsplash.com/premium_photo-1661883237884-263e8de8869b?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8cmVzdGF1cmFudGV8ZW58MHx8MHx8fDA%3D",     100,  "A la carta",   "12:00 - 23:00"},
        {"Piscina",                 "Acceso a piscina climatizada",                    12,  "https://plus.unsplash.com/premium_photo-1664304879163-73ac6d6d942b?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8cGlzY2luYSUyMGNsaW1hdGl6YWRhfGVufDB8fDB8fHww",         80,  "Por persona",  "08:00 - 20:00"},
        {"Gimnasio",                "Acceso a gimnasio equipado",                       8,  "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Z2ltbmFzaW98ZW58MHx8MHx8fDA%3D",        30,  "Por día",      "06:00 - 22:00"},
        {"Lavandería",              "Servicio de lavado y planchado de ropa",          18,  "https://plus.unsplash.com/premium_photo-1664372899525-d99a419fd21a?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8bGF2YW5kZXJpYXxlbnwwfHwwfHx8MA%3D%3D",       0,  "Por entrega",  "08:00 - 18:00"},
        {"Transfer Aeropuerto",     "Traslado desde/hacia el aeropuerto",              35,  "https://images.unsplash.com/photo-1724479839764-65981526641d?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8bWluaXZhbnxlbnwwfHwwfHx8MA%3D%3D",         4,  "Por viaje",    "Previa reserva"},
        {"Bar Nocturno",            "Cócteles y bebidas en el bar del hotel",           0,  "https://images.unsplash.com/photo-1597290282695-edc43d0e7129?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8YmFyfGVufDB8fDB8fHww",             60,  "A la carta",   "18:00 - 02:00"},
        {"Sala de Conferencias",    "Sala equipada para reuniones y eventos",          80,  "https://images.unsplash.com/photo-1582653291997-079a1c04e5a1?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8c2FsYSUyMGNvbmZlcmVuY2lhfGVufDB8fDB8fHww",            20,  "Por hora",     "08:00 - 20:00"},
        {"Servicio a la Habitación","Comidas y bebidas enviadas a tu habitación",       5,  "https://plus.unsplash.com/premium_photo-1661392877411-8a4c5ba70462?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8c2VydmljaW8lMjBhJTIwbGElMjBoYWJpdGFjaW9ufGVufDB8fDB8fHww",     0,  "Por pedido",   "07:00 - 23:00"},
        {"Tour Ciudad",             "Recorrido guiado por la ciudad",                  25,  "https://plus.unsplash.com/premium_photo-1718146019714-a7a0ab9e8e8d?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8dG91cnxlbnwwfHwwfHx8MA%3D%3D",            15,  "Por persona",  "09:00 - 13:00"},
        {"Alquiler Bicicletas",     "Bicicletas disponibles para explorar",            10,  "https://images.unsplash.com/photo-1561840884-9dda41ed54e4?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YmljaWNsZXRhc3xlbnwwfHwwfHx8MA%3D%3D",      10,  "Por hora",     "07:00 - 19:00"},
        {"Cuidado de Niños",        "Servicio de niñera certificada",                  20,  "https://plus.unsplash.com/premium_photo-1711381022854-e439a13807d1?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8bmklQzMlQjFlcnxlbnwwfHwwfHx8MA%3D%3D",            5,  "Por hora",     "08:00 - 22:00"},
        {"Masaje en Habitación",    "Masaje a domicilio en tu habitación",             70,  "https://images.unsplash.com/photo-1515377905703-c4788e51af15?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8bWFzYWplfGVufDB8fDB8fHww",           2,  "Por sesión",   "Previa reserva"},
        {"Alquiler de Autos",       "Autos disponibles para movilizarse",              50,  "https://images.unsplash.com/photo-1565043666747-69f6646db940?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YXV0b3MlMjBhbHF1aWxlcnxlbnwwfHwwfHx8MA%3D%3D",            5,  "Por día",      "08:00 - 18:00"},
        {"Clases de Yoga",          "Sesiones de yoga guiadas al aire libre",          15,  "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8eW9nYXxlbnwwfHwwfHx8MA%3D%3D",            12,  "Por sesión",   "07:00 - 08:00"},
        {"Servicio de Cuna",        "Cuna adicional para bebés en la habitación",       8,  "https://images.unsplash.com/photo-1458731909820-5850bdcaee0b?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Y3VuYXxlbnwwfHwwfHx8MA%3D%3D",             0,  "Por noche",    "Bajo solicitud"},
        {"Caja de Seguridad",       "Alquiler de caja fuerte en recepción",             5,  "https://media.istockphoto.com/id/2176074369/es/foto/primer-plano-de-la-mano-de-una-persona-que-introduce-una-contrase%C3%B1a-para-desbloquear-y-abrir.webp?a=1&b=1&s=612x612&w=0&k=20&c=vFoX7lLPdZoRvWtsbDoNC06X6dUunHkMs1HDb0K8HdE=",             0,  "Por día",      "24 horas"}
    };

    for (Object[] d : datos) {
        Servicio s = new Servicio();
        s.setNombre((String) d[0]);
        s.setDescripcion((String) d[1]);
        s.setPrecio((int) d[2]);
        s.setImagenUrl((String) d[3]);
        s.setCapacidad((int) d[4]);
        s.setPrecioTipo((String) d[5]);
        s.setHorario((String) d[6]);
        servicioRepository.save(s);
    }
}
private void cargarReservas() {
    List<Habitacion> habitaciones = habitacionRepository.findAll();
    List<Huesped> huespedes = huespedRepository.findAll();

    Object[][] datos = {
        // fechaInicio (año,mes,dia,hora,min), fechaFin, personas, estado, huesped idx, habitacion idx
        {2025, 6,  1, 14, 0,   2025, 6,  5, 12, 0,   1, "CONFIRMADA",  0,  0},
        {2025, 6, 10, 14, 0,   2025, 6, 15, 12, 0,   2, "PENDIENTE",   1, 10},
        {2025, 7,  1, 14, 0,   2025, 7,  4, 12, 0,   3, "PENDIENTE",   2, 20},
        {2025, 5,  1, 14, 0,   2025, 5,  4, 12, 0,   2, "CANCELADA",   3, 30},
        {2025, 4, 20, 14, 0,   2025, 4, 25, 12, 0,   4, "CONFIRMADA",  4, 40},
        {2025, 8,  5, 14, 0,   2025, 8, 10, 12, 0,   2, "PENDIENTE",   5,  1},
        {2025, 9, 12, 14, 0,   2025, 9, 14, 12, 0,   1, "CONFIRMADA",  6, 11},
        {2025, 3, 15, 14, 0,   2025, 3, 20, 12, 0,   3, "FINALIZADA",  7, 21},
        {2025,10,  1, 14, 0,   2025,10,  7, 12, 0,   5, "PENDIENTE",   8, 31},
        {2025,11, 20, 14, 0,   2025,11, 25, 12, 0,   2, "CONFIRMADA",  9, 41},
        {2025, 6, 18, 14, 0,   2025, 6, 22, 12, 0,   1, "CANCELADA",   0,  2},
        {2025, 7, 10, 14, 0,   2025, 7, 12, 12, 0,   2, "PENDIENTE",   1, 12},
        {2025, 8, 25, 14, 0,   2025, 8, 30, 12, 0,   4, "CONFIRMADA",  2, 22},
        {2025, 9,  5, 14, 0,   2025, 9,  8, 12, 0,   3, "FINALIZADA",  3, 32},
        {2025,12,  1, 14, 0,   2025,12,  5, 12, 0,   2, "PENDIENTE",   4, 42},
        {2025, 5, 10, 14, 0,   2025, 5, 13, 12, 0,   1, "FINALIZADA",  5,  3},
        {2025,10, 15, 14, 0,   2025,10, 18, 12, 0,   3, "CONFIRMADA",  6, 13},
        {2025,11,  5, 14, 0,   2025,11,  9, 12, 0,   2, "CANCELADA",   7, 23},
        {2025, 4,  1, 14, 0,   2025, 4,  6, 12, 0,   5, "FINALIZADA",  8, 33},
        {2025,12, 20, 14, 0,   2025,12, 28, 12, 0,   4, "PENDIENTE",   9, 43},
    };

    EstadoReserva[] estados = EstadoReserva.values();

    for (Object[] d : datos) {
        Reserva r = new Reserva();
        r.setFechaInicio(LocalDateTime.of((int)d[0],(int)d[1],(int)d[2],(int)d[3],(int)d[4]));
        r.setFechaFin(LocalDateTime.of((int)d[5],(int)d[6],(int)d[7],(int)d[8],(int)d[9]));
        r.setCantidadPersonas((int) d[10]);
        r.setEstado(EstadoReserva.valueOf((String) d[11]));
        r.setHuesped(huespedes.get((int) d[12]));
        r.setHabitacion(habitaciones.get((int) d[13]));
        reservaRepository.save(r);
    }
}
    
}