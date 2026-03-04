package com.example.demo.controller;

import com.example.demo.repository.HuespedesRepository;
import com.example.demo.repository.TipoHabitacionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/registrarse")
@Controller
public class RegistrarseController {

    @Autowired
    private HuespedesRepository repo;
    
    @GetMapping("")
    public String registrarse() {
        return "registrarse";
    }
}
