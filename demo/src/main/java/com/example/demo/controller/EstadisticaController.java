package com.example.demo.controller;

import com.example.demo.service.EstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "http://localhost:4200")
public class EstadisticaController {

    @Autowired
    private EstadisticaService estadisticaService;

    @GetMapping("/dashboard")
    public Map<String, Object> getEstadisticas() {
        return estadisticaService.obtenerEstadisticas();
    }
}