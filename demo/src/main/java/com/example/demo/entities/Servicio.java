package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private int precio;

    private String imagenUrl;

    @Column(nullable = false)
    private int capacidad;

    @Column(length = 100, nullable = false)
    private String precioTipo;

    @Column(length = 100, nullable = false)
    private String horario;
}