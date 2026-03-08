package com.example.demo.controller;

import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.TipoHabitacionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

@RequestMapping("/tipos-habitacion")
@Controller
public class TipoHabitacionController {

    @Autowired
    private TipoHabitacionRepository repo;
 
    @GetMapping
    public String home() {
        return "redirect:/tipos-habitacion/admin";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("tiposHabitacion", repo.findAll());
        return "tipos-habitacion-admin";
    }

    @GetMapping("/admin/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("tipo", new TipoHabitacion());
        model.addAttribute("modo", "crear");
        return "tipos-habitacion-form";
    }

    @PostMapping("/admin/guardar")
    public String guardar(@ModelAttribute("tipo") TipoHabitacion tipo, RedirectAttributes ra) {
        repo.save(tipo);
        return "redirect:/tipos-habitacion/admin";
    }

    @GetMapping("/admin/editar/{id}")
    public String editar(@PathVariable int id, Model model, RedirectAttributes ra) {
        TipoHabitacion tipo = repo.findById(id);
        if (tipo == null) {
            ra.addFlashAttribute("err", "No existe el tipo con id=" + id);
            return "redirect:/tipos-habitacion/admin";
        }
        model.addAttribute("tipo", tipo);
        model.addAttribute("modo", "editar");
        return "tipos-habitacion-form";
    }

    @PostMapping("/admin/actualizar/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute("tipo") TipoHabitacion tipo, RedirectAttributes ra) {
        if (repo.findById(id) == null) {
            ra.addFlashAttribute("err", "No existe el tipo con id=" + id);
            return "redirect:/tipos-habitacion/admin";
        }
        tipo.setId(id);
        repo.save(tipo);
        ra.addFlashAttribute("ok", "Tipo de habitación actualizado.");
        return "redirect:/tipos-habitacion/admin";
    }

    @PostMapping("/admin/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("ok", "Tipo de habitación eliminado.");
        return "redirect:/tipos-habitacion/admin";
    }
}