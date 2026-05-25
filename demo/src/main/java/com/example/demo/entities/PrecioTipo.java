package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Modalidad de cobro de un servicio. Antes era un String libre lo que permitia
 * variantes como "Por sesion" y "Por sesión" en la misma tabla. Cada valor
 * tiene un displayName que es lo que se guarda en BD y lo que viaja en JSON
 * (@JsonValue / @JsonCreator) para mantener compatibilidad con el front.
 */
public enum PrecioTipo {
    POR_PERSONA("Por persona"),
    POR_NOCHE("Por noche"),
    POR_DIA("Por día"),
    POR_HORA("Por hora"),
    POR_PEDIDO("Por pedido"),
    POR_SESION("Por sesión"),
    POR_VIAJE("Por viaje"),
    POR_ENTREGA("Por entrega"),
    A_LA_CARTA("A la carta");

    private final String displayName;

    PrecioTipo(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Resuelve un PrecioTipo a partir del string guardado en BD o de un
     * nombre de constante (POR_PERSONA). Devuelve null si no matchea.
     * Tolera variantes minimas (case, tilde faltante en "sesion").
     * @JsonCreator hace que Jackson use este metodo al deserializar JSON,
     * asi el front puede mandar "Por persona" en lugar de "POR_PERSONA".
     */
    @JsonCreator
    public static PrecioTipo fromString(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String normalizado = raw.trim();
        // 1) Por nombre de constante (POR_PERSONA)
        try { return PrecioTipo.valueOf(normalizado.toUpperCase().replace(' ', '_')); }
        catch (IllegalArgumentException ignored) {}
        // 2) Por displayName exacto
        for (PrecioTipo p : values()) {
            if (p.displayName.equalsIgnoreCase(normalizado)) return p;
        }
        // 3) Tolerancia: ignorar tildes
        String sinTildes = stripTildes(normalizado).toLowerCase();
        for (PrecioTipo p : values()) {
            if (stripTildes(p.displayName).toLowerCase().equals(sinTildes)) return p;
        }
        return null;
    }

    private static String stripTildes(String s) {
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
