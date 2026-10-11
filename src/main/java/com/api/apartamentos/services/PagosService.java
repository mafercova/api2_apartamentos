package com.api.apartamentos.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.apartamentos.models.PagosModel;
import com.api.apartamentos.models.PagosModel.EstadoPago;
import com.api.apartamentos.models.ReservaModel;
import com.api.apartamentos.repositories.IPagosRepository;
import com.api.apartamentos.repositories.IReservaRepository;

@Service
public class PagosService extends BaseCrudService<PagosModel> {
    private final IPagosRepository pagoRepository;
    private final IReservaRepository reservaRepository;

    public PagosService(IPagosRepository pagoRepository, IReservaRepository reservaRepository) {
        super(pagoRepository);
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<PagosModel> findByReserva(Long idReserva) {
        return pagoRepository.findByReserva_IdReservacion(idReserva);
    }

    @Override
    @Transactional
    public PagosModel save(PagosModel pago) {
        PagosModel anterior = pago.getIdPago() == null ? null : pagoRepository.findById(pago.getIdPago())
                .orElseThrow(() -> new IllegalArgumentException("El pago no existe"));
        ReservaModel reserva = resolverReserva(pago, anterior);
        validarReserva(reserva);
        validarTransicion(anterior, pago.getEstado());

        pago.setReserva(reserva);
        pago.setCliente(reserva.getCliente());
        if (pago.getReferenciaPago() == null || pago.getReferenciaPago().isBlank()) {
            pago.setReferenciaPago(generarReferencia());
        }
        if (anterior != null && pago.getCreatedAt() == null) {
            pago.setReferenciaPago(anterior.getReferenciaPago());
            pago.setCreatedAt(anterior.getCreatedAt());
        }
        if (pago.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria");
        }
        if (pago.getImporte() == null) {
            pago.setImporte(reserva.getImporteTotal());
        }
        if (pago.getImporte() == null || pago.getImporte().signum() <= 0) {
            throw new IllegalArgumentException("El importe debe ser mayor que cero");
        }
        if (pago.getEstado() == null) pago.setEstado(EstadoPago.PENDIENTE);
        if (requiereAplicacion(pago.getEstado())) {
            BigDecimal totalAplicado = pagoRepository.totalAplicado(reserva.getIdReservacion(), pago.getIdPago());
            BigDecimal nuevoTotal = totalAplicado.add(pago.getImporte());
            if (reserva.getImporteTotal() == null || nuevoTotal.compareTo(reserva.getImporteTotal()) > 0) {
                throw new IllegalArgumentException("El total de pagos supera el importe de la reserva");
            }
        }
        if (pago.getEstado() == EstadoPago.PAGADO && pago.getFechaPago() == null) {
            pago.setFechaPago(LocalDate.now());
        }
        if (pago.getEstado() != EstadoPago.PAGADO && pago.getEstado() != EstadoPago.REEMBOLSADO) {
            pago.setFechaPago(null);
        }
        return pagoRepository.save(pago);
    }

    @Transactional
    public PagosModel confirmar(Long id) {
        PagosModel pago = obtener(id);
        if (pago.getEstado() != EstadoPago.PENDIENTE && pago.getEstado() != EstadoPago.EN_PROCESO) {
            throw new IllegalArgumentException("Solo se puede confirmar un pago pendiente o en proceso");
        }
        pago.setEstado(EstadoPago.PAGADO);
        pago.setFechaPago(LocalDate.now());
        return save(pago);
    }

    @Transactional
    public PagosModel reembolsar(Long id) {
        PagosModel pago = obtener(id);
        if (pago.getEstado() != EstadoPago.PAGADO) {
            throw new IllegalArgumentException("Solo se puede reembolsar un pago confirmado");
        }
        pago.setEstado(EstadoPago.REEMBOLSADO);
        return save(pago);
    }

    @Transactional
    public void eliminar(Long id) {
        PagosModel pago = obtener(id);
        if (pago.getEstado() == EstadoPago.PAGADO || pago.getEstado() == EstadoPago.REEMBOLSADO) {
            throw new IllegalArgumentException("Los pagos confirmados no se pueden eliminar");
        }
        pagoRepository.deleteById(id);
    }

    private PagosModel obtener(Long id) {
        return pagoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("El pago no existe"));
    }

    private ReservaModel resolverReserva(PagosModel pago, PagosModel anterior) {
        Long idReserva = pago.getReserva() == null ? null : pago.getReserva().getIdReservacion();
        if (idReserva == null && anterior != null && anterior.getReserva() != null) idReserva = anterior.getReserva().getIdReservacion();
        if (idReserva == null) throw new IllegalArgumentException("La reserva es obligatoria");
        return reservaRepository.findById(idReserva).orElseThrow(() -> new IllegalArgumentException("La reserva no existe"));
    }

    private void validarReserva(ReservaModel reserva) {
        if (reserva.getEstado() == null || "CANCELADA".equals(reserva.getEstado())) {
            throw new IllegalArgumentException("No se puede registrar un pago para una reserva cancelada");
        }
        if (reserva.getCliente() == null || reserva.getCliente().getId() == null) {
            throw new IllegalArgumentException("La reserva no tiene un cliente válido");
        }
    }

    private void validarTransicion(PagosModel anterior, EstadoPago nuevoEstado) {
        if (nuevoEstado == null) return;
        if (anterior == null) {
            if (nuevoEstado != EstadoPago.PENDIENTE && nuevoEstado != EstadoPago.EN_PROCESO) {
                throw new IllegalArgumentException("Un pago nuevo debe iniciar como PENDIENTE o EN_PROCESO");
            }
            return;
        }
        EstadoPago anteriorEstado = anterior.getEstado();
        if (anteriorEstado == nuevoEstado) return;
        boolean valida = (anteriorEstado == EstadoPago.PENDIENTE && (nuevoEstado == EstadoPago.EN_PROCESO || nuevoEstado == EstadoPago.RECHAZADO || nuevoEstado == EstadoPago.VENCIDO))
                || (anteriorEstado == EstadoPago.EN_PROCESO && (nuevoEstado == EstadoPago.RECHAZADO || nuevoEstado == EstadoPago.VENCIDO))
                || (anteriorEstado == EstadoPago.PAGADO && nuevoEstado == EstadoPago.REEMBOLSADO);
        if (!valida) throw new IllegalArgumentException("Transición de pago no permitida: " + anteriorEstado + " -> " + nuevoEstado);
    }

    private boolean requiereAplicacion(EstadoPago estado) {
        return estado == EstadoPago.PENDIENTE || estado == EstadoPago.EN_PROCESO || estado == EstadoPago.PAGADO;
    }

    private String generarReferencia() {
        return "PAG-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

}
