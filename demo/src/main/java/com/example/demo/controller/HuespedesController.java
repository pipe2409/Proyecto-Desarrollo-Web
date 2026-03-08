package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.service.HuespedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/huespedes")
@Controller
public class HuespedesController {

    @Autowired
    private HuespedService huespedService;

    // Listar todos los huéspedes
    @GetMapping("/admin")
    public String huespedesAdmin(Model model) {
        model.addAttribute("huespedes", huespedService.listarHuespedes());
        return "huespedes-admin";
    }

    // Ver formulario para crear/editar huésped
    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("huesped", new Huesped());
        return "formulario-huesped";
    }

    // Guardar huésped (crear o editar)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Huesped huesped) {
        huespedService.guardarHuesped(huesped);
        return "redirect:/huespedes/admin";
    }

    // Eliminar huésped
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        huespedService.eliminarHuesped(id);
        return "redirect:/huespedes/admin";
    }
}