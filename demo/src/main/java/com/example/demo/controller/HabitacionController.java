package com.example.demo.controller;

import com.example.demo.entities.Habitacion;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.TipoHabitacionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/habitaciones")
@Controller
public class HabitacionController {

    @Autowired
    private HabitacionRepository habitacionRepo;

    @Autowired
    private TipoHabitacionRepository tipoRepo;

    public HabitacionController(HabitacionRepository habitacionRepo, TipoHabitacionRepository tipoRepo) {
        this.habitacionRepo = habitacionRepo;
        this.tipoRepo = tipoRepo;
    }

    // LISTAR (con filtro opcional ?tipoId=)
    @GetMapping("/admin")
    public String admin(@RequestParam(value = "tipoId", required = false) Integer tipoId, Model model) {

        List<Habitacion> habitaciones = (tipoId == null)
                ? habitacionRepo.findAll()
                : habitacionRepo.findByTipoId(tipoId);

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

    // FORM CREAR (evita null: setea tipo por defecto)
    @GetMapping("/admin/nuevo")
    public String nuevo(@RequestParam(value = "tipoId", required = false) Integer tipoId, Model model) {

        Habitacion h = new Habitacion();

        // estado por defecto
        h.setEstado("DISPONIBLE");

        // tipo por defecto (primer tipo disponible o 1)
        if (tipoId != null) {
            h.setTipoHabitacionId(tipoId);
        } else if (!tipoRepo.findAll().isEmpty()) {
            h.setTipoHabitacionId(tipoRepo.findAll().get(0).getId());
        } else {
            h.setTipoHabitacionId(1);
        }

        model.addAttribute("habitacion", h);
        model.addAttribute("tiposHabitacion", tipoRepo.findAll());
        model.addAttribute("modo", "crear");
        return "habitaciones-form";
    }

    // GUARDAR
    @PostMapping("/admin/guardar")
    public String guardar(@ModelAttribute("habitacion") Habitacion habitacion, RedirectAttributes ra) {

        // Validación mínima: tipo debe existir
        if (tipoRepo.findById(habitacion.getTipoHabitacionId()) == null) {
            ra.addFlashAttribute("err", "Selecciona un tipo de habitación válido.");
            return "redirect:/habitaciones/admin";
        }

        habitacionRepo.save(habitacion);
        ra.addFlashAttribute("ok", "Habitación creada.");
        return "redirect:/habitaciones/admin";
    }

    // FORM EDITAR
    @GetMapping("/admin/editar/{id}")
    public String editar(@PathVariable int id, Model model, RedirectAttributes ra) {

        Habitacion h = habitacionRepo.findById(id);
        if (h == null) {
            ra.addFlashAttribute("err", "No existe la habitación con id=" + id);
            return "redirect:/habitaciones/admin";
        }

        model.addAttribute("habitacion", h);
        model.addAttribute("tiposHabitacion", tipoRepo.findAll());
        model.addAttribute("modo", "editar");
        return "habitaciones-form";
    }

    // ACTUALIZAR
    @PostMapping("/admin/actualizar/{id}")
    public String actualizar(@PathVariable int id,
                             @ModelAttribute("habitacion") Habitacion habitacion,
                             RedirectAttributes ra) {

        if (habitacionRepo.findById(id) == null) {
            ra.addFlashAttribute("err", "No existe la habitación con id=" + id);
            return "redirect:/habitaciones/admin";
        }

        if (tipoRepo.findById(habitacion.getTipoHabitacionId()) == null) {
            ra.addFlashAttribute("err", "Selecciona un tipo de habitación válido.");
            return "redirect:/habitaciones/admin/editar/" + id;
        }

        habitacion.setId(id);
        habitacionRepo.save(habitacion);
        ra.addFlashAttribute("ok", "Habitación actualizada.");
        return "redirect:/habitaciones/admin";
    }

    // ELIMINAR
    @PostMapping("/admin/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes ra) {
        habitacionRepo.deleteById(id);
        ra.addFlashAttribute("ok", "Habitación eliminada.");
        return "redirect:/habitaciones/admin";
    }
}