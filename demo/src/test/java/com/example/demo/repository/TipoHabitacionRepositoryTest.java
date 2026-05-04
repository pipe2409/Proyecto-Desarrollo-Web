package com.example.demo.repository;

import com.example.demo.entities.TipoHabitacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TipoHabitacionRepositoryTest {

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    private TipoHabitacion tipoHabitacion;

    // ─────────────────────────────────────────────────────────────────
    //  Configuración inicial: se ejecuta antes de cada prueba
    // ─────────────────────────────────────────────────────────────────
    @BeforeEach
    void setUp() {
        tipoHabitacion = new TipoHabitacion();
        tipoHabitacion.setNombre("Suite Presidencial");
        tipoHabitacion.setDescripcion("Habitación de lujo con vista al mar");
        tipoHabitacion.setPrecio(500_000);
        tipoHabitacion.setCapacidad(4);
        tipoHabitacion.setCamas("2 camas king size");
        tipoHabitacion.setAmenities("Jacuzzi, minibar, terraza");
        tipoHabitacion.setImagenUrl("https://example.com/suite.jpg");
        tipoHabitacion.setDisponible(true);
    }

    // ─────────────────────────────────────────────────────────────────
    //  CREATE — guardar un nuevo tipo de habitación
    // ─────────────────────────────────────────────────────────────────
    @Test
    void guardarTipoHabitacion_deberiaRetornarTipoHabitacionConId() {
        // Arrange — tipoHabitacion preparado en @BeforeEach

        // Act
        TipoHabitacion guardado = tipoHabitacionRepository.save(tipoHabitacion);

        // Assert
        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Suite Presidencial");
        assertThat(guardado.getPrecio()).isEqualTo(500_000);
        assertThat(guardado.isDisponible()).isTrue();
    }

    // ─────────────────────────────────────────────────────────────────
    //  READ — buscar por ID existente
    // ─────────────────────────────────────────────────────────────────
    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarTipoHabitacion() {
        // Arrange
        TipoHabitacion guardado = tipoHabitacionRepository.save(tipoHabitacion);

        // Act
        Optional<TipoHabitacion> resultado = tipoHabitacionRepository.findById(guardado.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Suite Presidencial");
        assertThat(resultado.get().getCapacidad()).isEqualTo(4);
        assertThat(resultado.get().getCamas()).isEqualTo("2 camas king size");
    }

    // ─────────────────────────────────────────────────────────────────
    //  READ — buscar por ID inexistente
    // ─────────────────────────────────────────────────────────────────
    @Test
    void buscarPorId_cuandoNoExiste_deberiaRetornarOptionalVacio() {
        // Arrange — ID que no existe en la base de datos
        Integer idInexistente = 999;

        // Act
        Optional<TipoHabitacion> resultado = tipoHabitacionRepository.findById(idInexistente);

        // Assert
        assertThat(resultado).isNotPresent();
    }

    // ─────────────────────────────────────────────────────────────────
    //  READ ALL — listar todos los tipos de habitación
    // ─────────────────────────────────────────────────────────────────
    @Test
    void listarTodos_deberiaRetornarListaConTodosLosRegistros() {
        // Arrange
        TipoHabitacion otro = new TipoHabitacion();
        otro.setNombre("Habitación Estándar");
        otro.setDescripcion("Habitación básica con servicios esenciales");
        otro.setPrecio(150_000);
        otro.setCapacidad(2);
        otro.setCamas("1 cama doble");
        otro.setAmenities("TV, WiFi");
        otro.setImagenUrl("https://example.com/estandar.jpg");
        otro.setDisponible(true);

        tipoHabitacionRepository.save(tipoHabitacion);
        tipoHabitacionRepository.save(otro);

        // Act
        List<TipoHabitacion> lista = tipoHabitacionRepository.findAll();

        // Assert
        assertThat(lista).isNotNull();
        assertThat(lista).hasSize(2);
        assertThat(lista)
                .extracting(TipoHabitacion::getNombre)
                .containsExactlyInAnyOrder("Suite Presidencial", "Habitación Estándar");
    }

    // ─────────────────────────────────────────────────────────────────
    //  UPDATE — actualizar campos de un tipo de habitación existente
    // ─────────────────────────────────────────────────────────────────
    @Test
    void actualizarTipoHabitacion_deberiaReflejarCambiosEnBaseDeDatos() {
        // Arrange
        TipoHabitacion guardado = tipoHabitacionRepository.save(tipoHabitacion);

        // Act
        guardado.setNombre("Suite Presidencial Renovada");
        guardado.setPrecio(650_000);
        guardado.setDisponible(false);
        TipoHabitacion actualizado = tipoHabitacionRepository.save(guardado);

        // Assert
        assertThat(actualizado.getId()).isEqualTo(guardado.getId());
        assertThat(actualizado.getNombre()).isEqualTo("Suite Presidencial Renovada");
        assertThat(actualizado.getPrecio()).isEqualTo(650_000);
        assertThat(actualizado.isDisponible()).isFalse();
    }

    // ─────────────────────────────────────────────────────────────────
    //  DELETE — eliminar un tipo de habitación por ID
    // ─────────────────────────────────────────────────────────────────
    @Test
    void eliminarTipoHabitacion_deberiaNoEncontrarseDepues() {
        // Arrange
        TipoHabitacion guardado = tipoHabitacionRepository.save(tipoHabitacion);
        Integer idGuardado = guardado.getId();

        // Act
        tipoHabitacionRepository.deleteById(idGuardado);

        // Assert
        Optional<TipoHabitacion> resultado = tipoHabitacionRepository.findById(idGuardado);
        assertThat(resultado).isNotPresent();
        assertThat(tipoHabitacionRepository.findAll()).isEmpty();
    }
}