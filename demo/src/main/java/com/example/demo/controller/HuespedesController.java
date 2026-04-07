package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.service.HuespedService;
import com.example.demo.service.ReservaService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RequestMapping("/huespedes")
@Controller
public class HuespedesController {

    @Autowired
    private HuespedService huespedService;


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
        @RequestParam String pais) {

    Integer huespedId = (Integer) session.getAttribute("huespedId");

    if (huespedId == null) {
        return "redirect:/iniciar-sesion";
    }

    huespedService.update(
            huespedId, nombre, apellido, correo, telefono, direccion, pais);

    session.setAttribute("huespedNombre", nombre);

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
        @RequestParam String actual,
        @RequestParam String nueva,
        @RequestParam String confirmar,
        Model model) {

    Integer huespedId = (Integer) session.getAttribute("huespedId");

    if (huespedId == null) {
        return "redirect:/iniciar-sesion";
    }

    try {

        huespedService.cambiarContrasena(huespedId, actual, nueva, confirmar);

        model.addAttribute("ok", "Contraseña actualizada correctamente.");

    } catch (RuntimeException e) {

        model.addAttribute("error", e.getMessage());
    }

    return "cambiar-contrasena";
}
  @Autowired
private ReservaService reservaService;  

@PostMapping("/eliminar-cuenta")
public String eliminarCuenta(HttpSession session, Model model) {

    Integer huespedId = (Integer) session.getAttribute("huespedId");

    if (huespedId == null) {
        return "redirect:/iniciar-sesion";
    }

    // Verificar si tiene reservas activas
    if (reservaService.tieneReservasActivas(huespedId)) {
        model.addAttribute("error", "No puedes eliminar tu cuenta porque tienes reservas activas o pendientes. Cancela tus reservas primero.");
        
        // Obtener los datos del huésped para mostrar la página nuevamente
        Huesped huesped = huespedService.findById(huespedId);
        model.addAttribute("huesped", huesped);
        model.addAttribute("nombreCompleto", huesped.getNombre() + " " + huesped.getApellido());
        return "mi-cuenta";  // Regresar a la página de cuenta con el mensaje de error
    }

    Huesped huesped = huespedService.findById(huespedId);

    if (huesped != null) {
        huespedService.deleteById(huespedId);
    }

    session.invalidate();

    return "redirect:/?cuentaEliminada";
}
}