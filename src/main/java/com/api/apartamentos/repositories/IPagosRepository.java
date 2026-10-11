package com.api.apartamentos.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.api.apartamentos.models.PagosModel;

public interface IPagosRepository extends JpaRepository<PagosModel, Long> {
	@Override
	@EntityGraph(attributePaths = {"reserva", "reserva.unidad", "reserva.unidad.propiedad", "cliente"})
	List<PagosModel> findAll();

	@Override
	@EntityGraph(attributePaths = {"reserva", "reserva.unidad", "reserva.unidad.propiedad", "cliente"})
	Optional<PagosModel> findById(Long id);

	@EntityGraph(attributePaths = {"reserva", "reserva.unidad", "reserva.unidad.propiedad", "cliente"})
	List<PagosModel> findByReserva_IdReservacion(Long idReserva);

	@Query("select coalesce(sum(p.importe), 0) from PagosModel p where p.reserva.idReservacion = :reservaId and p.estado not in ('RECHAZADO', 'REEMBOLSADO') and (:pagoId is null or p.idPago <> :pagoId)")
	BigDecimal totalAplicado(@Param("reservaId") Long reservaId, @Param("pagoId") Long pagoId);
}
