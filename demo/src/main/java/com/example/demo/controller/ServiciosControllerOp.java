package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ServiciosControllerOp {

  @GetMapping("/serviciosop")
  public String servicios(Model model) {

    // Datos (por ahora) desde el backend (NO en el HTML)
    List<ServicioDTO> servicios = List.of(
        new ServicioDTO("Gastronomía Premium", "Experimenta una experiencia culinaria única con nuestros chefs especializados. Menú gourmet con ingredientes frescos y de...", "$500", "2-3 horas", "/images/servicios/gastronomia.jpg", "gastro"),
        new ServicioDTO("Spa & Wellness", "Relájate con nuestros tratamientos de spa de clase mundial. Masajes terapéuticos, faciales y tratamientos corporales personalizados.", "$800", "1-2 horas", "/images/servicios/spa.jpg", "spa"),
        new ServicioDTO("Gimnasio 24/7", "Acceso completo a nuestras instalaciones de fitness con equipamiento de última generación. Incluye clases grupales y entrenador personal.", "$300", "Día completo", "/images/servicios/gym.jpg", "gym"),
        new ServicioDTO("Tour Guiado", "Descubre los mejores lugares de la costa con nuestros tours personalizados. Incluye transporte, guía profesional y refrigerios.", "$600", "4-6 horas", "/images/servicios/tour.jpg", "tour")
    );

    model.addAttribute("servicios", servicios);
    return "servicios-op"; // servicios.html en templates
  }

  // DTO simple para pasar datos a Thymeleaf
  public record ServicioDTO(
      String titulo,
      String descripcion,
      String precio,
      String duracion,
      String imagen,
      String icono
  ) {}
}