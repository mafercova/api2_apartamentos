package com.api.apartamentos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.apartamentos.models.PropiedadModel;
import com.api.apartamentos.models.UnidadRentableModel;
import com.api.apartamentos.repositories.IPropiedadRepository;
import com.api.apartamentos.repositories.IUnidadRentableRepository;

@ExtendWith(MockitoExtension.class)
class UnidadRentableServiceTest {
    @Mock
    private IUnidadRentableRepository unidadRepository;
    @Mock
    private IPropiedadRepository propiedadRepository;

    private UnidadRentableService service;

    @BeforeEach
    void setUp() {
        service = new UnidadRentableService(unidadRepository, propiedadRepository);
    }

    @Test
    void guardaLaUnidadConLaPropiedadPersistida() {
        PropiedadModel propiedad = propiedad(4L, PropiedadModel.EstadoPropiedad.DISPONIBLE);
        UnidadRentableModel unidad = unidadConPropiedadId(4L);
        when(propiedadRepository.findById(4L)).thenReturn(Optional.of(propiedad));
        when(unidadRepository.save(any(UnidadRentableModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UnidadRentableModel resultado = service.save(unidad);

        assertEquals(propiedad, resultado.getPropiedad());
        verify(unidadRepository).save(resultado);
    }

    @Test
    void rechazaUnaPropiedadInexistente() {
        when(propiedadRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.save(unidadConPropiedadId(99L)));

        assertEquals("La propiedad no existe", exception.getMessage());
    }

    @Test
    void rechazaUnaPropiedadEnMantenimiento() {
        when(propiedadRepository.findById(5L)).thenReturn(
                Optional.of(propiedad(5L, PropiedadModel.EstadoPropiedad.MANTENIMIENTO)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.save(unidadConPropiedadId(5L)));

        assertEquals("No se pueden agregar unidades a una propiedad en mantenimiento", exception.getMessage());
    }

    private UnidadRentableModel unidadConPropiedadId(Long idPropiedad) {
        UnidadRentableModel unidad = new UnidadRentableModel();
        PropiedadModel referencia = new PropiedadModel();
        referencia.setIdPropiedad(idPropiedad);
        unidad.setPropiedad(referencia);
        unidad.setNombre("Unidad de prueba");
        unidad.setTipo("APARTAMENTO");
        unidad.setCapacidad(2);
        unidad.setPrecioNoche(new BigDecimal("500.00"));
        return unidad;
    }

    private PropiedadModel propiedad(Long id, PropiedadModel.EstadoPropiedad estado) {
        PropiedadModel propiedad = new PropiedadModel();
        propiedad.setIdPropiedad(id);
        propiedad.setEstado(estado);
        return propiedad;
    }
}
