package com.example.demo.service;

import com.example.demo.entities.Reserva;
import com.example.demo.entities.EstadoReserva;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HuespedRepository;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.ItemCuentaRepository;
import com.example.demo.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class EstadisticaService {

    @Autowired
    private HabitacionRepository habitacionRepository;
    
    @Autowired
    private HuespedRepository huespedRepository;
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired
    private ItemCuentaRepository itemCuentaRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        // 1. Habitaciones
        long totalHabitaciones = habitacionRepository.count();
        long habitacionesOcupadas = habitacionRepository.countByEstadoIn(List.of("OCUPADA", "RESERVADA"));
        
        // 2. Reservas
        long reservasActivas = reservaRepository.countByEstadoIn(List.of(EstadoReserva.CONFIRMADA, EstadoReserva.PENDIENTE));
        
        // 3. Huéspedes
        long totalHuespedes = huespedRepository.count();
        
        // 4. Servicios
        long totalServicios = servicioRepository.count();
        
        // 5. Ingresos del mes
        int añoActual = LocalDate.now().getYear();
        int mesActual = LocalDate.now().getMonthValue();
        Double ingresosMes = itemCuentaRepository.sumSubtotalByMes(añoActual, mesActual);
        if (ingresosMes == null) ingresosMes = 0.0;
        
        // 6. Próximas llegadas (próximos 3 días)
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = LocalDateTime.now().plusDays(3);
        List<Reserva> reservasProximas = reservaRepository.findByEstadoInAndFechaInicioBetweenOrderByFechaInicioAsc(
            List.of(EstadoReserva.CONFIRMADA, EstadoReserva.PENDIENTE), inicio, fin);
        
        List<Map<String, Object>> proximasLlegadas = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        
        for (Reserva reserva : reservasProximas) {
            Map<String, Object> llegada = new HashMap<>();
            llegada.put("id", reserva.getId());
            llegada.put("nombre", reserva.getHuesped().getNombre());
            llegada.put("apellido", reserva.getHuesped().getApellido());
            llegada.put("habitacion", reserva.getHabitacion().getCodigo());
            llegada.put("fechaInicio", reserva.getFechaInicio().format(formatter));
            llegada.put("personas", reserva.getCantidadPersonas());
            proximasLlegadas.add(llegada);
        }
        
        // Calcular porcentaje de ocupación
        double porcentajeOcupacion = totalHabitaciones > 0 ? 
            (habitacionesOcupadas * 100.0) / totalHabitaciones : 0;
        
        stats.put("totalHabitaciones", totalHabitaciones);
        stats.put("habitacionesOcupadas", habitacionesOcupadas);
        stats.put("porcentajeOcupacion", Math.round(porcentajeOcupacion));
        stats.put("reservasActivas", reservasActivas);
        stats.put("totalHuespedes", totalHuespedes);
        stats.put("serviciosActivos", totalServicios);
        stats.put("totalServicios", totalServicios);
        stats.put("ingresosMes", ingresosMes);
        stats.put("proximasLlegadas", proximasLlegadas);
        
        return stats;
    }
}