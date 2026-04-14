package com.example.demo.service;

import com.example.demo.entities.Huesped;
import com.example.demo.repository.HuespedRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class HuespedServiceImpl implements HuespedService {

    @Autowired
    private  HuespedRepository huespedRepository;

   

    @Override
    public List<Huesped> findAll() {
        return huespedRepository.findAll();
    }

    @Override
    public Huesped findById(Integer id) {
    return huespedRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                    "No existe el huésped con id=" + id));
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
public Huesped update(
        Integer id,
        String nombre,
        String apellido,
        String correo,
        String cedula,
        String telefono,
        String direccion,
        String nacionalidad
) {
    Huesped huesped = findById(id);

    if (huesped == null) {
        throw new RuntimeException("Huésped no encontrado.");
    }

    huesped.setNombre(nombre);
    huesped.setApellido(apellido);
    huesped.setCorreo(correo);
    huesped.setCedula(cedula);
    huesped.setTelefono(telefono);
    huesped.setDireccion(direccion);
    huesped.setNacionalidad(nacionalidad);

    return save(huesped);
}

    @Override
    public void deleteById(Integer id) {
        huespedRepository.deleteById(id);
    }

    public void cambiarContrasena(Integer id,
                               String actual,
                               String nueva,
                               String confirmar) {

    Huesped huesped = huespedRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                    "No existe el huésped"));

    if (!huesped.getContrasena().equals(actual)) {
        throw new RuntimeException("La contraseña actual es incorrecta.");
    }

    if (!nueva.equals(confirmar)) {
        throw new RuntimeException("La nueva contraseña y la confirmación no coinciden.");
    }

    huesped.setContrasena(nueva);

    huespedRepository.save(huesped);
}
}