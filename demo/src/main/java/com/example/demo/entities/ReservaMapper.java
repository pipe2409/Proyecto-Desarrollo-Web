package com.example.demo.entities;

import com.example.demo.dtos.ReservaDetalleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    ReservaMapper GET_MAPPER = Mappers.getMapper(ReservaMapper.class);

    @Mapping(source = "habitacion.codigo", target = "habitacionCodigo")
    @Mapping(source = "habitacion.tipoHabitacion.nombre", target = "habitacionTipo")
    @Mapping(source = "habitacion.tipoHabitacion.precio", target = "precioNoche")
    @Mapping(target = "huespedNombreCompleto", expression = "java(reserva.getHuesped().getNombre() + \" \" + reserva.getHuesped().getApellido())")
    @Mapping(source = "cuentaHabitacion.total", target = "totalCuenta")
    ReservaDetalleDTO toDto(Reserva reserva);

    List<ReservaDetalleDTO> toDtoList(List<Reserva> reservas);
}