package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "servicio")
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

    public Servicio() {
    }

    public Servicio(int id, String nombre, String descripcion, int precio,
                    String imagenUrl, int capacidad, String precioTipo, String horario) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.capacidad = capacidad;
        this.precioTipo = precioTipo;
        this.horario = horario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public String getPrecioTipo() { return precioTipo; }
    public void setPrecioTipo(String precioTipo) { this.precioTipo = precioTipo; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}