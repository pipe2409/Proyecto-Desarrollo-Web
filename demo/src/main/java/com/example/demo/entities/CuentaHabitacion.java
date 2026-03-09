package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;



@Entity
@Data
@NoArgsConstructor
public class CuentaHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private int total;


    @OneToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @OneToMany(mappedBy = "cuentaHabitacion")
    private List<ItemCuenta> items;
}