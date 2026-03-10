package com.example.demo.controller;

import com.example.demo.entities.TipoHabitacion;
import com.example.demo.service.TipoHabitacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

@RequestMapping("/tipos-habitacion")
@Controller
public class TipoHabitacionController {

    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    

    @GetMapping
    public String home() {
        return "redirect:/tipos-habitacion/admin";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("tiposHabitacion", tipoHabitacionService.findAll());
        return "tipos-habitacion-admin";
    }

    @GetMapping("/admin/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("tipo", new TipoHabitacion());
        model.addAttribute("modo", "crear");
        return "tipos-habitacion-form";
    }

   @PostMapping("/admin/guardar")
public String guardar(@ModelAttribute("tipo") TipoHabitacion tipo,
                      RedirectAttributes ra,
                      Model model) {
    try {
        tipoHabitacionService.save(tipo);
        ra.addFlashAttribute("ok", "Tipo de habitación creado.");
        return "redirect:/tipos-habitacion/admin";
    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("tipo", tipo);
        model.addAttribute("modo", "crear");
        model.addAttribute("error", "No se pudo guardar el tipo de habitación.");
        return "tipos-habitacion-form";
    }
}
    @GetMapping("/admin/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        TipoHabitacion tipo = tipoHabitacionService.findById(id);
        if (tipo == null) {
            ra.addFlashAttribute("err", "No existe el tipo con id=" + id);
            return "redirect:/tipos-habitacion/admin";
        }
        model.addAttribute("tipo", tipo);
        model.addAttribute("modo", "editar");
        return "tipos-habitacion-form";
    }

    

    @PostMapping("/admin/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        tipoHabitacionService.deleteById(id);
        ra.addFlashAttribute("ok", "Tipo de habitación eliminado.");
        return "redirect:/tipos-habitacion/admin";
    }
}