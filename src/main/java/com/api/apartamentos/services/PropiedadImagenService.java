package com.api.apartamentos.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.api.apartamentos.models.PropiedadImagenModel;
import com.api.apartamentos.repositories.IPropiedadImagenRepository;

@Service
public class PropiedadImagenService extends BaseCrudService<PropiedadImagenModel> {
    private final IPropiedadImagenRepository imagenRepository;
    public PropiedadImagenService(IPropiedadImagenRepository repository) { super(repository); imagenRepository = repository; }
    public List<PropiedadImagenModel> findByPropiedad(Long id) { return imagenRepository.findByPropiedad_IdPropiedad(id); }
}
