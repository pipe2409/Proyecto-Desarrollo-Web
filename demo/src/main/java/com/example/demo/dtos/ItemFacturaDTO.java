package com.example.demo.dtos;

import lombok.Data;

@Data
public class ItemFacturaDTO {
    private String servicioNombre;
    private Integer cantidad;
    private Integer precioUnitario;
    private Integer subtotal;
}