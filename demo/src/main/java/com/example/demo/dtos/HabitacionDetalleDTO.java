package com.example.demo.dtos;

import lombok.Data;

@Data
public class HabitacionDetalleDTO {
    private Integer id;
    private String codigo;
    private Integer piso;
    private String estado;
    private String notas;
    private Integer tipoHabitacionId;
    private String tipoNombre;
    private String descripcion;
    private Integer precio;
    private Integer capacidad;
    private String imagenUrl;
    private String amenities;
}