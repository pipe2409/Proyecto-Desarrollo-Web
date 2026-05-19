package com.example.demo.service;

import com.example.demo.entities.Operador;
import com.example.demo.repository.OperadorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository operadorRepository;
    private final PasswordEncoder passwordEncoder;

    public OperadorServiceImpl(OperadorRepository operadorRepository, PasswordEncoder passwordEncoder) {
        this.operadorRepository = operadorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Operador> findAll() {
        return operadorRepository.findAll();
    }

    @Override
    public Operador findById(Integer id) {
        return operadorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El operador no existe con id=" + id));
    }

    @Override
    public Operador save(Operador operador) {
        return operadorRepository.save(operador);
    }

    @Override
    public Operador update(Integer id, Operador operador) {
        Operador existente = operadorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe el operador con id=" + id));

        if (operador.getUser() != null) existente.getUser().setUsername(operador.getUser().getUsername());

        // Solo actualiza la contraseña si viene un valor nuevo y lo encripta
        if (operador.getUser() != null && operador.getUser().getPassword() != null && !operador.getUser().getPassword().isBlank()) {
            existente.getUser().setPassword(passwordEncoder.encode(operador.getUser().getPassword()));
        }

        return operadorRepository.save(existente);
    }

    @Override
    public void deleteById(Integer id) {
        operadorRepository.deleteById(id);
    }

    @Override
    public Operador login(String correo, String contrasena) {
        return operadorRepository.findByUser_Username(correo)
                .filter(op -> op.getUser() != null && passwordEncoder.matches(contrasena, op.getUser().getPassword()))
                .orElse(null);
    }
}