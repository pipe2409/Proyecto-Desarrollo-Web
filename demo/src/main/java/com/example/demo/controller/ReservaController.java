package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.entities.Reserva;
import com.example.demo.service.ReservaService;
import com.example.demo.service.HuespedService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;  
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.ui.Model;
import java.util.List;



@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;
    @Autowired
    private HuespedService huespedService;
   
   
    

    @GetMapping("/mis-reservas")
    public String misReservas(Model model, HttpSession session) {
        Integer huespedId = (Integer) session.getAttribute("huespedId");
        
        if (huespedId == null) return "redirect:/iniciar-sesion";
        
        Huesped huesped = huespedService.findById(huespedId);
        List<Reserva> reservas = reservaService.findByHuesped(huesped);
        model.addAttribute("reservas", reservas);
        return "mis-reservas";
    }
    @PostMapping("/crear")
    public String crear(@RequestParam Integer habitacionId,
                        @RequestParam String fechaInicio,
                        @RequestParam String fechaFin,
                        @RequestParam Integer cantidadPersonas,
                        HttpSession session,
                        RedirectAttributes ra) {

        Integer huespedId = (Integer) session.getAttribute("huespedId"); // ← cambia esto

        if (huespedId == null) {
            return "redirect:/iniciar-sesion";
        }

        try {
            LocalDateTime inicio = LocalDate.parse(fechaInicio).atStartOfDay();
            LocalDateTime fin    = LocalDate.parse(fechaFin).atStartOfDay();

            reservaService.crearReserva(habitacionId, huespedId, inicio, fin, cantidadPersonas);

            return "redirect:/habitaciones/reservar?exito=1";

        } catch (Exception e) {
            ra.addFlashAttribute("err", e.getMessage());
            return "redirect:/habitaciones/reservar?error=1";
        }
    }
}