package com.example.demo.service;

import com.example.demo.entities.Habitacion;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.TipoHabitacionRepository;
import com.example.demo.service.HabitacionService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitacionServiceImpl implements HabitacionService {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    @Override
    public List<Habitacion> listar(Integer tipoId) {
        if (tipoId != null) {
            return habitacionRepository.findByTipoId(tipoId);
        }
        return habitacionRepository.findAll();
    }

    @Override
    public Habitacion obtenerPorId(int id) {
        Habitacion habitacion = habitacionRepository.findById(id);
        if (habitacion == null) {
            throw new RuntimeException("Habitación no encontrada con ID: " + id);
        }
        return habitacion;
    }

    @Override
public Habitacion construirNueva(Integer tipoId) {
    Habitacion habitacion = new Habitacion();
    if (tipoId != null) {
        habitacion.setTipoHabitacionId(tipoId);
    }
    return habitacion;
}

    @Override
    public void crear(Habitacion habitacion) {
        habitacionRepository.save(habitacion);
    }

    @Override
public void actualizar(int id, Habitacion habitacion) {
    Habitacion existente = obtenerPorId(id);
    existente.setCodigo(habitacion.getCodigo());
    existente.setPiso(habitacion.getPiso());
    existente.setEstado(habitacion.getEstado());
    existente.setTipoHabitacionId(habitacion.getTipoHabitacionId());
    existente.setNotas(habitacion.getNotas());
    habitacionRepository.save(existente);
}
    @Override
    public void eliminar(int id) {
        obtenerPorId(id); // valida que exista antes de eliminar
        habitacionRepository.deleteById(id);
    }

    @Override
    public Map<Integer, String> nombresTipoHabitacion() {
        return tipoHabitacionRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        TipoHabitacion::getId,
                        TipoHabitacion::getNombre
                ));
    }
}