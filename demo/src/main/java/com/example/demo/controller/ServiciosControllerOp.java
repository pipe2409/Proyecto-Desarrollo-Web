package com.example.demo.controller;

import com.example.demo.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ServiciosControllerOp {

    @Autowired
    private ServicioRepository repo;

    public ServiciosControllerOp(ServicioRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/servicios-op")
    public String serviciosOp(Model model) {
        model.addAttribute("servicios", repo.findAll());
        return "servicios-op"; // -> servicios-op.html
    }

    @GetMapping("/api/servicio/{id}")
    @ResponseBody
    public Object obtenerServicio(@PathVariable int id) {
        return repo.findById(id);
    }
}