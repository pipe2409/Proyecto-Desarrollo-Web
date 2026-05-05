package com.example.demo.service;

import com.example.demo.entities.*;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HuespedRepository;
import com.example.demo.repository.ReservaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private HuespedRepository huespedRepository;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    @Test
    void findAll_debeRetornarReservas() {
        when(reservaRepository.findAll()).thenReturn(List.of(new Reserva()));

        List<Reserva> resultado = reservaService.findAll();

        assertEquals(1, resultado.size());
        verify(reservaRepository).findAll();
    }

    @Test
    void findById_debeRetornarReserva() {
        Reserva reserva = new Reserva();
        reserva.setId(1);

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        Reserva resultado = reservaService.findById(1);

        assertEquals(1, resultado.getId());
        verify(reservaRepository).findById(1);
    }

    @Test
    void findById_debeLanzarErrorSiNoExiste() {
        when(reservaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservaService.findById(99));

        verify(reservaRepository).findById(99);
    }

    @Test
    void isHabitacionDisponible_debeRetornarTrueSiNoHayReservas() {
        LocalDateTime inicio = LocalDateTime.of(2026, 5, 10, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 5, 12, 0, 0);

        when(reservaRepository.findByHabitacionId(1)).thenReturn(List.of());

        boolean disponible = reservaService.isHabitacionDisponible(1, inicio, fin);

        assertTrue(disponible);
        verify(reservaRepository).findByHabitacionId(1);
    }

    @Test
    void isHabitacionDisponible_debeRetornarFalseSiHayConflicto() {
        LocalDateTime inicioExistente = LocalDateTime.of(2026, 5, 10, 0, 0);
        LocalDateTime finExistente = LocalDateTime.of(2026, 5, 15, 0, 0);

        Reserva reservaExistente = new Reserva();
        reservaExistente.setFechaInicio(inicioExistente);
        reservaExistente.setFechaFin(finExistente);
        reservaExistente.setEstado(EstadoReserva.CONFIRMADA);

        LocalDateTime nuevoInicio = LocalDateTime.of(2026, 5, 12, 0, 0);
        LocalDateTime nuevoFin = LocalDateTime.of(2026, 5, 13, 0, 0);

        when(reservaRepository.findByHabitacionId(1)).thenReturn(List.of(reservaExistente));

        boolean disponible = reservaService.isHabitacionDisponible(1, nuevoInicio, nuevoFin);

        assertFalse(disponible);
        verify(reservaRepository).findByHabitacionId(1);
    }

    @Test
    void deberiaCrearReservaCorrectamente() {
        Habitacion habitacion = new Habitacion();
        habitacion.setId(1);

        Huesped huesped = new Huesped();
        huesped.setId(1);

        LocalDateTime inicio = LocalDateTime.now().plusDays(10);
        LocalDateTime fin = LocalDateTime.now().plusDays(12);

        Reserva reservaGuardada = new Reserva();
        reservaGuardada.setId(1);
        reservaGuardada.setHabitacion(habitacion);
        reservaGuardada.setHuesped(huesped);
        reservaGuardada.setFechaInicio(inicio);
        reservaGuardada.setFechaFin(fin);
        reservaGuardada.setCantidadPersonas(2);
        reservaGuardada.setEstado(EstadoReserva.PENDIENTE);

        when(habitacionRepository.findById(1)).thenReturn(Optional.of(habitacion));
        when(huespedRepository.findById(1)).thenReturn(Optional.of(huesped));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        Reserva resultado = reservaService.crearReserva(1, 1, inicio, fin, 2);

        assertEquals(EstadoReserva.PENDIENTE, resultado.getEstado());
        assertEquals(1, resultado.getHabitacion().getId());
        assertEquals(1, resultado.getHuesped().getId());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void deberiaObtenerReservasPorHuesped() {
        Huesped huesped = new Huesped();
        huesped.setId(1);

        Reserva r1 = new Reserva();
        r1.setHuesped(huesped);

        Reserva r2 = new Reserva();
        r2.setHuesped(huesped);

        when(reservaRepository.findByHuesped(huesped)).thenReturn(List.of(r1, r2));

        List<Reserva> resultado = reservaService.findByHuesped(huesped);

        assertEquals(2, resultado.size());
        verify(reservaRepository).findByHuesped(huesped);
    }

    @Test
    void deberiaCrearReservaPorTipoDeHabitacion() {
        TipoHabitacion tipo = new TipoHabitacion();
        tipo.setId(1);

        Habitacion habitacion = new Habitacion();
        habitacion.setId(1);
        habitacion.setTipoHabitacion(tipo);

        Huesped huesped = new Huesped();
        huesped.setId(1);

        LocalDateTime inicio = LocalDateTime.now().plusDays(10);
        LocalDateTime fin = LocalDateTime.now().plusDays(12);

        Reserva reservaGuardada = new Reserva();
        reservaGuardada.setId(1);
        reservaGuardada.setHabitacion(habitacion);
        reservaGuardada.setHuesped(huesped);
        reservaGuardada.setEstado(EstadoReserva.PENDIENTE);

        when(huespedRepository.findById(1)).thenReturn(Optional.of(huesped));
        when(habitacionRepository.findByTipoHabitacion_Id(1)).thenReturn(List.of(habitacion));
        when(reservaRepository.findByHabitacionId(1)).thenReturn(List.of());
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        Reserva resultado = reservaService.crearReservaPorTipo(1, 1, inicio, fin, 2);

        assertEquals(EstadoReserva.PENDIENTE, resultado.getEstado());
        assertEquals(1, resultado.getHabitacion().getTipoHabitacion().getId());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void deberiaVerificarSiHuespedTieneReservasActivas() {
        Huesped huesped = new Huesped();
        huesped.setId(1);

        Reserva activa = new Reserva();
        activa.setEstado(EstadoReserva.PENDIENTE);

        when(huespedRepository.findById(1)).thenReturn(Optional.of(huesped));
        when(reservaRepository.findByHuesped(huesped)).thenReturn(List.of(activa));

        boolean resultado = reservaService.tieneReservasActivas(1);

        assertTrue(resultado);
        verify(huespedRepository).findById(1);
        verify(reservaRepository).findByHuesped(huesped);
    }

    @Test
    void deberiaCancelarReservaCorrectamente() {
        Reserva reserva = new Reserva();
        reserva.setId(1);
        reserva.setEstado(EstadoReserva.PENDIENTE);

        reserva.setEstado(EstadoReserva.CANCELADA);

        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Reserva resultado = reservaService.save(reserva);

        assertEquals(EstadoReserva.CANCELADA, resultado.getEstado());
        verify(reservaRepository).save(reserva);
    }
}