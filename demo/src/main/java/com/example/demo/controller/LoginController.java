package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.service.HuespedService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class LoginController {

    @Autowired
    private HuespedService huespedService;


    // ==========================
    // GET - Mostrar login
    // ==========================
    @GetMapping("/iniciar-sesion")
    public String iniciarSesion() {
        return "iniciar-sesion";
    }

    // ==========================
    // POST - Procesar login
    // ==========================
    @PostMapping("/iniciar-sesion")
    public String procesarLogin(
            @RequestParam("correo") String correo,
            @RequestParam("contrasena") String contrasena,
            HttpSession session,
            Model model
    ) {
        Huesped h = huespedService.login(correo, contrasena);

        if (h == null) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "iniciar-sesion";
        }

        // Guardamos el id en sesión
        session.setAttribute("huespedId", h.getId());
        session.setAttribute("huespedNombre", h.getNombre());

        return "redirect:/huespedes/crud";
    }

    // ==========================
    // Logout
    // ==========================
    @GetMapping("/cerrar-sesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}