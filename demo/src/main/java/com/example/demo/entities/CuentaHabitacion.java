package com.example.demo.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class CuentaHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int total;

    // 🔥 EVITA ciclo con Reserva
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    // 🔥 EVITA ciclo con ItemCuenta
    @JsonIgnore
    @OneToMany(mappedBy = "cuentaHabitacion")
    private List<ItemCuenta> items;
}