package com.example.demo.dtos;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private String token;
    // Otros campos públicos no sensibles que quieras exponer
}