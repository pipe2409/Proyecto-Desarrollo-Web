package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    private int id;
    private String nombre;
    private String descripcion;
    private float precio;
    private String imagenUrl;

    // Nuevos campos
    private int capacidad; // cuántas personas puede atender
    private String precioTipo; // "por persona" o "por grupo"
    private String horario; // horario o disponibilidad
}

