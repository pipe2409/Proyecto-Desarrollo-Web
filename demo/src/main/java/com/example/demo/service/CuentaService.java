package com.example.demo.service;

import com.example.demo.entities.CuentaHabitacion;
import com.example.demo.entities.ItemCuenta;
import com.example.demo.entities.Reserva;
import com.example.demo.entities.Servicio;
import com.example.demo.entities.EstadoReserva;
import com.example.demo.repository.CuentaHabitacionRepository;
import com.example.demo.repository.ItemCuentaRepository;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.ServicioRepository;
import com.example.demo.repository.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaService {

    @Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired
    private CuentaHabitacionRepository cuentaRepository;
    
    @Autowired
    private ItemCuentaRepository itemCuentaRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @Autowired
    private HabitacionRepository habitacionRepository;

    // Buscar reserva activa por número de habitación
    public Reserva findReservaActivaByHabitacionCodigo(String codigoHabitacion) {
        var habitacion = habitacionRepository.findAll().stream()
            .filter(h -> h.getCodigo().equals(codigoHabitacion))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));
        
        return reservaRepository.findAll().stream()
            .filter(r -> r.getHabitacion().getId().equals(habitacion.getId()))
            .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.PENDIENTE)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No hay reserva activa para esta habitación"));
    }

    // Obtener o crear cuenta de habitación
    @Transactional
    public CuentaHabitacion getOrCreateCuentaByReserva(Reserva reserva) {
        CuentaHabitacion cuenta = reserva.getCuentaHabitacion();
        if (cuenta == null) {
            cuenta = new CuentaHabitacion();
            cuenta.setReserva(reserva);
            cuenta.setTotal(0);
            cuenta = cuentaRepository.save(cuenta);
            reserva.setCuentaHabitacion(cuenta);
            reservaRepository.save(reserva);
        }
        return cuenta;
    }

    // Obtener items de una cuenta
    public List<ItemCuenta> getItemsByCuentaId(Integer cuentaId) {
        return itemCuentaRepository.findByCuentaHabitacionId(cuentaId);
    }

    // Agregar servicio a la cuenta
    @Transactional
    public ItemCuenta agregarServicio(Integer cuentaId, Integer servicioId, Integer cantidad) {
        CuentaHabitacion cuenta = cuentaRepository.findById(cuentaId)
            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        
        Servicio servicio = servicioRepository.findById(servicioId)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        // Buscar si ya existe el mismo servicio en la cuenta
        ItemCuenta itemExistente = itemCuentaRepository.findByCuentaHabitacionId(cuentaId).stream()
            .filter(i -> i.getServicio().getId().equals(servicioId))
            .findFirst()
            .orElse(null);
        
        if (itemExistente != null) {
            int nuevaCantidad = itemExistente.getCantidad() + cantidad;
            itemExistente.setCantidad(nuevaCantidad);
            itemExistente.setSubtotal(servicio.getPrecio() * nuevaCantidad);
            itemCuentaRepository.save(itemExistente);
        } else {
            ItemCuenta nuevoItem = new ItemCuenta();
            nuevoItem.setCuentaHabitacion(cuenta);
            nuevoItem.setServicio(servicio);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setSubtotal(servicio.getPrecio() * cantidad);
            itemExistente = itemCuentaRepository.save(nuevoItem);
        }
        
        // Actualizar total de la cuenta
        int total = itemCuentaRepository.findByCuentaHabitacionId(cuentaId).stream()
            .mapToInt(ItemCuenta::getSubtotal)
            .sum();
        cuenta.setTotal(total);
        cuentaRepository.save(cuenta);
        
        return itemExistente;
    }

    // Eliminar item de la cuenta
    @Transactional
    public void eliminarItem(Integer itemId) {
        ItemCuenta item = itemCuentaRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        
        Integer cuentaId = item.getCuentaHabitacion().getId();
        itemCuentaRepository.delete(item);
        
        // Actualizar total de la cuenta
        int total = itemCuentaRepository.findByCuentaHabitacionId(cuentaId).stream()
            .mapToInt(ItemCuenta::getSubtotal)
            .sum();
        
        CuentaHabitacion cuenta = cuentaRepository.findById(cuentaId).get();
        cuenta.setTotal(total);
        cuentaRepository.save(cuenta);
    }

    // Pagar todos los servicios de la cuenta
    @Transactional
    public int pagarCuenta(Integer cuentaId) {
        CuentaHabitacion cuenta = cuentaRepository.findById(cuentaId)
            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        
        List<ItemCuenta> items = itemCuentaRepository.findByCuentaHabitacionId(cuentaId);
        int totalPagado = cuenta.getTotal();
        
        // Eliminar items (en un sistema real, se moverían a un historial)
        for (ItemCuenta item : items) {
            itemCuentaRepository.delete(item);
        }
        
        // Resetear cuenta
        cuenta.setTotal(0);
        cuentaRepository.save(cuenta);
        
        return totalPagado;
    }
}