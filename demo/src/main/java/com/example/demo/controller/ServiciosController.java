package com.example.demo.controller;


import com.example.demo.entities.Servicio;
import com.example.demo.service.ServicioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/servicios")
@Controller
public class ServiciosController {

    @Autowired
    private ServicioService serviciosService;

    @GetMapping("/admin")
    public String admin(Model model) {


        model.addAttribute("servicios", serviciosService.findAll());
        return "servicios-admin";
    }

    @GetMapping("/admin/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("servicio", new Servicio());
        model.addAttribute("modo", "crear");
        return "servicios-form";
    }

    @PostMapping("/admin/guardar")
    public String guardar(@ModelAttribute("servicio") Servicio servicio, RedirectAttributes ra) {
        serviciosService.save(servicio);
        ra.addFlashAttribute("ok", "Servicio creado.");

        return "redirect:/servicios/admin";
    }

    @GetMapping("/admin/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Servicio servicio = serviciosService.findById(id);
        if (servicio == null) {
            ra.addFlashAttribute("err", "No existe el servicio con id=" + id);
            return "redirect:/servicios/admin";
        }

        model.addAttribute("servicio", servicio);
        model.addAttribute("modo", "editar");
        return "servicios-form";
    }

    @PostMapping("/admin/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute("servicio") Servicio servicio,
                             RedirectAttributes ra) {
        try {
            serviciosService.update(id, servicio);
            ra.addFlashAttribute("ok", "Servicio actualizado.");
        } catch (Exception e) {
            ra.addFlashAttribute("err", "No existe el servicio con id=" + id);
        }
        return "redirect:/servicios/admin";
    }

    @PostMapping("/admin/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        serviciosService.deleteById(id);
        ra.addFlashAttribute("ok", "Servicio eliminado.");


        return "redirect:/servicios/admin";
    }

     @GetMapping("/op")
    public String serviciosOp(Model model) {
        model.addAttribute("servicios", serviciosService.findAll());
        return "servicios-op";

    }
}