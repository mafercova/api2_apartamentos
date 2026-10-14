package com.api.apartamentos.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.api.apartamentos.models.UnidadRentableModel;

public interface IUnidadRentableRepository extends JpaRepository<UnidadRentableModel, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from UnidadRentableModel u where u.idUnidad = :id")
    java.util.Optional<UnidadRentableModel> bloquearPorId(@org.springframework.data.repository.query.Param("id") Long id);

    @Override
    @EntityGraph(attributePaths = "propiedad")
    List<UnidadRentableModel> findAll();

    @Override
    @EntityGraph(attributePaths = "propiedad")
    java.util.Optional<UnidadRentableModel> findById(Long id);

    @EntityGraph(attributePaths = "propiedad")
    List<UnidadRentableModel> findByPropiedad_IdPropiedad(Long idPropiedad);
}
