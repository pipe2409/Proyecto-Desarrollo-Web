package com.example.demo.service;

import com.example.demo.entities.Servicio;

import java.util.List;

public interface ServicioService {

    List<Servicio> findAll();

    Servicio findById(Integer id);

    Servicio save(Servicio servicio);

    Servicio update(Integer id, Servicio servicio);

    void deleteById(Integer id);
}