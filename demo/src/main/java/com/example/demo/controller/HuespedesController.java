package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.service.HuespedService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/huespedes")
@Controller
public class HuespedesController {

    @Autowired
    private HuespedService huespedService;

    public HuespedesController(HuespedService huespedService) {
        this.huespedService = huespedService;
    }

    @GetMapping("/admin")
    public String huespedesAdmin(Model model) {
        model.addAttribute("huespedes", huespedService.listarHuespedes());
        return "huespedes-admin";
    }


    @GetMapping("/mi-cuenta")
    public String miCuenta(HttpSession session, Model model) {
        Integer id = (Integer) session.getAttribute("huespedId");
        if (id == null) return "redirect:/iniciar-sesion";

        Huesped h = huespedService.buscarPorId(id);
        if (h == null) return "redirect:/iniciar-sesion";

        model.addAttribute("huesped", h);
        model.addAttribute("nombreCompleto", h.getNombre() + " " + h.getApellido());
        return "crud-huespedes";
    }


    @GetMapping("/mi-perfil")
    public String miPerfil(HttpSession session, Model model) {
        Integer id = (Integer) session.getAttribute("huespedId");
        if (id == null) return "redirect:/iniciar-sesion";

        Huesped h = huespedService.buscarPorId(id);
        if (h == null) return "redirect:/iniciar-sesion";

        model.addAttribute("huesped", h);
        model.addAttribute("nombreCompleto", h.getNombre() + " " + h.getApellido());
        return "mi-perfil";
    }


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

        Huesped h = huespedService.buscarPorId(id);
        if (h == null) return "redirect:/iniciar-sesion";

        h.setNombre(nombre);
        h.setApellido(apellido);
        h.setCorreo(correo);
        h.setTelefono(telefono);
        h.setDireccion(direccion);
        h.setPais(pais);

        huespedService.guardarHuesped(h);

        return "redirect:/huespedes/mi-perfil?ok";
    }


    @PostMapping("/eliminar-cuenta")
    public String eliminarCuenta(HttpSession session) {
        Integer id = (Integer) session.getAttribute("huespedId");
        if (id == null) return "redirect:/iniciar-sesion";

        huespedService.eliminarHuesped(id);
        session.invalidate();

        return "redirect:/?cuentaEliminada";
    }

    // ==========================
    // CAMBIAR CONTRASEÑA (GET)
    // ==========================
    @GetMapping("/cambiar-contrasena")
    public String verCambiarContrasena(HttpSession session) {
        Integer id = (Integer) session.getAttribute("huespedId");
        if (id == null) return "redirect:/iniciar-sesion";
        return "cambiar-contrasena";
    }

    // ==========================
    // CAMBIAR CONTRASEÑA (POST)
    // ==========================
    @PostMapping("/cambiar-contrasena")
    public String cambiarContrasena(
            HttpSession session,
            @RequestParam String actual,
            @RequestParam String nueva,
            @RequestParam String confirmar,
            Model model
    ) {
        Integer id = (Integer) session.getAttribute("huespedId");
        if (id == null) return "redirect:/iniciar-sesion";

        Huesped h = huespedService.buscarPorId(id);
        if (h == null) return "redirect:/iniciar-sesion";

        if (h.getContrasena() == null || !h.getContrasena().equals(actual)) {
            model.addAttribute("error", "La contraseña actual no es correcta.");
            return "cambiar-contrasena";
        }

        if (!nueva.equals(confirmar)) {
            model.addAttribute("error", "La nueva contraseña y la confirmación no coinciden.");
            return "cambiar-contrasena";
        }

        if (nueva.length() < 6) {
            model.addAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres.");
            return "cambiar-contrasena";
        }

        h.setContrasena(nueva);
        huespedService.guardarHuesped(h);

        return "redirect:/huespedes/cambiar-contrasena?okPass";
    }
}