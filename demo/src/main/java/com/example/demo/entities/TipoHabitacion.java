package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


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
    
    @Min(0)
    private Integer precio;

    @Column(name = "imagen_url", columnDefinition = "TEXT")
    private String imagenUrl;


    
    @Min(0)
    private Integer capacidad;

    private String camas;

    private String amenities;
    @Column(nullable = false)
    private boolean disponible = true; 
     @OneToMany(mappedBy = "tipoHabitacion")
    @JsonIgnore 
    private java.util.List<Habitacion> habitaciones;

}