package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.entities.Reserva;
import com.example.demo.service.ReservaService;
import com.example.demo.service.HuespedService;
import com.example.demo.service.HabitacionService;
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
    @Autowired
    private HabitacionService habitacionService;

   // Mostrar formulario editar
@GetMapping("/admin/editar/{id}")
public String editarReserva(@PathVariable Integer id, Model model) {
    Reserva reserva = reservaService.findById(id);
    model.addAttribute("reserva", reserva);
    model.addAttribute("huespedes", huespedService.findAll());
    model.addAttribute("habitaciones", habitacionService.findAll());
    model.addAttribute("modo", "editar");
    return "reservas-form";
}

// Guardar cambios
@PostMapping("/admin/actualizar/{id}")
public String actualizarReserva(
        @PathVariable Integer id,
        @ModelAttribute Reserva reserva,
        @RequestParam Integer huespedId,
        @RequestParam Integer habitacionId,
        @RequestParam(required = false) Integer operadorId,
        @RequestParam String fechaInicio,
        @RequestParam String fechaFin,
        RedirectAttributes ra) {

    reserva.setHuesped(huespedService.findById(huespedId));
    reserva.setHabitacion(habitacionService.findById(habitacionId));
    reserva.setFechaInicio(LocalDateTime.parse(fechaInicio));
    reserva.setFechaFin(LocalDateTime.parse(fechaFin));
    reservaService.save(reserva);
    ra.addFlashAttribute("ok", "Reserva actualizada correctamente.");
    return "redirect:/reservas/admin";
    }
   
   @GetMapping("/admin")
    public String adminReservas(Model model) {
        model.addAttribute("reservas", reservaService.findAll());
        return "reservas-admin";
    }
    

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