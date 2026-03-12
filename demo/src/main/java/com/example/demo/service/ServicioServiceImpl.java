package com.example.demo.service;

import com.example.demo.entities.Servicio;
import com.example.demo.repository.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

    @Autowired
    private  ServicioRepository servicioRepository;

    
    @Override
    public List<Servicio> findAll() {
        return servicioRepository.findAll();
    }

    @Override
    public Servicio findById(Integer id) {
        return servicioRepository.findById(id).orElse(null);
    }

    @Override
    public Servicio save(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    @Override
    public Servicio update(Integer id, Servicio servicio) {
        Servicio existente = servicioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el servicio con id=" + id));

        existente.setNombre(servicio.getNombre());
        existente.setDescripcion(servicio.getDescripcion());
        existente.setPrecio(servicio.getPrecio());
        existente.setImagenUrl(servicio.getImagenUrl());
        existente.setCapacidad(servicio.getCapacidad());
        existente.setPrecioTipo(servicio.getPrecioTipo());
        existente.setHorario(servicio.getHorario());

        return servicioRepository.save(existente);
    }

    @Override
    public void deleteById(Integer id) {
        servicioRepository.deleteById(id);
    }
}