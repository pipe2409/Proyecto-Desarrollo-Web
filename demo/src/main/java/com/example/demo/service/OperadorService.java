package com.example.demo.service;

import com.example.demo.entities.Operador;
import com.example.demo.repository.OperadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperadorService {

    @Autowired
    private OperadorRepository operadorRepository;
    
    public Operador login(String correo, String contrasena) {
        // Buscar operador por correo (necesitas agregar método en Repository)
        return operadorRepository.findByCorreo(correo)
            .filter(operador -> operador.getContrasena().equals(contrasena))        .orElse(null);
    }
}