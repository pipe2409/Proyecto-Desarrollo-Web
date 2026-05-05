package com.example.demo.controller;

import com.example.demo.entities.Reserva;
import com.example.demo.service.ReservaService;
import com.example.demo.service.HuespedService;
import com.example.demo.service.HabitacionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservaService reservaService;

    @MockBean
    private HuespedService huespedService;

    @MockBean
    private HabitacionService habitacionService;

    // 1. GET - listar reservas
    @Test
    void testGetAll() throws Exception {
        when(reservaService.findAll()).thenReturn(List.of(new Reserva()));

        mockMvc.perform(get("/api/reservas/admin"))
                .andExpect(status().isOk());
    }

    // 2. GET - obtener reserva por id
    @Test
    void testGetById() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setId(1);

        when(reservaService.findById(1)).thenReturn(reserva);

        mockMvc.perform(get("/api/reservas/admin/1"))
                .andExpect(status().isOk());
    }

    // 3. POST - crear reserva
    @Test
    void testCrearReserva() throws Exception {
        String json = """
        {
            "habitacionId": 1,
            "huespedId": 1,
            "cantidadPersonas": 2,
            "fechaInicio": "2026-05-10",
            "fechaFin": "2026-05-12"
        }
        """;

        when(reservaService.isHabitacionDisponible(
                eq(1),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(true);

        mockMvc.perform(post("/api/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    // 4. PUT - finalizar reserva
    @Test
    void testFinalizarReserva() throws Exception {
        when(reservaService.finalizarReserva(1))
                .thenReturn(Map.of("ok", "Reserva finalizada correctamente"));

        mockMvc.perform(put("/api/reservas/1/finalizar"))
                .andExpect(status().isOk());
    }

    // 5. DELETE - eliminar reserva
    @Test
    void testEliminarReserva() throws Exception {
        doNothing().when(reservaService).deleteById(1);

        mockMvc.perform(delete("/api/reservas/admin/1"))
                .andExpect(status().isNoContent());
    }
}