package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users") // Asumiendo un nombre de tabla
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username; // Este será el email
    private String password;
    private String nombre;
    private String apellido;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Si la cuenta confirmó el correo de verificación
    @Column(nullable = false)
    private boolean verificado = false;

    // Token unico que se manda por email para verificar la cuenta
    @Column(length = 100)
    private String tokenVerificacion;

    // Token unico para recuperar la contraseña (se manda por email).
    // Nullable: solo se setea cuando el usuario solicita la recuperacion.
    @Column(length = 100)
    private String tokenRecuperacion;

    // Cuando expira el token de recuperacion. Si esta vencido, el endpoint
    // de restablecer rechaza la peticion.
    private LocalDateTime tokenRecuperacionExpira;
}