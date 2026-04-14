package com.example.demo.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false)
    private int piso;

    @Column(nullable = false, length = 30)
    private String estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @Column(length = 255)
    private String notas;



     //@OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        //@JsonIgnore

    //private List<Reserva> reservas = new ArrayList<>();
    
    /**
     * Calcula el estado actual de la habitación basado en sus reservas
     * @return "OCUPADA" si tiene reservas confirmadas o pendientes, "DISPONIBLE" en caso contrario
     */
    //public String calcularEstado() {
      //  if (reservas == null || reservas.isEmpty()) {
           // return "DISPONIBLE";
        //}
        
        //boolean tieneReservaActiva = reservas.stream()
        //        .anyMatch(reserva -> reserva.getEstado() == EstadoReserva.CONFIRMADA || 
        //                             reserva.getEstado() == EstadoReserva.PENDIENTE);
        
        //return tieneReservaActiva ? "OCUPADA" : "DISPONIBLE";
    //}
    
    /**
     * Actualiza el estado de la habitación según sus reservas
     */
    //public void actualizarEstado() {
      //  this.estado = calcularEstado();
    //}
}