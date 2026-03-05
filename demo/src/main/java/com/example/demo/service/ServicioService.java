package com.example.demo.service;

import com.example.demo.entities.Servicio;
import java.util.List;

public interface ServicioService {

    List<Servicio> listarServicios();

    void guardarServicio(Servicio servicio);

    void eliminarServicio(int id);

    Servicio buscarPorId(int id); 
}