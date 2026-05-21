package com.example.demo.dtos;

import lombok.Data;
import java.util.List;

@Data
public class FacturaResumenDTO {
    private Integer reservaId;
    private String huespedNombre;
    private String habitacionCodigo;
    private String fechaInicio;
    private String fechaFin;
    private Integer noches;
    private Integer precioNoche;
    private Integer subtotalHabitacion;
    private List<ItemFacturaDTO> itemsServicios;
    private Integer subtotalServicios;
    private Integer totalGeneral;
    private String estadoReserva;
}