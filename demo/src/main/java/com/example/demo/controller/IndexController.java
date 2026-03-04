package com.example.demo.controller;

import com.example.demo.repository.TipoHabitacionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("")
@Controller
public class IndexController {

    private final TipoHabitacionRepository repo;

    public IndexController(TipoHabitacionRepository repo) {
        this.repo = repo;
    }

    @GetMapping("")
    public String index(Model model) {

        model.addAttribute("tiposHabitacion", repo.findAll());

        return "index";
    }
}