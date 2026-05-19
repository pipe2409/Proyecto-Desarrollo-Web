package com.example.demo.service;

import com.example.demo.entities.Huesped;
import com.example.demo.repository.HuespedRepository;
import com.example.demo.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class HuespedServiceImpl implements HuespedService {

    private final HuespedRepository huespedRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public HuespedServiceImpl(HuespedRepository huespedRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.huespedRepository = huespedRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

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
        // Asumiendo que actualizaste el repositorio para buscar por el username del UserEntity
        return huespedRepository.findByUser_Username(correo).orElse(null);
    }

    @Override
    public Huesped login(String correo, String contrasena) {
        return huespedRepository.findByUser_Username(correo)
                .filter(h -> h.getUser() != null && passwordEncoder.matches(contrasena, h.getUser().getPassword()))
                .orElse(null);
    }

    @Override
    public Huesped save(Huesped huesped) {
        if (huesped.getNombre() == null || huesped.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio.");
        }

        if (huesped.getApellido() == null || huesped.getApellido().isBlank()) {
            throw new RuntimeException("El apellido es obligatorio.");
        }

        if (huesped.getUser() == null || huesped.getUser().getUsername() == null || huesped.getUser().getUsername().isBlank()) {
            throw new RuntimeException("El correo es obligatorio.");
        }

        if (huesped.getUser() == null || huesped.getUser().getPassword() == null || huesped.getUser().getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria.");
        }

        // Encriptar contraseña antes de guardar
        huesped.getUser().setPassword(passwordEncoder.encode(huesped.getUser().getPassword()));

        // Asignar rol predeterminado
        roleRepository.findByName("ROLE_CLIENTE")
                .ifPresent(role -> huesped.getUser().getRoles().add(role));

        if (huesped.getCedula() == null || huesped.getCedula().isBlank()) {
            throw new RuntimeException("La cédula es obligatoria.");
        }

        Huesped existenteCorreo = huespedRepository.findByUser_Username(huesped.getUser().getUsername()).orElse(null);
        if (existenteCorreo != null) {
            throw new RuntimeException("Ya existe un usuario con ese correo.");
        }

        Huesped existenteCedula = huespedRepository.findByCedula(huesped.getCedula()).orElse(null);
        if (existenteCedula != null) {
            throw new RuntimeException("Ya existe un usuario con esa cédula.");
        }

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

        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("El nombre es obligatorio.");
        }

        if (apellido == null || apellido.isBlank()) {
            throw new RuntimeException("El apellido es obligatorio.");
        }

        if (correo == null || correo.isBlank()) {
            throw new RuntimeException("El correo es obligatorio.");
        }

        if (cedula == null || cedula.isBlank()) {
            throw new RuntimeException("La cédula es obligatoria.");
        }

        Huesped existenteCorreo = huespedRepository.findByUser_Username(correo).orElse(null);
        if (existenteCorreo != null && !existenteCorreo.getId().equals(id)) {
            throw new RuntimeException("Ya existe un usuario con ese correo.");
        }

        Huesped existenteCedula = huespedRepository.findByCedula(cedula).orElse(null);
        if (existenteCedula != null && !existenteCedula.getId().equals(id)) {
            throw new RuntimeException("Ya existe un usuario con esa cédula.");
        }

        huesped.setNombre(nombre);
        huesped.setApellido(apellido);
        if (huesped.getUser() != null) huesped.getUser().setUsername(correo);
        huesped.setCedula(cedula);
        huesped.setTelefono(telefono);
        huesped.setDireccion(direccion);
        huesped.setNacionalidad(nacionalidad);

        return huespedRepository.save(huesped);
    }

    @Override
    public void deleteById(Integer id) {
        huespedRepository.deleteById(id);
    }

    @Override
    public void cambiarContrasena(Integer id,
                                  String actual,
                                  String nueva,
                                  String confirmar) {

        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe el huésped"));

        if (huesped.getUser() == null || !passwordEncoder.matches(actual, huesped.getUser().getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta.");
        }

        if (!nueva.equals(confirmar)) {
            throw new RuntimeException("La nueva contraseña y la confirmación no coinciden.");
        }

        huesped.getUser().setPassword(passwordEncoder.encode(nueva));

        huespedRepository.save(huesped);
    }
}