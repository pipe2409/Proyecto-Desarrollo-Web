package com.example.demo.service;
 
import com.example.demo.entities.Operador;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
public interface OperadorService {
 
    List<Operador> findAll();
 
    Operador findById(Integer id);
 
    Operador save(Operador operador);
 
    Operador update(Integer id, Operador operador);
 
    void deleteById(Integer id);
 
    // Login: retorna el operador si las credenciales son válidas, null si no
    Operador login(String correo, String contrasena);
}
 