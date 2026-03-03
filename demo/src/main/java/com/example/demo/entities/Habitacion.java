package com.example.demo.entities;

public class Habitacion {

    private int id;
    private String codigo;        // 101, 202, PH-01
    private int piso;             // 1,2,3...
    private String estado;        // DISPONIBLE / OCUPADA / MANTENIMIENTO
    private int tipoHabitacionId; // FK simulada (NO null)
    private String notas;         // opcional

    public Habitacion() {}

    public Habitacion(int id, String codigo, int piso, String estado, int tipoHabitacionId, String notas) {
        this.id = id;
        this.codigo = codigo;
        this.piso = piso;
        this.estado = estado;
        this.tipoHabitacionId = tipoHabitacionId;
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

    public int getTipoHabitacionId() { return tipoHabitacionId; }
    public void setTipoHabitacionId(int tipoHabitacionId) { this.tipoHabitacionId = tipoHabitacionId; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}