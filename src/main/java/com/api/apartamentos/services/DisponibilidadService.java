package com.api.apartamentos.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.api.apartamentos.models.DisponibilidadModel;
import com.api.apartamentos.repositories.IDisponibilidadRepository;

@Service
public class DisponibilidadService extends BaseCrudService<DisponibilidadModel> {
    private final IDisponibilidadRepository disponibilidadRepository;
    public DisponibilidadService(IDisponibilidadRepository repository) { super(repository); disponibilidadRepository = repository; }
    @Override
    public DisponibilidadModel save(DisponibilidadModel value) {
        if (value.getUnidad() == null || value.getUnidad().getIdUnidad() == null || value.getDisponible() == null)
            throw new IllegalArgumentException("Unidad y disponibilidad son obligatorias");
        if (value.getFechaInicio() == null || value.getFechaFin() == null || !value.getFechaFin().isAfter(value.getFechaInicio()))
            throw new IllegalArgumentException("El rango de disponibilidad no es válido");
        if (value.getPrecioEspecial() != null && value.getPrecioEspecial().signum() <= 0)
            throw new IllegalArgumentException("El precio especial debe ser positivo");
        return super.save(value);
    }
    public List<DisponibilidadModel> findByUnidad(Long id) { return disponibilidadRepository.findByUnidad_IdUnidad(id); }
}
