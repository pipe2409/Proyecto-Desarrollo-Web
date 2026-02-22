package com.example.demo.service;

import com.example.demo.entities.Servicio;
import com.example.demo.repository.ServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository repository;

    public ServicioServiceImpl(ServicioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Servicio> listarServicios() {
        return repository.findAll();
    }

    @Override
    public void guardarServicio(Servicio servicio) {
        repository.save(servicio);
    }


    @Override
    public void eliminarServicio(int id) {
        repository.deleteById(id);
    }
}