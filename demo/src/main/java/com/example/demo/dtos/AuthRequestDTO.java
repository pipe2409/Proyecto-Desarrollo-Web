package com.example.demo.dtos;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}