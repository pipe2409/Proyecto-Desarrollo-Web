package com.example.demo.service;

import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.TipoHabitacionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TipoHabitacionServiceImpl implements TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;

    public TipoHabitacionServiceImpl(TipoHabitacionRepository tipoHabitacionRepository) {
        this.tipoHabitacionRepository = tipoHabitacionRepository;
    }

    @Override
    public List<TipoHabitacion> findAll() {
        return tipoHabitacionRepository.findAll();
    }

    @Override
    public TipoHabitacion findById(Integer id) {
        return tipoHabitacionRepository.findById(id)
                .orElse(null);
    }

    @Override
    public TipoHabitacion save(TipoHabitacion tipoHabitacion) {
        return tipoHabitacionRepository.save(tipoHabitacion);
    }

    @Override
    public TipoHabitacion update(Integer id, TipoHabitacion tipoHabitacion) {
        TipoHabitacion existente = tipoHabitacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el tipo con id=" + id));

        existente.setNombre(tipoHabitacion.getNombre());
        existente.setDescripcion(tipoHabitacion.getDescripcion());
        existente.setPrecio(tipoHabitacion.getPrecio());
        existente.setImagenUrl(tipoHabitacion.getImagenUrl());
        existente.setCapacidad(tipoHabitacion.getCapacidad());
        existente.setCamas(tipoHabitacion.getCamas());
        existente.setAmenities(tipoHabitacion.getAmenities());
        existente.setDisponible(tipoHabitacion.isDisponible());

        return tipoHabitacionRepository.save(existente);
    }

    @Override
    public void deleteById(Integer id) {
        tipoHabitacionRepository.deleteById(id);
    }
}