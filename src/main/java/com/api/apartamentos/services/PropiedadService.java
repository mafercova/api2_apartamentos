package com.api.apartamentos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.apartamentos.models.PropiedadModel;
import com.api.apartamentos.repositories.IPropiedadRepository;

@Service
public class PropiedadService extends BaseCrudService<PropiedadModel> {

    public PropiedadService(IPropiedadRepository propiedadRepository) {
        super(propiedadRepository);
    }

    public List<PropiedadModel> getAllPropiedades() {
        return findAll();
    }

    public Optional<PropiedadModel> getPropiedadById(Long id) {
        return findById(id);
    }

    public PropiedadModel savePropiedad(PropiedadModel propiedad) {
        return save(propiedad);
    } 
    
    public void deletePropiedad(Long id) {
        deleteById(id);
    }

    // ACTIVIDAD: Crea un metodo Recuperar propiedades por tipo
}
