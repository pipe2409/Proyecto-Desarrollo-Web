package com.example.demo.controller;

import com.example.demo.repository.TipoHabitacionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/tipos-habitacion")
@Controller
public class TipoHabitacionController {

    private final TipoHabitacionRepository repo;

    public TipoHabitacionController(TipoHabitacionRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String homeTiposHabitacion() {
        return "redirect:/tipos-habitacion/admin";
    }

    @GetMapping("/admin")
    public String tiposHabitacionAdmin(Model model) {
        model.addAttribute("tiposHabitacion", repo.findAll());
        return "tipos-habitacion-admin";
    }

    @GetMapping("/op")
    public String tiposHabitacionOp(Model model) {
        model.addAttribute("tiposHabitacion", repo.findAll());
        return "tipos-habitacion-op";
    }

    @GetMapping("/api/tipo/{id}")
    @ResponseBody
    public Object obtenerTipoHabitacion(@PathVariable int id) {
        return repo.findById(id);
    }
}