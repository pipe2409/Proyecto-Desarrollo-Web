package com.example.demo.entities;


import lombok.Data;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Data
@Entity
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;        // 101, 202, PH-01

    @Column(nullable = false)
    private int piso;             // 1,2,3...

    @Column(nullable = false, length = 30)
    private String estado;        // DISPONIBLE / OCUPADA / MANTENIMIENTO

    @ManyToOne
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion; 

    @Column(length = 255)
    private String notas;         // opcional

    public Habitacion() {}

    public Habitacion(int id, String codigo, int piso, String estado, TipoHabitacion tipoHabitacion, String notas) {
        this.id = id;
        this.codigo = codigo;
        this.piso = piso;
        this.estado = estado;
        this.tipoHabitacion = tipoHabitacion;
        this.notas = notas;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getPiso() { return piso; }
    public void setPiso(int piso) { this.piso = piso; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public TipoHabitacion getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}