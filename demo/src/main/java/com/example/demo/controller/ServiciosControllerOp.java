package com.example.demo.controller;

import com.example.demo.repository.ServicioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServiciosControllerOp {

    private final ServicioRepository repo;

    public ServiciosControllerOp(ServicioRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/servicios-op")
    public String serviciosOp(Model model) {
        model.addAttribute("servicios", repo.findAll());
        return "servicios-op"; // -> servicios-op.html
    }
}