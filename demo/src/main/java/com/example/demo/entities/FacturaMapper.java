package com.example.demo.entities;

import com.example.demo.dtos.FacturaResumenDTO;
import com.example.demo.dtos.ItemFacturaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FacturaMapper {

    @Mapping(source = "servicio.nombre", target = "servicioNombre")
    @Mapping(source = "servicio.precio", target = "precioUnitario")
    ItemFacturaDTO toItemDto(ItemCuenta item);

    List<ItemFacturaDTO> toItemDtoList(List<ItemCuenta> items);

    default FacturaResumenDTO toFacturaDto(Reserva reserva) {
        if (reserva == null) return null;

        FacturaResumenDTO dto = new FacturaResumenDTO();
        dto.setReservaId(reserva.getId());
        dto.setHuespedNombre(reserva.getHuesped().getNombre() + " " + reserva.getHuesped().getApellido());
        dto.setHabitacionCodigo(reserva.getHabitacion().getCodigo());
        dto.setFechaInicio(reserva.getFechaInicio().toLocalDate().toString());
        dto.setFechaFin(reserva.getFechaFin().toLocalDate().toString());
        dto.setEstadoReserva(reserva.getEstado().toString());

        // Cálculo de noches
        long noches = ChronoUnit.DAYS.between(reserva.getFechaInicio().toLocalDate(), reserva.getFechaFin().toLocalDate());
        if (noches <= 0) noches = 1; 

        int precioNoche = reserva.getHabitacion().getTipoHabitacion().getPrecio();
        int subtotalHabitacion = (int) (noches * precioNoche);

        dto.setNoches((int) noches);
        dto.setPrecioNoche(precioNoche);
        dto.setSubtotalHabitacion(subtotalHabitacion);

        int subtotalServicios = 0;
        if (reserva.getCuentaHabitacion() != null) {
            dto.setItemsServicios(toItemDtoList(reserva.getCuentaHabitacion().getItems()));
            subtotalServicios = reserva.getCuentaHabitacion().getTotal();
        }
        
        dto.setSubtotalServicios(subtotalServicios);
        dto.setTotalGeneral(subtotalHabitacion + subtotalServicios);

        return dto;
    }
}