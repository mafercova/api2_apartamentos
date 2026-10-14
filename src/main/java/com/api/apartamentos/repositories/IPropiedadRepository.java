package com.api.apartamentos.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.apartamentos.models.PropiedadModel;

public interface IPropiedadRepository extends JpaRepository<PropiedadModel, Long> {
    @Override
    @EntityGraph(attributePaths = "propietario")
    List<PropiedadModel> findAll();

    @Override
    @EntityGraph(attributePaths = "propietario")
    Optional<PropiedadModel> findById(Long id);
}
