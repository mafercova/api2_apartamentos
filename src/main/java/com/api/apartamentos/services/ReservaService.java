package com.api.apartamentos.services;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.api.apartamentos.models.*;
import com.api.apartamentos.repositories.*;

@Service
public class ReservaService extends BaseCrudService<ReservaModel> {
    private final IReservaRepository reservas;
    private final IUnidadRentableRepository unidades;
    private final IClienteRepository clientes;
    private final IDisponibilidadRepository disponibilidades;
    public ReservaService(IReservaRepository reservas, IUnidadRentableRepository unidades,
            IClienteRepository clientes, IDisponibilidadRepository disponibilidades) {
        super(reservas); this.reservas = reservas; this.unidades = unidades;
        this.clientes = clientes; this.disponibilidades = disponibilidades;
    }
    @Override
    @Transactional
    public ReservaModel save(ReservaModel reserva) {
        if (reserva.getUnidad() == null || reserva.getUnidad().getIdUnidad() == null)
            throw new IllegalArgumentException("La unidad es obligatoria");
        var unidad = unidades.bloquearPorId(reserva.getUnidad().getIdUnidad())
                .orElseThrow(() -> new IllegalArgumentException("La unidad no existe"));
        if (reserva.getCliente() == null || reserva.getCliente().getId() == null)
            throw new IllegalArgumentException("El cliente es obligatorio");
        var cliente = clientes.findById(reserva.getCliente().getId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente no existe"));
        if (cliente.getEstatus() != ClienteModel.ClienteStatus.ACTIVO)
            throw new IllegalArgumentException("El cliente debe estar activo");
        if (reserva.getFechaEntrada() == null || reserva.getFechaSalida() == null || !reserva.getFechaSalida().isAfter(reserva.getFechaEntrada()))
            throw new IllegalArgumentException("El rango de fechas no es válido");
        if (!Set.of("PENDIENTE", "CONFIRMADA", "CHECKIN", "CHECKOUT", "CANCELADA", "RECHAZADA").contains(reserva.getEstado() == null ? "" : reserva.getEstado()))
            throw new IllegalArgumentException("Estado de reserva no válido");
        boolean activa = !Set.of("CANCELADA", "RECHAZADA").contains(reserva.getEstado());
        if (activa) {
            if (!"DISPONIBLE".equals(unidad.getEstado()) || unidad.getPropiedad().getEstado() != PropiedadModel.EstadoPropiedad.DISPONIBLE)
                throw new IllegalArgumentException("La unidad o propiedad no está disponible");
            if (reservas.existeSolapamiento(unidad.getIdUnidad(), reserva.getIdReservacion(), reserva.getFechaEntrada(), reserva.getFechaSalida()))
                throw new IllegalArgumentException("La unidad ya está reservada en ese rango");
            boolean bloqueada = disponibilidades.findByUnidad_IdUnidad(unidad.getIdUnidad()).stream().anyMatch(d ->
                    Boolean.FALSE.equals(d.getDisponible()) && d.getFechaInicio().isBefore(reserva.getFechaSalida()) && d.getFechaFin().isAfter(reserva.getFechaEntrada()));
            if (bloqueada) throw new IllegalArgumentException("La unidad tiene un bloqueo de disponibilidad en ese rango");
        }
        reserva.setUnidad(unidad); reserva.setCliente(cliente);
        if (reserva.getImporteTotal() == null) {
            long noches = Math.max(1, ChronoUnit.DAYS.between(reserva.getFechaEntrada().toLocalDate(), reserva.getFechaSalida().toLocalDate()));
            reserva.setImporteTotal(unidad.getPrecioNoche().multiply(BigDecimal.valueOf(noches)));
        }
        if (reserva.getImporteTotal().signum() <= 0) throw new IllegalArgumentException("El importe debe ser positivo");
        return reservas.save(reserva);
    }
}
