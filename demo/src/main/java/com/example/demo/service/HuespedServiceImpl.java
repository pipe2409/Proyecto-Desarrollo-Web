package com.example.demo.service;

import com.example.demo.entities.Huesped;
import com.example.demo.repository.HuespedesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HuespedServiceImpl implements HuespedService {

    @Autowired
    private HuespedesRepository repository;


    @Override
    public List<Huesped> listarHuespedes() {
        return repository.findAll();
    }

    @Override
    public Huesped buscarPorId(int id) {
        return repository.findById(id);
    }

    @Override
    public void guardarHuesped(Huesped huesped) {
        repository.save(huesped);
    }

    @Override
    public void eliminarHuesped(int id) {
        repository.deleteById(id);
    }
}
