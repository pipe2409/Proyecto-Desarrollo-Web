package com.example.demo.controller;

import com.example.demo.entities.Huesped;
import com.example.demo.repository.HuespedesRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/huespedes")
@Controller
public class HuespedesController {

    @Autowired
    private HuespedesRepository repo;

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

    @PostMapping("/eliminar-cuenta")
public String eliminarCuenta(HttpSession session) {
    Integer id = (Integer) session.getAttribute("huespedId");
    if (id == null) return "redirect:/iniciar-sesion";

    repo.deleteById(id);   // 👈 elimina del Map
    session.invalidate();  // 👈 cierra sesión

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

    Huesped h = repo.findById(id);
    if (h == null) return "redirect:/iniciar-sesion";

    // 1) validar actual
    if (h.getContrasena() == null || !h.getContrasena().equals(actual)) {
        model.addAttribute("error", "La contraseña actual no es correcta.");
        return "cambiar-contrasena";
    }

    // 2) validar nueva = confirmar
    if (!nueva.equals(confirmar)) {
        model.addAttribute("error", "La nueva contraseña y la confirmación no coinciden.");
        return "cambiar-contrasena";
    }

    // 3) validar mínima (opcional)
    if (nueva.length() < 6) {
        model.addAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres.");
        return "cambiar-contrasena";
    }

    // 4) guardar
    h.setContrasena(nueva);
    repo.save(h);

    return "redirect:/huespedes/cambiar-contrasena?okPass";
}
}