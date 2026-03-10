package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.service.HuespedService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RequestMapping("/registrarse")
@Controller
public class RegistrarseController {

    @Autowired
    private HuespedService huespedService;


    @GetMapping("")
    public String registrarse(Model model) {
        model.addAttribute("huesped", new Huesped());
        return "registrarse";
    }

    @PostMapping("")
    public String guardarHuesped(@ModelAttribute Huesped huesped, Model model) {
        try {
            huespedService.save(huesped);
            return "redirect:/iniciar-sesion";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al registrarse. Intenta de nuevo.");
            return "registrarse";
        }
    }
}