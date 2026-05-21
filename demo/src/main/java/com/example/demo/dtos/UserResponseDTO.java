package com.example.demo.dtos;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email; // Mapeado desde username de UserEntity
    // Otros campos públicos no sensibles que quieras exponer
}