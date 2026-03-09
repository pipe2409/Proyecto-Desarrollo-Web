package com.example.demo.controller;

import com.example.demo.entities.Habitacion;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.TipoHabitacionRepository;
import com.example.demo.service.HabitacionService;
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

    private final HabitacionService habitacionService;
    private final TipoHabitacionRepository tipoRepo;

    public HabitacionController(HabitacionService habitacionService, TipoHabitacionRepository tipoRepo) {
        this.habitacionService = habitacionService;
        this.tipoRepo = tipoRepo;
    }

    @GetMapping("/admin")
    public String admin(@RequestParam(value = "tipoId", required = false) Integer tipoId, Model model) {
        List<Habitacion> habitaciones = habitacionService.findByTipoId(tipoId);

        model.addAttribute("habitaciones", habitaciones);
        model.addAttribute("tiposHabitacion", tipoRepo.findAll());
        model.addAttribute("tipoId", tipoId);

        Map<Integer, String> nombresTipo = new LinkedHashMap<>();
        for (TipoHabitacion t : tipoRepo.findAll()) {
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
        model.addAttribute("tiposHabitacion", tipoRepo.findAll());
        model.addAttribute("tipoIdSeleccionado", tipoId);
        model.addAttribute("modo", "crear");

        return "habitaciones-form";
    }

    @PostMapping("/admin/guardar")
    public String guardar(@ModelAttribute("habitacion") Habitacion habitacion,
                          @RequestParam("tipoHabitacionId") Integer tipoHabitacionId,
                          RedirectAttributes ra) {
        try {
            TipoHabitacion tipo = tipoRepo.findById(tipoHabitacionId).orElse(null);

            if (tipo == null) {
                ra.addFlashAttribute("err", "El tipo de habitación seleccionado no existe.");
                return "redirect:/habitaciones/admin";
            }

            habitacion.setTipoHabitacion(tipo);
            habitacionService.save(habitacion);

            ra.addFlashAttribute("ok", "Habitación creada.");
            return "redirect:/habitaciones/admin?tipoId=" + tipoHabitacionId;

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("err", "No se pudo crear la habitación: " + e.getMessage());
            return "redirect:/habitaciones/admin";
        }
    }

    @GetMapping("/admin/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Habitacion h = habitacionService.findById(id);

        if (h == null) {
            ra.addFlashAttribute("err", "No existe la habitación con id=" + id);
            return "redirect:/habitaciones/admin";
        }

        Integer tipoIdSeleccionado = h.getTipoHabitacion() != null ? h.getTipoHabitacion().getId() : null;

        model.addAttribute("habitacion", h);
        model.addAttribute("tiposHabitacion", tipoRepo.findAll());
        model.addAttribute("tipoIdSeleccionado", tipoIdSeleccionado);
        model.addAttribute("modo", "editar");

        return "habitaciones-form";
    }

    @PostMapping("/admin/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute("habitacion") Habitacion habitacion,
                             @RequestParam("tipoHabitacionId") Integer tipoHabitacionId,
                             RedirectAttributes ra) {
        try {
            TipoHabitacion tipo = tipoRepo.findById(tipoHabitacionId).orElse(null);

            if (tipo == null) {
                ra.addFlashAttribute("err", "El tipo de habitación seleccionado no existe.");
                return "redirect:/habitaciones/admin";
            }

            habitacion.setTipoHabitacion(tipo);
            habitacionService.update(id, habitacion);

            ra.addFlashAttribute("ok", "Habitación actualizada.");
            return "redirect:/habitaciones/admin?tipoId=" + tipoHabitacionId;

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("err", "No se pudo actualizar la habitación: " + e.getMessage());
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