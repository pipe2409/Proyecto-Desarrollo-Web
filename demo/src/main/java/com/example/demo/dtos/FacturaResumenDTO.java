package com.example.demo.dtos;

import lombok.Data;
import java.util.List;

@Data
public class FacturaResumenDTO {
    private Integer reservaId;
    private Integer cuentaId;          // ID de la CuentaHabitacion (para poder pagar)
    private String huespedNombre;
    private String habitacionCodigo;
    private String fechaInicio;
    private String fechaFin;
    private Integer noches;
    private Integer precioNoche;
    private Integer subtotalHabitacion;
    private boolean habitacionPagada;       // True si ya se pago la habitacion via Stripe al reservar
    private List<ItemFacturaDTO> itemsServicios;
    private Integer subtotalServicios;
    private Integer totalGeneral;
    private Integer deudaPendiente;         // Lo que el huesped TODAVIA debe (excluye lo ya pagado)
    private String estadoReserva;
}