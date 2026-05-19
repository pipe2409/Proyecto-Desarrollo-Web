package com.example.demo;

import com.example.demo.entities.*;
import com.example.demo.repository.*;
import com.example.demo.service.ReservaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("unitTest")
@Transactional // Cada prueba se hace en una transacción y se revierte al final
class ReservaServiceIntegrationTest {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private HuespedRepository huespedRepository;

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private Habitacion habitacionDisponible;
    private Huesped huesped;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        // Buscar o crear una habitación disponible
        habitacionDisponible = habitacionRepository.findAll().stream()
                .filter(h -> "DISPONIBLE".equals(h.getEstado()))
                .findFirst()
                .orElseGet(() -> {
                    Habitacion nueva = new Habitacion();
                    nueva.setCodigo("TEST01");
                    nueva.setPiso(1);
                    nueva.setEstado("DISPONIBLE");
                    nueva.setTipoHabitacion(tipoHabitacionRepository.findAll().get(0));
                    return habitacionRepository.save(nueva);
                });

        // Buscar o crear un huésped
        huesped = huespedRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    Huesped huesped = new Huesped();
                        huesped.setNombre("Nombre Prueba");
                        huesped.setApellido("Apellido Prueba");
                        
                    UserEntity user = new UserEntity();
                        user.setUsername("h3@mail.com"); // Lo que antes era setCorreo
                        user.setPassword(passwordEncoder.encode("123")); // Encriptada para ser real
                        
                    // Asignar rol para que el usuario sea válido en el sistema
                    roleRepository.findByName("ROLE_CLIENTE").ifPresent(r -> user.getRoles().add(r));
                    
