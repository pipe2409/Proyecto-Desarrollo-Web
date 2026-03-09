package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoHabitacion {

    private int id;
    private String nombre;
    private String descripcion;
    private int precio;          // si manejan COP entero como en Servicio
    private String imagenUrl;
    private int capacidad;       // personas
    private String camas;        // ej: "1 Queen", "2 Dobles"
    private String amenities;    // texto corto (wifi, tv, etc.)
    private boolean disponible;  // similar a "activo"

}