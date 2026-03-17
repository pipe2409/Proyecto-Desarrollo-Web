package com.example.demo.controller;

import com.example.demo.entities.Habitacion;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.service.HabitacionService;
import com.example.demo.service.TipoHabitacionService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/habitaciones")
@Controller
public class HabitacionController {

    @Autowired
    private  HabitacionService habitacionService;
    @Autowired
    private  TipoHabitacionService tipoHabitacionService;

@GetMapping("/reservar")
public String reservar(Model model, HttpSession session) {
    model.addAttribute("habitaciones", habitacionService.findAll());
    model.addAttribute("usuarioLogueado", session.getAttribute("huespedId") != null);
    return "habitaciones-reservar";
}

@GetMapping("/admin")
public String admin(@RequestParam(value = "tipoId", required = false) Integer tipoId, Model model) {

    List<Habitacion> habitaciones = habitacionService.findByTipoId(tipoId);
    List<TipoHabitacion> tipos = tipoHabitacionService.findAll();

    model.addAttribute("habitaciones", habitaciones);
    model.addAttribute("tiposHabitacion", tipos);
    model.addAttribute("tipoId", tipoId);

    Map<Integer, String> nombresTipo = new LinkedHashMap<>();
    for (TipoHabitacion t : tipos) {
        nombresTipo.put(t.getId(), t.getNombre());
    }

    model.addAttribute("nombresTipo", nombresTipo);

    return "habitaciones-admin";
}

    @GetMapping("/admin/nuevo")
public String nuevo(@RequestParam(value = "tipoId", required = false) Integer tipoId, Model model) {

    Habitacion h = new Habitacion();
    h.setEstado("DISPONIBLE");

    model.addAttribute("habitacion", h);
    model.addAttribute("tiposHabitacion", tipoHabitacionService.findAll());
    model.addAttribute("tipoIdSeleccionado", tipoId);
    model.addAttribute("modo", "crear");

    return "habitaciones-form";
}

    @PostMapping("/admin/guardar")
public String guardar(@ModelAttribute("habitacion") Habitacion habitacion,
                      @RequestParam("tipoHabitacionId") Integer tipoHabitacionId,
                      RedirectAttributes ra) {

    try {

        TipoHabitacion tipo = tipoHabitacionService.findById(tipoHabitacionId);

        habitacion.setTipoHabitacion(tipo);
        habitacionService.save(habitacion);

        ra.addFlashAttribute("ok", "Habitación creada.");
        return "redirect:/habitaciones/admin?tipoId=" + tipoHabitacionId;

    } catch (EntityNotFoundException e) {

        ra.addFlashAttribute("err", e.getMessage());
        return "redirect:/habitaciones/admin";
    }
}

   @GetMapping("/admin/editar/{id}")
public String editar(@PathVariable Integer id, Model model, RedirectAttributes ra) {

    try {

        Habitacion h = habitacionService.findById(id);

        Integer tipoIdSeleccionado =
                h.getTipoHabitacion() != null ? h.getTipoHabitacion().getId() : null;

        model.addAttribute("habitacion", h);
        model.addAttribute("tiposHabitacion", tipoHabitacionService.findAll());
        model.addAttribute("tipoIdSeleccionado", tipoIdSeleccionado);
        model.addAttribute("modo", "editar");

        return "habitaciones-form";

    } catch (EntityNotFoundException e) {

        ra.addFlashAttribute("err", e.getMessage());
        return "redirect:/habitaciones/admin";
    }
}

   @PostMapping("/admin/actualizar/{id}")
public String actualizar(@PathVariable Integer id,
                         @ModelAttribute("habitacion") Habitacion habitacion,
                         @RequestParam("tipoHabitacionId") Integer tipoHabitacionId,
                         RedirectAttributes ra) {

    try {

        habitacionService.update(id, habitacion, tipoHabitacionId);

        ra.addFlashAttribute("ok", "Habitación actualizada.");
        return "redirect:/habitaciones/admin?tipoId=" + tipoHabitacionId;

    } catch (Exception e) {

        ra.addFlashAttribute("err", e.getMessage());
        return "redirect:/habitaciones/admin";
    }
}

    @PostMapping("/admin/eliminar/{id}")
    public String eliminar(@PathVariable Integer id,
                           @RequestParam(value = "tipoId", required = false) Integer tipoId,
                           RedirectAttributes ra) {
        try {
            habitacionService.deleteById(id);
            ra.addFlashAttribute("ok", "Habitación eliminada.");
        } catch (Exception e) {
            ra.addFlashAttribute("err", "No se pudo eliminar la habitación.");
        }

        return tipoId != null
                ? "redirect:/habitaciones/admin?tipoId=" + tipoId
                : "redirect:/habitaciones/admin";
    }
}