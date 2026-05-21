package com.example.demo.entities;

import com.example.demo.dtos.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // componentModel = "spring" para que Spring lo gestione como un bean
public interface UserMapper {

    UserMapper GET_MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "username", target = "email") // Mapea el campo 'username' de UserEntity a 'email' en UserResponseDTO
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "token", ignore = true)
    UserResponseDTO toDto(UserEntity userEntity);

    // Puedes añadir más métodos de mapeo si los necesitas, por ejemplo, de DTO a Entidad
}