package com.example.demo.repository;

import com.example.demo.entities.Huesped;
import com.example.demo.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
List<Reserva> findByHuesped(Huesped huesped);
}