package com.example.demo.controller;

import com.example.demo.entities.Servicio;
import com.example.demo.service.ServicioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServiciosControllerAd {

    @Autowired
    private ServicioService service;

    public ServiciosControllerAd(ServicioService service) {
        this.service = service;
    }


    @GetMapping("/servicios-admin")
    public String verServicios(Model model) {
        model.addAttribute("servicios", service.listarServicios());
        return "servicios-admin";
    }
}
