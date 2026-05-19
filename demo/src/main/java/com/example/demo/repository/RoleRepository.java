package com.example.demo.repository;

import com.example.demo.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Busca un rol por su nombre (ej: ROLE_CLIENTE)
    Optional<Role> findByName(String name);
}