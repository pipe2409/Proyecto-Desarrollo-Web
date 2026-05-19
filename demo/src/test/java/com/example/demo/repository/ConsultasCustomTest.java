package com.example.demo.repository;

import com.example.demo.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("unitTest")
public class ConsultasCustomTest {

    @Autowired private HabitacionRepository habitacionRepository;
    @Autowired private ServicioRepository servicioRepository;
    @Autowired private HuespedRepository huespedRepository;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private TipoHabitacionRepository tipoHabitacionRepository;

    @BeforeEach
    void setUp() {
        // Datos de prueba mínimos para las consultas
        TipoHabitacion economico = new TipoHabitacion();
        economico.setNombre("Económica");
        economico.setPrecio(50);
        economico.setCapacidad(2);
        tipoHabitacionRepository.save(economico);

        Habitacion h1 = new Habitacion();
        h1.setCodigo("101");
        h1.setPiso(1);
        h1.setEstado("DISPONIBLE");
        h1.setTipoHabitacion(economico);
        habitacionRepository.save(h1);

        Huesped huesped = new Huesped();
        huesped.setNombre("Juan");
        huesped.setApellido("Perez");
        UserEntity user = new UserEntity();
        user.setUsername("juan@gmail.com");
        user.setPassword("123");
        huesped.setUser(user);
        huesped.setCedula("12345678");
        huesped.setTelefono("3000000000");
        huesped.setDireccion("Calle Falsa 123");
        huesped.setNacionalidad("Colombia");
        huespedRepository.save(huesped);

        Servicio s1 = new Servicio();
        s1.setNombre("Desayuno");
        s1.setDescripcion("Desayuno Buffet");
        s1.setPrecio(10);
        s1.setPrecioTipo("Por persona");
        s1.setHorario("07:00 - 10:00");
        s1.setCapacidad(50);
        s1.setImagenUrl("https://example.com/desayuno.jpg");
        servicioRepository.save(s1);
    }

    @Test
    void test1_findHabitacionesPorEstadoYPiso_JPQL() {
        // Act
        List<Habitacion> resultado = habitacionRepository.findByEstadoYPiso("DISPONIBLE", 1);

        // Assert
        assertThat(resultado).isNotEmpty();
        assertThat(resultado.get(0).getCodigo()).isEqualTo("101");
    }

    @Test
    void test2_buscarServiciosEconomicos_NativeSQL() {
        // Act
        List<Servicio> resultado = servicioRepository.buscarEconomicosPorTipoNative(15, "Por persona");

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Desayuno");
    }

    @Test
    void test3_buscarHuespedesPorDominioCorreo_JPQL() {
        // Act
        List<Huesped> gmailUsers = huespedRepository.buscarPorDominioCorreo("gmail.com");

        // Assert
        assertThat(gmailUsers).hasSize(1);
        assertThat(gmailUsers.get(0).getUser().getUsername()).contains("gmail.com");
    }

    @Test
    void test4_contarReservasConfirmadas_JPQL() {
        // Arrange: Crear una reserva confirmada
        Huesped h = huespedRepository.findAll().get(0);
        Habitacion hab = habitacionRepository.findAll().get(0);
        
        Reserva r = new Reserva();
        r.setHuesped(h);
        r.setHabitacion(hab);
        r.setFechaInicio(LocalDateTime.now());
        r.setFechaFin(LocalDateTime.now().plusDays(3));
        r.setCantidadPersonas(1);
        r.setEstado(EstadoReserva.CONFIRMADA);
        reservaRepository.save(r);

        // Act
        Long conteo = reservaRepository.contarReservasConfirmadas(h.getId());

        // Assert
        assertThat(conteo).isEqualTo(1L);
    }

    @Test
    void test5_encontrarTipoMasBaratoNative_NativeSQL() {
        // Arrange: El setup ya tiene uno de precio 50 y capacidad 2
        
        // Act
        Optional<TipoHabitacion> resultado = tipoHabitacionRepository.encontrarMasBaratoParaCapacidadNative(2);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Económica");
        assertThat(resultado.get().getPrecio()).isEqualTo(50);
    }
}