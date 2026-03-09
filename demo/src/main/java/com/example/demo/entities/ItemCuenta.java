package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
public class ItemCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int cantidad;
    private int subtotal;

    @ManyToOne
    @JoinColumn(name = "cuenta_habitacion_id")
    private CuentaHabitacion cuentaHabitacion;

    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;

    public ItemCuenta() {
    }

    public ItemCuenta( int cantidad, int subtotal, CuentaHabitacion cuentaHabitacion, Servicio servicio) {
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.cuentaHabitacion = cuentaHabitacion;
        this.servicio = servicio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public CuentaHabitacion getCuentaHabitacion() {
        return cuentaHabitacion;
    }

    public void setCuentaHabitacion(CuentaHabitacion cuentaHabitacion) {
        this.cuentaHabitacion = cuentaHabitacion;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
}