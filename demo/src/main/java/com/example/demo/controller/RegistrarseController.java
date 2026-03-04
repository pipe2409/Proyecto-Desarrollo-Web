package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.repository.HuespedesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequestMapping("/registrarse")
@Controller
public class RegistrarseController {

    @Autowired
    private HuespedesRepository repo;

    @GetMapping("")
    public String registrarse() {
        return "registrarse";
    }

    @PostMapping("")
    public String guardarHuesped(@ModelAttribute Huesped huesped, Model model) {
        try {
            repo.save(huesped);
            return "redirect:/iniciar-sesion";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al registrarse. Intenta de nuevo.");
            return "registrarse";
        }
    }
}