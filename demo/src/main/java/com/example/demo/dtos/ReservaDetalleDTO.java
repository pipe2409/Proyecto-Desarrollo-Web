package com.example.demo.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservaDetalleDTO {
    private Integer id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer cantidadPersonas;
    private String estado;
    private String habitacionCodigo;
    private String habitacionTipo;
    private Integer precioNoche;
    private String huespedNombreCompleto;
    private Integer totalCuenta;
}