package com.example.demo.service;

import com.example.demo.entities.Habitacion;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.HabitacionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.repository.TipoHabitacionRepository;

import java.util.List;

@Service
@Transactional
public class HabitacionServiceImpl implements HabitacionService {

    @Autowired
    private  HabitacionRepository habitacionRepository;
    @Autowired
    private  TipoHabitacionRepository tipoHabitacionRepository;



    @Override
    public List<Habitacion> findAll() {
        return habitacionRepository.findAll();
    }

    @Override
    public List<Habitacion> findByTipoId(Integer tipoId) {
        if (tipoId == null) {
            return habitacionRepository.findAll();
        }
        return habitacionRepository.findByTipoHabitacion_Id(tipoId);
    }

    @Override
    public Habitacion findById(Integer id) {
    return habitacionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                    "No existe la habitación con id=" + id));
    }

    @Override
    public Habitacion save(Habitacion habitacion) {
        return habitacionRepository.save(habitacion);
    }

    @Override
    public Habitacion update(Integer id, Habitacion habitacion, Integer tipoHabitacionId) {
        
        Habitacion existente = habitacionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                    "No existe la habitación con id=" + id));

        TipoHabitacion tipo = tipoHabitacionRepository.findById(tipoHabitacionId)
            .orElseThrow(() -> new EntityNotFoundException(
                    "El tipo de habitación seleccionado no existe"));

    existente.setCodigo(habitacion.getCodigo());
    existente.setPiso(habitacion.getPiso());
    existente.setEstado(habitacion.getEstado());
    existente.setTipoHabitacion(tipo);
    existente.setNotas(habitacion.getNotas());

    return habitacionRepository.save(existente);
    }

    @Override
    public void deleteById(Integer id) {
        habitacionRepository.deleteById(id);
    }
}