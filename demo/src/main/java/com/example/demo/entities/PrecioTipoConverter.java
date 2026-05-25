package com.example.demo.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Mapea PrecioTipo <-> String guardado en BD. Necesario porque ya hay filas
 * existentes con valores como "Por persona" (con espacios y tildes) que no
 * coinciden con los nombres de constantes Java. autoApply=true hace que JPA
 * use este converter automaticamente para cualquier campo de tipo PrecioTipo.
 */
@Converter(autoApply = true)
public class PrecioTipoConverter implements AttributeConverter<PrecioTipo, String> {

    @Override
    public String convertToDatabaseColumn(PrecioTipo attribute) {
        return attribute == null ? null : attribute.getDisplayName();
    }

    @Override
    public PrecioTipo convertToEntityAttribute(String dbData) {
        return PrecioTipo.fromString(dbData);
    }
}
