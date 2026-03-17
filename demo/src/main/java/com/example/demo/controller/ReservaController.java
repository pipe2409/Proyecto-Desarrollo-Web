package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;  
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/crear")
    public String crear(@RequestParam Integer habitacionId,
                        @RequestParam String fechaInicio,
                        @RequestParam String fechaFin,
                        @RequestParam Integer cantidadPersonas,
                        HttpSession session,
                        RedirectAttributes ra) {

        // Obtener el huésped de la sesión
        Huesped huesped = (Huesped) session.getAttribute("huesped");

        if (huesped == null) {
            return "redirect:/iniciar-sesion";
        }

        try {
            LocalDateTime inicio = LocalDate.parse(fechaInicio).atStartOfDay();
            LocalDateTime fin    = LocalDate.parse(fechaFin).atStartOfDay();

            reservaService.crearReserva(
                habitacionId,
                huesped.getId(),
                inicio,
                fin,
                cantidadPersonas
            );

            return "redirect:/habitaciones/reservar?exito=1";

        } catch (Exception e) {
            ra.addFlashAttribute("err", e.getMessage());
            return "redirect:/habitaciones/reservar?error=1";
        }
    }
}