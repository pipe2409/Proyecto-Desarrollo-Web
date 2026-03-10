package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.service.HuespedService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/huespedes")
@Controller
public class HuespedesController {

    private final HuespedService huespedService;

    public HuespedesController(HuespedService huespedService) {
        this.huespedService = huespedService;
    }

    @GetMapping("/admin")
    public String huespedesAdmin(Model model) {
        model.addAttribute("huespedes", huespedService.findAll());
        return "huespedes-admin";
    }

    @GetMapping("/crud")
    public String crudHuespedes(HttpSession session, Model model) {
        Integer huespedId = (Integer) session.getAttribute("huespedId");

        if (huespedId == null) {
            return "redirect:/iniciar-sesion";
        }

        Huesped huesped = huespedService.findById(huespedId);
        if (huesped == null) {
            session.invalidate();
            return "redirect:/iniciar-sesion";
        }

        model.addAttribute("huesped", huesped);
        model.addAttribute("nombreCompleto", huesped.getNombre() + " " + huesped.getApellido());
        return "crud-huespedes";
    }

    @GetMapping("/mi-cuenta")
    public String miCuenta(HttpSession session, Model model) {
        Integer huespedId = (Integer) session.getAttribute("huespedId");

        if (huespedId == null) {
            return "redirect:/iniciar-sesion";
        }

        Huesped huesped = huespedService.findById(huespedId);
        if (huesped == null) {
            session.invalidate();
            return "redirect:/iniciar-sesion";
        }

        model.addAttribute("huesped", huesped);
        model.addAttribute("nombreCompleto", huesped.getNombre() + " " + huesped.getApellido());
        return "mi-cuenta";
    }

    @PostMapping("/mi-cuenta")
    public String actualizarMiCuenta(
            HttpSession session,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String pais
    ) {
        Integer huespedId = (Integer) session.getAttribute("huespedId");

        if (huespedId == null) {
            return "redirect:/iniciar-sesion";
        }

        Huesped huesped = huespedService.findById(huespedId);
        if (huesped == null) {
            session.invalidate();
            return "redirect:/iniciar-sesion";
        }

        huesped.setNombre(nombre);
        huesped.setApellido(apellido);
        huesped.setCorreo(correo);
        huesped.setTelefono(telefono);
        huesped.setDireccion(direccion);
        huesped.setPais(pais);

        huespedService.save(huesped);
        session.setAttribute("huespedNombre", huesped.getNombre());

        return "redirect:/huespedes/crud?ok";
    }

    @GetMapping("/cambiar-contrasena")
    public String verCambiarContrasena(HttpSession session) {
        Integer huespedId = (Integer) session.getAttribute("huespedId");

        if (huespedId == null) {
            return "redirect:/iniciar-sesion";
        }

        return "cambiar-contrasena";
    }

    @PostMapping("/cambiar-contrasena")
    public String cambiarContrasena(
            HttpSession session,
            @RequestParam("actual") String actual,
            @RequestParam("nueva") String nueva,
            @RequestParam("confirmar") String confirmar,
            Model model
    ) {
        Integer huespedId = (Integer) session.getAttribute("huespedId");

        if (huespedId == null) {
            return "redirect:/iniciar-sesion";
        }

        Huesped huesped = huespedService.findById(huespedId);
        if (huesped == null) {
            session.invalidate();
            return "redirect:/iniciar-sesion";
        }

        if (!huesped.getContrasena().equals(actual)) {
            model.addAttribute("error", "La contraseña actual es incorrecta.");
            return "cambiar-contrasena";
        }

        if (!nueva.equals(confirmar)) {
            model.addAttribute("error", "La nueva contraseña y la confirmación no coinciden.");
            return "cambiar-contrasena";
        }

        huesped.setContrasena(nueva);
        huespedService.save(huesped);

        model.addAttribute("ok", "Contraseña actualizada correctamente.");
        return "cambiar-contrasena";
    }
    @PostMapping("/eliminar-cuenta")
public String eliminarCuenta(HttpSession session) {

    Integer huespedId = (Integer) session.getAttribute("huespedId");

    if (huespedId == null) {
        return "redirect:/iniciar-sesion";
    }

    Huesped huesped = huespedService.findById(huespedId);

    if (huesped != null) {
        huespedService.deleteById(huespedId);
    }

    session.invalidate();

    return "redirect:/?cuentaEliminada";
}
}