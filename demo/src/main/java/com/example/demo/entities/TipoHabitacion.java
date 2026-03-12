package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_habitacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    
    private String descripcion;

    private int precio;

    @Column(name = "imagen_url", columnDefinition = "TEXT")
    private String imagenUrl;

    private int capacidad;

    private String camas;

    private String amenities;

    private boolean disponible;
}