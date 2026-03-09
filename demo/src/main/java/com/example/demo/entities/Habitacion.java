package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Habitacion {

    private int id;
    private String codigo;        // 101, 202, PH-01
    private int piso;             // 1,2,3...
    private String estado;        // DISPONIBLE / OCUPADA / MANTENIMIENTO
    private int tipoHabitacionId; // FK simulada (NO null)
    private String notas;         // opcional

}