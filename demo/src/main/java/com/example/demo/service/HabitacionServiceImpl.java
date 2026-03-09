package com.example.demo.service;

import com.example.demo.entities.Habitacion;
import com.example.demo.repository.HabitacionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public HabitacionServiceImpl(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

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
        return habitacionRepository.findById(id).orElse(null);
    }

    @Override
    public Habitacion save(Habitacion habitacion) {
        return habitacionRepository.save(habitacion);
    }

    @Override
    public Habitacion update(Integer id, Habitacion habitacion) {
        Habitacion existente = habitacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe la habitación con id=" + id));

        existente.setCodigo(habitacion.getCodigo());
        existente.setPiso(habitacion.getPiso());
        existente.setEstado(habitacion.getEstado());
        existente.setTipoHabitacion(habitacion.getTipoHabitacion());
        existente.setNotas(habitacion.getNotas());

        return habitacionRepository.save(existente);
    }

    @Override
    public void deleteById(Integer id) {
        habitacionRepository.deleteById(id);
    }
}