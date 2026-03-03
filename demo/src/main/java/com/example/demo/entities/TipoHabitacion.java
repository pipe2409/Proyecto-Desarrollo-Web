package com.example.demo.entities;

public class TipoHabitacion {

    private int id;
    private String nombre;
    private String descripcion;
    private int precio;          // si manejan COP entero como en Servicio
    private String imagenUrl;
    private int capacidad;       // personas
    private String camas;        // ej: "1 Queen", "2 Dobles"
    private String amenities;    // texto corto (wifi, tv, etc.)
    private boolean disponible;  // similar a "activo"

    public TipoHabitacion() {}

    public TipoHabitacion(int id, String nombre, String descripcion, int precio, String imagenUrl,
                          int capacidad, String camas, String amenities, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.capacidad = capacidad;
        this.camas = camas;
        this.amenities = amenities;
        this.disponible = disponible;
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

    public String getCamas() { return camas; }
    public void setCamas(String camas) { this.camas = camas; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}