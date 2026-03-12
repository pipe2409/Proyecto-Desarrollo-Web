package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int cantidad;
    private int subtotal;

    @ManyToOne
    @JoinColumn(name = "cuenta_habitacion_id")
    private CuentaHabitacion cuentaHabitacion;

    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;
}