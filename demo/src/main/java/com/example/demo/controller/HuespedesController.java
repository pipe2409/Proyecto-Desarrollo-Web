package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.repository.HuespedesRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/huespedes")
@Controller
public class HuespedesController {

    private final HuespedesRepository repo;

    public HuespedesController(HuespedesRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/admin")
    public String huespedesAdmin(Model model) {
        model.addAttribute("huespedes", repo.findAll());
        return "huespedes-admin";
    }

    // ✅ PÁGINA PRINCIPAL DEL HUÉSPED (tu "dashboard")
    // Template: crud-huespedes.html
    @GetMapping("/mi-cuenta")
    public String miCuenta(HttpSession session, Model model) {
        Integer id = (Integer) session.getAttribute("huespedId");
        if (id == null) return "redirect:/iniciar-sesion";

        Huesped h = repo.findById(id);
        if (h == null) return "redirect:/iniciar-sesion";

        model.addAttribute("huesped", h);
        model.addAttribute("nombreCompleto", h.getNombre() + " " + h.getApellido());
        return "crud-huespedes";
    }

    // ✅ PERFIL: mostrar formulario con datos del logueado
    @GetMapping("/mi-perfil")
    public String miPerfil(HttpSession session, Model model) {
        Integer id = (Integer) session.getAttribute("huespedId");
        if (id == null) return "redirect:/iniciar-sesion";

        Huesped h = repo.findById(id);
        if (h == null) return "redirect:/iniciar-sesion";

        model.addAttribute("huesped", h);
        model.addAttribute("nombreCompleto", h.getNombre() + " " + h.getApellido());
        return "mi-perfil";
    }

    // ✅ PERFIL: guardar cambios (actualiza el Map)
    @PostMapping("/mi-perfil")
    public String actualizarPerfil(
            HttpSession session,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String pais
    ) {
        Integer id = (Integer) session.getAttribute("huespedId");
        if (id == null) return "redirect:/iniciar-sesion";

        Huesped h = repo.findById(id);
        if (h == null) return "redirect:/iniciar-sesion";

        h.setNombre(nombre);
        h.setApellido(apellido);
        h.setCorreo(correo);
        h.setTelefono(telefono);
        h.setDireccion(direccion);
        h.setPais(pais);

        repo.save(h); // sobrescribe en el Map
        return "redirect:/huespedes/mi-perfil?ok";
    }
}