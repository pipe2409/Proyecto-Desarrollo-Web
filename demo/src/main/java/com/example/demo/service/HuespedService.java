package com.example.demo.service;

import com.example.demo.entities.Huesped;
import java.util.List;

public interface HuespedService {

    List<Huesped> listarHuespedes();

    Huesped buscarPorId(int id);

    void guardarHuesped(Huesped huesped);

    void eliminarHuesped(int id);
}