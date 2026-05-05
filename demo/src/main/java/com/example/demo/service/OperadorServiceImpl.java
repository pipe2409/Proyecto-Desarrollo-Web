package com.example.demo.service;

import com.example.demo.entities.Operador;
import com.example.demo.repository.OperadorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository operadorRepository;

    public OperadorServiceImpl(OperadorRepository operadorRepository) {
        this.operadorRepository = operadorRepository;
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

        existente.setCorreo(operador.getCorreo());

        // Solo actualiza la contraseña si viene un valor nuevo
        if (operador.getContrasena() != null && !operador.getContrasena().isBlank()) {
            existente.setContrasena(operador.getContrasena());
        }

        return operadorRepository.save(existente);
    }

    @Override
    public void deleteById(Integer id) {
        operadorRepository.deleteById(id);
    }

    @Override
    public Operador login(String correo, String contrasena) {
        return operadorRepository.findByCorreo(correo)
                .filter(op -> op.getContrasena().equals(contrasena))
                .orElse(null);
    }
}