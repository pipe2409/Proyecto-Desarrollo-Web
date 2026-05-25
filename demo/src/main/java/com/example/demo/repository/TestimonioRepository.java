package com.example.demo.repository;

import com.example.demo.entities.Testimonio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonioRepository extends JpaRepository<Testimonio, Integer> {
    // Mas recientes primero
    List<Testimonio> findAllByOrderByFechaCreacionDesc();

    // Promedio de estrellas. Devuelve null si no hay testimonios.
    @Query("SELECT AVG(t.estrellas) FROM Testimonio t")
    Double findPromedioEstrellas();
}
