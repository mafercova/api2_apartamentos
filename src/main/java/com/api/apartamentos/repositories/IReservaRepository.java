package com.api.apartamentos.repositories;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.api.apartamentos.models.ReservaModel;

public interface IReservaRepository extends JpaRepository<ReservaModel, Long> {
    @Override
    @EntityGraph(attributePaths = {"unidad", "unidad.propiedad", "cliente"})
    java.util.List<ReservaModel> findAll();

    @Override
    @EntityGraph(attributePaths = {"unidad", "unidad.propiedad", "cliente"})
    java.util.Optional<ReservaModel> findById(Long id);

    @Query("select count(r) > 0 from ReservaModel r where r.unidad.idUnidad = :unidadId and (:reservaId is null or r.idReservacion <> :reservaId) and r.estado not in ('CANCELADA', 'RECHAZADA') and r.fechaEntrada < :salida and r.fechaSalida > :entrada")
    boolean existeSolapamiento(@Param("unidadId") Long unidadId, @Param("reservaId") Long reservaId, @Param("entrada") LocalDateTime entrada, @Param("salida") LocalDateTime salida);
}
