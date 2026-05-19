package com.example.demo.repository;

import com.example.demo.entities.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, Integer> {
    Optional<Huesped> findByUser_Username(String username);
    Optional<Huesped> findByCedula(String cedula);

    @Query("SELECT h FROM Huesped h JOIN h.user u WHERE u.username LIKE %:dominio%")
    List<Huesped> buscarPorDominioCorreo(@Param("dominio") String dominio);
}