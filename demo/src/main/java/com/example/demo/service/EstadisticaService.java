package com.example.demo.service;

import com.example.demo.entities.Reserva;
import com.example.demo.entities.EstadoReserva;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HuespedRepository;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.ItemCuentaRepository;
import com.example.demo.repository.ServicioRepository;
import com.example.demo.repository.TestimonioRepository;
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

    @Autowired
    private TestimonioRepository testimonioRepository;

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
        
        // 5. Ingresos del mes = habitaciones pagadas via Stripe del mes + servicios consumidos del mes.
        // Nota: cuando un cliente paga servicios via Stripe los ItemCuenta se borran (ver
        // CuentaService.pagarCuenta), por lo que la suma de items aqui refleja servicios
        // todavia pendientes + los que no fueron pagados con Stripe. Para un calculo
        // contable estricto deberiamos persistir un historico de pagos.
        int añoActual = LocalDate.now().getYear();
        int mesActual = LocalDate.now().getMonthValue();
        Double ingresosServicios = itemCuentaRepository.sumSubtotalByMes(añoActual, mesActual);
        if (ingresosServicios == null) ingresosServicios = 0.0;

        long ingresosHabitaciones = 0;
        for (Reserva r : reservaRepository.findHabitacionesPagadasDelMes(añoActual, mesActual)) {
            if (r.getHabitacion() == null || r.getHabitacion().getTipoHabitacion() == null) continue;
            long noches = Math.max(1,
                java.time.temporal.ChronoUnit.DAYS.between(r.getFechaInicio().toLocalDate(), r.getFechaFin().toLocalDate()));
            ingresosHabitaciones += noches * r.getHabitacion().getTipoHabitacion().getPrecio();
        }
        double ingresosMes = ingresosServicios + ingresosHabitaciones;

        // Calificacion promedio del hotel a partir de los testimonios.
        Double calificacion = testimonioRepository.findPromedioEstrellas();
        if (calificacion == null) calificacion = 0.0;
        // Redondeo a 1 decimal (4.83 -> 4.8)
        calificacion = Math.round(calificacion * 10.0) / 10.0;
        
        // 6. Proximas llegadas: desde las 00:00 de HOY hasta el final del dia +3.
        // Antes usabamos LocalDateTime.now() como inicio, lo que ocultaba reservas
        // con check-in mas temprano del mismo dia (ej. 25/05 00:00 a las 14:00 ya no
        // aparecia). Tomamos el dia completo para que el operador vea todas las
        // llegadas del dia.
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = LocalDate.now().plusDays(3).atTime(23, 59, 59);
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
        stats.put("calificacion", calificacion);
        stats.put("totalTestimonios", testimonioRepository.count());
        stats.put("proximasLlegadas", proximasLlegadas);
        
        return stats;
    }
}