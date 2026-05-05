package com.example.demo.controller;

import com.example.demo.entities.Reserva;
import com.example.demo.entities.Habitacion;
import com.example.demo.service.ReservaService;
import com.example.demo.service.HuespedService;
import com.example.demo.service.HabitacionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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

    // ✅ 1. listar reservas
    @Test
    void testGetAll() throws Exception {
        when(reservaService.findAll()).thenReturn(List.of(new Reserva()));

        mockMvc.perform(get("/api/reservas/admin"))
                .andExpect(status().isOk());
    }

    // ✅ 2. obtener por id OK
    @Test
    void testGetById() throws Exception {
        Reserva r = new Reserva();
        r.setId(1);

        when(reservaService.findById(1)).thenReturn(r);

        mockMvc.perform(get("/api/reservas/admin/1"))
                .andExpect(status().isOk());
    }

    // ❌ 3. obtener por id error
    @Test
    void testGetByIdError() throws Exception {
        when(reservaService.findById(1)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/reservas/admin/1"))
                .andExpect(status().isNotFound());
    }

    // ✅ 4. cancelar reserva
    @Test
    void testCancelar() throws Exception {
        Reserva r = new Reserva();
        r.setHabitacion(new Habitacion());

        when(reservaService.findById(1)).thenReturn(r);

        mockMvc.perform(put("/api/reservas/1/cancelar"))
                .andExpect(status().isOk());
    }

    // ❌ 5. cancelar error
   @Test
void testFinalizarReserva() throws Exception {
    when(reservaService.finalizarReserva(1))
            .thenReturn(Map.of("ok", "Reserva finalizada correctamente"));

    mockMvc.perform(put("/api/reservas/1/finalizar"))
            .andExpect(status().isOk());
}
}