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
}