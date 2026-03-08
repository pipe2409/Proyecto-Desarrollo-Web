package com.example.demo.controller;

import com.example.demo.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/servicios")
@Controller
public class ServiciosController {

    @Autowired
    private ServicioService servicioService;

   
    @GetMapping("/admin")
    public String serviciosAdmin(Model model) {
        model.addAttribute("servicios", servicioService.listarServicios());
        return "servicios-admin";
    }

    @GetMapping("/op")
    public String serviciosOp(Model model) {
        model.addAttribute("servicios", servicioService.listarServicios());
        return "servicios-op";
    }

    @GetMapping("/api/servicio/{id}")
    @ResponseBody
    public Object obtenerServicio(@PathVariable int id) {
        return servicioService.buscarPorId(id);
    }
}