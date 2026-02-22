package com.example.demo.controller;

import com.example.demo.entities.Servicio;
import com.example.demo.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServicioController {

    private final ServicioService service;

    public ServicioController(ServicioService service) {
        this.service = service;
    }


    @GetMapping("/servicios")
    public String verServicios(Model model) {
        model.addAttribute("servicios", service.listarServicios());
        return "servicios";
    }
}
