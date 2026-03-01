package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    private int id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private int CantidadPersonas;
    private EstadoReserva estado;
    
}
