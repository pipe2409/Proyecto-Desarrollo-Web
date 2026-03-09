package com.example.demo.service;

import com.example.demo.entities.Huesped;
import com.example.demo.repository.HuespedRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class HuespedServiceImpl implements HuespedService {

    private final HuespedRepository huespedRepository;

    public HuespedServiceImpl(HuespedRepository huespedRepository) {
        this.huespedRepository = huespedRepository;
    }

    @Override
    public List<Huesped> findAll() {
        return huespedRepository.findAll();
    }

    @Override
    public Huesped findById(Integer id) {
        return huespedRepository.findById(id).orElse(null);
    }

    @Override
    public Huesped findByCorreo(String correo) {
        return huespedRepository.findByCorreo(correo).orElse(null);
    }

    @Override
    public Huesped login(String correo, String contrasena) {
        return huespedRepository.findByCorreoAndContrasena(correo, contrasena).orElse(null);
    }

    @Override
    public Huesped save(Huesped huesped) {
        return huespedRepository.save(huesped);
    }

    @Override
    public Huesped update(Integer id, Huesped huesped) {
        Huesped existente = huespedRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el huésped con id=" + id));

        existente.setNombre(huesped.getNombre());
        existente.setApellido(huesped.getApellido());
        existente.setCorreo(huesped.getCorreo());
        existente.setContrasena(huesped.getContrasena());
        existente.setCedula(huesped.getCedula());
        existente.setTelefono(huesped.getTelefono());
        existente.setDireccion(huesped.getDireccion());
        existente.setPais(huesped.getPais());

        return huespedRepository.save(existente);
    }

    @Override
    public void deleteById(Integer id) {
        huespedRepository.deleteById(id);
    }
}