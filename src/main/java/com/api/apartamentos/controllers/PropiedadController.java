package com.api.apartamentos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.apartamentos.models.PropiedadModel;
import com.api.apartamentos.services.PropiedadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/propiedades")
public class PropiedadController {
    
    private final PropiedadService propiedadService;

    public PropiedadController(PropiedadService propiedadService) {
        this.propiedadService = propiedadService;
    }

    // Aquí irán los métodos para manejar las solicitudes HTTP relacionadas con las propiedades
    // Obtener todas las propiedades, obtener por ID, crear, actualizar, eliminar, etc.
    
    //Recuperar todas la propiedades
    @GetMapping
    public List<PropiedadModel> getAllPropiedades() {
        return propiedadService.getAllPropiedades();
    }

    //Propiedad por ID
    //... Similar a ClienteController
    @GetMapping("/{id}")
    public ResponseEntity<PropiedadModel> getPropiedadesById(@PathVariable Long id) {
        Optional<PropiedadModel> propiedad = propiedadService.getPropiedadById(id);
        return propiedad.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear nueva propiedad
    @PostMapping
    public PropiedadModel createPropiedad(@Valid @RequestBody PropiedadModel propiedad) {
        return propiedadService.savePropiedad(propiedad);
    }

    // Actualizar propiedad existente
    @PutMapping("/{id}")
    public ResponseEntity<PropiedadModel> updatePropiedad(@PathVariable Long id, @Valid @RequestBody PropiedadModel propiedadDetails) {
        Optional<PropiedadModel> propiedadOptional = propiedadService.getPropiedadById(id);
        if (propiedadOptional.isPresent()) {
            PropiedadModel propiedadToUpdate = propiedadOptional.get();
            propiedadToUpdate.setDireccion(propiedadDetails.getDireccion());
            propiedadToUpdate.setTipo(propiedadDetails.getTipo());
            propiedadToUpdate.setTitulo(propiedadDetails.getTitulo());
            propiedadToUpdate.setDescripcion(propiedadDetails.getDescripcion());
            propiedadToUpdate.setCiudad(propiedadDetails.getCiudad());
            propiedadToUpdate.setEstadoFederativo(propiedadDetails.getEstadoFederativo());
            propiedadToUpdate.setCodigoPostal(propiedadDetails.getCodigoPostal());
            propiedadToUpdate.setPais(propiedadDetails.getPais());
            propiedadToUpdate.setLatitud(propiedadDetails.getLatitud());
            propiedadToUpdate.setLongitud(propiedadDetails.getLongitud());
            propiedadToUpdate.setPrecioNoche(propiedadDetails.getPrecioNoche());
            propiedadToUpdate.setCapacidad(propiedadDetails.getCapacidad());
            propiedadToUpdate.setNumHabitaciones(propiedadDetails.getNumHabitaciones());
            propiedadToUpdate.setNumBanos(propiedadDetails.getNumBanos());
            propiedadToUpdate.setMetrosCuadrados(propiedadDetails.getMetrosCuadrados());
            propiedadToUpdate.setComodidades(propiedadDetails.getComodidades());
            propiedadToUpdate.setReglas(propiedadDetails.getReglas());
            propiedadToUpdate.setEstado(propiedadDetails.getEstado());
            propiedadToUpdate.setPropietario(propiedadDetails.getPropietario());

            PropiedadModel updatedPropiedad = propiedadService.savePropiedad(propiedadToUpdate);
            return ResponseEntity.ok(updatedPropiedad);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar una propiedad
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePropiedad(@PathVariable Long id) {
        Optional<PropiedadModel> propiedad = propiedadService.getPropiedadById(id);
        if (propiedad.isPresent()) {
            propiedadService.deletePropiedad(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    
}
