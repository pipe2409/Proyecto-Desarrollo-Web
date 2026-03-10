package com.example.demo.controller;

import com.example.demo.entities.RoomType;
import com.example.demo.repository.RoomTypeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/roomTypes")
@Controller
public class RoomTypeController {

    private final RoomTypeRepository repo;

    public RoomTypeController(RoomTypeRepository repo) {
        this.repo = repo;
    }

    // ✅ VER TODAS LAS HABITACIONES
    @GetMapping("/admin")
    public String roomTypesAdmin(Model model) {
        model.addAttribute("roomTypes", repo.findAll());
        return "habitaciones-admin";
    }

    // ✅ MOSTRAR FORMULARIO PARA CREAR NUEVA HABITACIÓN
    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("roomType", new RoomType());
        return "habitacion-form";
    }

    // ✅ GUARDAR NUEVA HABITACIÓN
    @PostMapping("/crear")
    public String crear(
            @RequestParam String name,
            @RequestParam String tipoHabitacion,
            @RequestParam int capacity,
            @RequestParam int numero,
            @RequestParam String estado) {
        
        RoomType roomType = new RoomType();
        roomType.setName(name);
        roomType.setTipoHabitacion(tipoHabitacion);
        roomType.setCapacity(capacity);
        roomType.setNumero(numero);
        roomType.setEstado(estado);
        
        repo.save(roomType);
        return "redirect:/roomTypes/admin?ok";
    }

    // ✅ MOSTRAR FORMULARIO PARA EDITAR HABITACIÓN
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, Model model) {
        RoomType roomType = repo.findById(id);
        if (roomType == null) {
            return "redirect:/roomTypes/admin?error";
        }
        model.addAttribute("roomType", roomType);
        return "habitacion-form";
    }

    // ✅ ACTUALIZAR HABITACIÓN
    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable int id,
            @RequestParam String name,
            @RequestParam String tipoHabitacion,
            @RequestParam int capacity,
            @RequestParam int numero,
            @RequestParam String estado) {
        
        RoomType roomType = repo.findById(id);
        if (roomType == null) {
            return "redirect:/roomTypes/admin?error";
        }
        
        roomType.setName(name);
        roomType.setTipoHabitacion(tipoHabitacion);
        roomType.setCapacity(capacity);
        roomType.setNumero(numero);
        roomType.setEstado(estado);
        
        repo.save(roomType);
        return "redirect:/roomTypes/admin?ok";
    }

    // ✅ ELIMINAR HABITACIÓN
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id) {
        repo.deleteById(id);
        return "redirect:/roomTypes/admin?ok";
    }

    // ✅ VER DETALLE DE UNA HABITACIÓN
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable int id, Model model) {
        RoomType roomType = repo.findById(id);
        if (roomType == null) {
            return "redirect:/roomTypes/admin?error";
        }
        model.addAttribute("roomType", roomType);
        return "habitacion-detalle";
    }
}
