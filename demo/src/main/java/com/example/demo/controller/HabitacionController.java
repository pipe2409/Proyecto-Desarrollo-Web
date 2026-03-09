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

        if (tipoId != null) {
            TipoHabitacion tipo = tipoRepo.findById(tipoId).orElse(null);
            if (tipo != null) {
                h.setTipoHabitacion(tipo);
            }
        } else if (!tipoRepo.findAll().isEmpty()) {
            h.setTipoHabitacion(tipoRepo.findAll().get(0));
        } else {
            h.setTipoHabitacion(null);
        }

        model.addAttribute("habitacion", h);
        model.addAttribute("tiposHabitacion", tipoRepo.findAll());
        model.addAttribute("modo", "crear");
        return "habitaciones-form";
    }

    @PostMapping("/admin/guardar")
    public String guardar(@ModelAttribute("habitacion") Habitacion habitacion, RedirectAttributes ra) {
        habitacionService.save(habitacion);
        ra.addFlashAttribute("ok", "Habitación creada.");
        return "redirect:/habitaciones/admin";
    }

    @GetMapping("/admin/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes ra) {

        Habitacion h = habitacionService.findById(id);
        if (h == null) {
            ra.addFlashAttribute("err", "No existe la habitación con id=" + id);
            return "redirect:/habitaciones/admin";
        }

        model.addAttribute("habitacion", h);
        model.addAttribute("tiposHabitacion", tipoRepo.findAll());
        model.addAttribute("modo", "editar");
        return "habitaciones-form";
    }

    @PostMapping("/admin/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute("habitacion") Habitacion habitacion,
                             RedirectAttributes ra) {
        try {
            habitacionService.update(id, habitacion);
            ra.addFlashAttribute("ok", "Habitación actualizada.");
        } catch (Exception e) {
            ra.addFlashAttribute("err", "No existe la habitación con id=" + id);
        }
        return "redirect:/habitaciones/admin";
    }

    @PostMapping("/admin/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        habitacionService.deleteById(id);
        ra.addFlashAttribute("ok", "Habitación eliminada.");
        return "redirect:/habitaciones/admin";
    }
}