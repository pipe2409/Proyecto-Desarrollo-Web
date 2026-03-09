package com.example.demo.controller;

import com.example.demo.entities.Habitacion;
import com.example.demo.service.HabitacionService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    @GetMapping("/admin")
    public String listar(Model model) {
        model.addAttribute("habitaciones", habitacionService.listar(null));
        return "habitaciones-admin";
    }

    @GetMapping("/admin/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("habitacion", new Habitacion());
        return "habitaciones-form";
    }

    @PostMapping("/admin/guardar")
    public String guardar(@ModelAttribute Habitacion habitacion, RedirectAttributes ra) {
        habitacionService.crear(habitacion);
        ra.addFlashAttribute("ok", "Habitación creada.");
        return "redirect:/habitaciones/admin";
    }

    @GetMapping("/admin/editar/{id}")
    public String editar(@PathVariable int id, Model model) {
        model.addAttribute("habitacion", habitacionService.obtenerPorId(id));
        return "habitaciones-form";
    }

    @PostMapping("/admin/actualizar/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute Habitacion habitacion, RedirectAttributes ra) {
        habitacionService.actualizar(id, habitacion);
        ra.addFlashAttribute("ok", "Habitación actualizada.");
        return "redirect:/habitaciones/admin";
    }

    @PostMapping("/admin/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes ra) {
        habitacionService.eliminar(id);
        ra.addFlashAttribute("ok", "Habitación eliminada.");
        return "redirect:/habitaciones/admin";
    }
}