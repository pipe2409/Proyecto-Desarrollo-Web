package com.example.demo.entities;

import com.example.demo.dtos.HabitacionDetalleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface HabitacionMapper {

    HabitacionMapper GET_MAPPER = Mappers.getMapper(HabitacionMapper.class);

    @Mapping(source = "tipoHabitacion.id", target = "tipoHabitacionId")
    @Mapping(source = "tipoHabitacion.nombre", target = "tipoNombre")
    @Mapping(source = "tipoHabitacion.descripcion", target = "descripcion")
    @Mapping(source = "tipoHabitacion.precio", target = "precio")
    @Mapping(source = "tipoHabitacion.capacidad", target = "capacidad")
    @Mapping(source = "tipoHabitacion.imagenUrl", target = "imagenUrl")
    @Mapping(source = "tipoHabitacion.amenities", target = "amenities")
    HabitacionDetalleDTO toDto(Habitacion habitacion);

    List<HabitacionDetalleDTO> toDtoList(List<Habitacion> habitaciones);
}