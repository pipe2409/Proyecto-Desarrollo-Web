package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "testimonio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Testimonio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String autor;

    @Column(nullable = false, length = 500)
    private String texto;

    // 1 a 5 estrellas
    @Column(nullable = false)
    private Integer estrellas;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
}
