package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Huesped {

    private int id;
    private String nombre;
    private String apellido;
    private String correo;
    private String contraseña;
    private String cedula;
    private String telefono;
    private String direccion;
    private String pais;
    private List<Reserva> historialReservas; // Lista de reservas asociadas al huésped

}
