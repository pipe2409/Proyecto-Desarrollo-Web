package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private int precio;

    @Column(length = 10000)
    private String imagenUrl;

    @Column(nullable = false)
    private int capacidad;

    @Column(length = 100, nullable = false)
    private PrecioTipo precioTipo;

    @Column(length = 100, nullable = false)
    private String horario;

    // Features normalizadas: una fila por caracteristica, FK al servicio.
    // orphanRemoval + cascade=ALL hace que reemplazar la lista borre las viejas
    // y persista las nuevas en una sola operacion del setter (ver setFeatures).
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CaracteristicaServicio> caracteristicas = new ArrayList<>();

    // Front intercambia las features como List<String>; estos helpers la traducen
    // a la tabla normalizada sin que el front tenga que conocer la entidad join.
    @JsonProperty("features")
    public List<String> getFeatures() {
        if (caracteristicas == null) return List.of();
        return caracteristicas.stream()
                .map(CaracteristicaServicio::getTexto)
                .collect(Collectors.toList());
    }

    @JsonProperty("features")
    public void setFeatures(List<String> features) {
        if (caracteristicas == null) caracteristicas = new ArrayList<>();
        caracteristicas.clear();
        if (features == null) return;
        for (String texto : features) {
            if (texto == null || texto.isBlank()) continue;
            CaracteristicaServicio c = new CaracteristicaServicio();
            c.setTexto(texto.trim());
            c.setServicio(this);
            caracteristicas.add(c);
        }
    }
}