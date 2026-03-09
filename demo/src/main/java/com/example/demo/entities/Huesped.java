package com.example.demo.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "huesped")
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

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(length = 20)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(length = 50)
    private String pais;

    @OneToMany(mappedBy = "huesped", cascade = CascadeType.ALL)
    private List<Reserva> historialReservas = new ArrayList<>();

    public Huesped() {
    }

    public Huesped(Integer id, String nombre, String apellido, String correo, String contrasena,
                   String cedula, String telefono, String direccion, String pais,
                   List<Reserva> historialReservas) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.cedula = cedula;
        this.telefono = telefono;
        this.direccion = direccion;
        this.pais = pais;
        this.historialReservas = historialReservas;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public List<Reserva> getHistorialReservas() { return historialReservas; }
    public void setHistorialReservas(List<Reserva> historialReservas) { this.historialReservas = historialReservas; }
}