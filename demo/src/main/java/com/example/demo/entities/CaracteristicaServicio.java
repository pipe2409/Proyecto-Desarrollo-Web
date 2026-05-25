package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cada caracteristica/feature de un servicio (ej. "WiFi", "Aire acondicionado").
 * Antes el front concatenaba todas las features con comas y las metia en el
 * campo Servicio.horario, lo que sobrescribia el horario real y violaba 1FN.
 * Ahora cada feature es una fila independiente con FK al servicio.
 */
@Entity
@Table(name = "caracteristica_servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaracteristicaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String texto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    @JsonIgnore
    private Servicio servicio;
}
