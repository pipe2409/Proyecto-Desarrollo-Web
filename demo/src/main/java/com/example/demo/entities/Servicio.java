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
}

