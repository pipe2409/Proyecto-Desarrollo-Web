package com.example.demo.controller;

import com.example.demo.repository.TipoHabitacionRepository;
import com.example.demo.service.TipoHabitacionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

@RequestMapping("")


@Controller
public class IndexController {

    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tiposHabitacion", tipoHabitacionService.findAll());
        return "index";
    }
}