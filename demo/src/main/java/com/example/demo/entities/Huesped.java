package com.example.demo.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "huesped")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Huesped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(length = 20)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(length = 50)
    private String nacionalidad;

    @JsonIgnore
    @OneToMany(mappedBy = "huesped", cascade = CascadeType.ALL)
    private List<Reserva> historialReservas = new ArrayList<>();
}