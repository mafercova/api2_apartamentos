package com.api.apartamentos.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.api.apartamentos.models.UnidadRentableModel;
import com.api.apartamentos.repositories.IUnidadRentableRepository;
import com.api.apartamentos.repositories.IPropiedadRepository;
import com.api.apartamentos.models.PropiedadModel;

@Service
public class UnidadRentableService extends BaseCrudService<UnidadRentableModel> {
    private final IUnidadRentableRepository unidadRepository;
    private final IPropiedadRepository propiedadRepository;
    public UnidadRentableService(IUnidadRentableRepository repository, IPropiedadRepository propiedadRepository) {
        super(repository);
        unidadRepository = repository;
        this.propiedadRepository = propiedadRepository;
    }
    public List<UnidadRentableModel> findByPropiedad(Long id) { return unidadRepository.findByPropiedad_IdPropiedad(id); }

    @Override
    public UnidadRentableModel save(UnidadRentableModel unidad) {
        if (unidad.getPropiedad() == null || unidad.getPropiedad().getIdPropiedad() == null) {
            throw new IllegalArgumentException("La propiedad es obligatoria");
        }
        PropiedadModel propiedad = propiedadRepository.findById(unidad.getPropiedad().getIdPropiedad())
                .orElseThrow(() -> new IllegalArgumentException("La propiedad no existe"));
        if (propiedad.getEstado() == PropiedadModel.EstadoPropiedad.MANTENIMIENTO) {
            throw new IllegalArgumentException("No se pueden agregar unidades a una propiedad en mantenimiento");
        }
        unidad.setPropiedad(propiedad);
        if (unidad.getEstado() == null || unidad.getEstado().isBlank()) unidad.setEstado("DISPONIBLE");
        return super.save(unidad);
    }
}
