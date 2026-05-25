package com.example.demo.dtos;

import com.example.demo.entities.EstadoHabitacion;
import lombok.Data;

@Data
public class HabitacionDetalleDTO {
    private Integer id;
    private String codigo;
    private Integer piso;
    // Jackson serializa el enum como "DISPONIBLE"/"OCUPADA"/... que es lo
    // mismo que el front ya consume (compara h.estado === 'DISPONIBLE').
    private EstadoHabitacion estado;
    private String notas;
    private Integer tipoHabitacionId;
    private String tipoNombre;
    private String descripcion;
    private Integer precio;
    private Integer capacidad;
    private String imagenUrl;
    private String amenities;
}