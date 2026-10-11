package com.api.apartamentos.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.api.apartamentos.models.UnidadRentableModel;

public interface IUnidadRentableRepository extends JpaRepository<UnidadRentableModel, Long> {
    @Override
    @EntityGraph(attributePaths = "propiedad")
    List<UnidadRentableModel> findAll();

    @Override
    @EntityGraph(attributePaths = "propiedad")
    java.util.Optional<UnidadRentableModel> findById(Long id);

    @EntityGraph(attributePaths = "propiedad")
    List<UnidadRentableModel> findByPropiedad_IdPropiedad(Long idPropiedad);
}
