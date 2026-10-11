package com.api.apartamentos.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.api.apartamentos.models.DisponibilidadModel;
import com.api.apartamentos.repositories.IDisponibilidadRepository;

@Service
public class DisponibilidadService extends BaseCrudService<DisponibilidadModel> {
    private final IDisponibilidadRepository disponibilidadRepository;
    public DisponibilidadService(IDisponibilidadRepository repository) { super(repository); disponibilidadRepository = repository; }
    public List<DisponibilidadModel> findByUnidad(Long id) { return disponibilidadRepository.findByUnidad_IdUnidad(id); }
}