                        huesped.setUser(user);
                 huesped.setCedula("12345678");
                 return huespedRepository.save(huesped);
                });

        // Fechas de prueba
        fechaInicio = LocalDateTime.now().plusDays(10);
        fechaFin = LocalDateTime.now().plusDays(12);
    }

    // crear reserva correctamente
    @Test
    void deberiaCrearReservaCorrectamente() {
        // ARRANGE: Preparar los datos de entrada
        Integer habitacionId = habitacionDisponible.getId();
        Integer huespedId = huesped.getId();
        Integer cantidadPersonas = 2;

        // ACT: Ejecutar el método del servicio
        Reserva reservaCreada = reservaService.crearReserva(
                habitacionId, huespedId, fechaInicio, fechaFin, cantidadPersonas);

        // ASSERT: Verificar que se guardó correctamente en la base
        assertThat(reservaCreada).isNotNull();
        assertThat(reservaCreada.getId()).isNotNull();
        assertThat(reservaCreada.getEstado()).isEqualTo(EstadoReserva.PENDIENTE);
        assertThat(reservaCreada.getHabitacion().getId()).isEqualTo(habitacionId);
        assertThat(reservaCreada.getHuesped().getId()).isEqualTo(huespedId);
        assertThat(reservaCreada.getCantidadPersonas()).isEqualTo(cantidadPersonas);
        assertThat(reservaCreada.getFechaInicio()).isEqualTo(fechaInicio);
        assertThat(reservaCreada.getFechaFin()).isEqualTo(fechaFin);

        // Verificar que realmente existe en el repositorio
        Reserva reservaFromDb = reservaRepository.findById(reservaCreada.getId()).orElse(null);
        assertThat(reservaFromDb).isNotNull();
        assertThat(reservaFromDb.getEstado()).isEqualTo(EstadoReserva.PENDIENTE);

    }

    // Validar que no se pueden crear reservas con fechas inválidas (fin antes de
    // inicio)
    @Test
    void deberiaFallarCuandoFechasInvalidas() {
        // ARRANGE: fechas donde fin es antes que inicio
        LocalDateTime inicio = LocalDateTime.now().plusDays(10);
        LocalDateTime fin = LocalDateTime.now().plusDays(5); // Fin < inicio

        // ACT & ASSERT: Verificar que lanza excepción
        assertThatThrownBy(() -> reservaService.crearReserva(
                habitacionDisponible.getId(),
                huesped.getId(),
                inicio,
                fin,
                2)).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("La fecha de fin debe ser posterior");
    }

    // Validar que no se pueden crear reservas para habitaciones no disponibles
    @Test
    void deberiaFallarSiHabitacionNoDisponible() {
        // ARRANGE: Primero crear una reserva que ocupe la habitación
        reservaService.crearReserva(
                habitacionDisponible.getId(),
                huesped.getId(),
                fechaInicio,
                fechaFin,
                2);

        // Intentar crear otra reserva para la misma habitación en las mismas fechas
        // Debería fallar por disponibilidad
        assertThatThrownBy(() -> reservaService.crearReserva(
                habitacionDisponible.getId(),
                huesped.getId(),
                fechaInicio,
                fechaFin,
                2)).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no está disponible");
    }

    // Validar que el método de disponibilidad funciona correctamente
    @Test
    void deberiaVerificarDisponibilidadCorrectamente() {
        // ARRANGE: Crear una reserva que ocupe ciertas fechas
        LocalDateTime inicioOcupado = LocalDateTime.now().plusDays(20);
        LocalDateTime finOcupado = LocalDateTime.now().plusDays(25);

        reservaService.crearReserva(
                habitacionDisponible.getId(),
                huesped.getId(),
                inicioOcupado,
                finOcupado,
                2);

        // ACT: Verificar disponibilidad para diferentes fechas
        boolean disponibleFueraDelRango = reservaService.isHabitacionDisponible(
                habitacionDisponible.getId(),
                finOcupado.plusDays(1), // un día después
                finOcupado.plusDays(3));

        boolean disponibleDuranteRango = reservaService.isHabitacionDisponible(
                habitacionDisponible.getId(),
                inicioOcupado.plusDays(1), // dentro del rango ocupado
                finOcupado.minusDays(1));

        // ASSERT
        assertThat(disponibleFueraDelRango).isTrue();
        assertThat(disponibleDuranteRango).isFalse();
    }

    // cancelar reserva correctamente
    @Test
    void deberiaCancelarReservaCorrectamente() {
        // ARRANGE: Crear una reserva primero
        Reserva reserva = reservaService.crearReserva(
                habitacionDisponible.getId(),
                huesped.getId(),
                fechaInicio,
                fechaFin,
                2);

        // ACT: Cancelar la reserva (necesitamos acceso al controller o agregar método
        // en service)
        // Nota: Tu servicio no tiene método cancelar, solo el controller.
        // Para probar integración, podemos hacer:
        reserva.setEstado(EstadoReserva.CANCELADA);
        Reserva reservaCancelada = reservaService.save(reserva);

        // ASSERT
        assertThat(reservaCancelada.getEstado()).isEqualTo(EstadoReserva.CANCELADA);

        boolean disponible = reservaService.isHabitacionDisponible(
                habitacionDisponible.getId(),
                fechaInicio,
                fechaFin);
        assertThat(disponible).isTrue(); // Cancelada, debería estar disponible
    }

    // Validar que se pueden obtener reservas por huésped
    @Test
    void deberiaObtenerReservasPorHuesped() {
        // ARRANGE: Crear varias reservas para el mismo huésped
        reservaService.crearReserva(
                habitacionDisponible.getId(),
                huesped.getId(),
                fechaInicio,
                fechaFin,
                2);

        LocalDateTime otrasFechas = fechaInicio.plusDays(30);
        reservaService.crearReserva(
                habitacionDisponible.getId(),
                huesped.getId(),
                otrasFechas,
                otrasFechas.plusDays(2),
                1);

        // ACT: Buscar reservas del huésped
        List<Reserva> reservas = reservaService.findByHuesped(huesped);

        // ASSERT
        assertThat(reservas).isNotNull();
        assertThat(reservas.size()).isGreaterThanOrEqualTo(2);
        assertThat(reservas).allMatch(r -> r.getHuesped().getId().equals(huesped.getId()));
    }

    // Validar que se pueden crear reservas por tipo de habitación
    @Test
    void deberiaCrearReservaPorTipoDeHabitacion() {
        // ARRANGE: Obtener un tipo de habitación que tenga habitaciones disponibles
        TipoHabitacion tipo = tipoHabitacionRepository.findAll().stream()
                .findFirst()
                .orElseThrow();

        // ACT: Crear reserva por tipo
        Reserva reserva = reservaService.crearReservaPorTipo(
                tipo.getId(),
                huesped.getId(),
                fechaInicio,
                fechaFin,
                2);

        // ASSERT
        assertThat(reserva).isNotNull();
        assertThat(reserva.getId()).isNotNull();
        assertThat(reserva.getHabitacion().getTipoHabitacion().getId()).isEqualTo(tipo.getId());
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.PENDIENTE);
    }

    // Validar que el método para verificar reservas activas funciona correctamente
    @Test
    void deberiaVerificarSiHuespedTieneReservasActivas() {
        // ARRANGE: Crear reserva pendiente
        reservaService.crearReserva(
                habitacionDisponible.getId(),
                huesped.getId(),
                fechaInicio,
                fechaFin,
                2);

        // ACT
        boolean tieneActivas = reservaService.tieneReservasActivas(huesped.getId());

        // ASSERT
        assertThat(tieneActivas).isTrue();

        // ARRANGE 2: Cancelar la reserva
        List<Reserva> reservas = reservaService.findByHuesped(huesped);
        reservas.forEach(r -> {
            r.setEstado(EstadoReserva.CANCELADA);
            reservaService.save(r);
        });

        // ACT 2
        boolean tieneActivasDespues = reservaService.tieneReservasActivas(huesped.getId());

        // ASSERT 2
        assertThat(tieneActivasDespues).isFalse();
    }

}
