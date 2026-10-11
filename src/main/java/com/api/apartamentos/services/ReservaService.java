package com.api.apartamentos.services;

import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.api.apartamentos.models.ReservaModel;
import com.api.apartamentos.repositories.IReservaRepository;

@Service
public class ReservaService extends BaseCrudService<ReservaModel> {
    private final IReservaRepository reservaRepository;
    public ReservaService(IReservaRepository repository) { super(repository); reservaRepository = repository; }

    @Override
    @Transactional
    public ReservaModel save(ReservaModel reserva) {
        if (reserva.getUnidad() == null || reserva.getUnidad().getIdUnidad() == null) throw new IllegalArgumentException("La unidad es obligatoria");
        if (reserva.getFechaEntrada() == null || reserva.getFechaSalida() == null || !reserva.getFechaSalida().isAfter(reserva.getFechaEntrada())) throw new IllegalArgumentException("El rango de fechas no es válido");
        if (!"CANCELADA".equals(reserva.getEstado()) && reservaRepository.existeSolapamiento(reserva.getUnidad().getIdUnidad(), reserva.getIdReservacion(), reserva.getFechaEntrada(), reserva.getFechaSalida())) throw new IllegalArgumentException("La unidad ya está reservada en ese rango");
        if (reserva.getImporteTotal() == null && reserva.getUnidad().getPrecioNoche() != null) {
            long noches = Math.max(1, Duration.between(reserva.getFechaEntrada(), reserva.getFechaSalida()).toDays());
            reserva.setImporteTotal(reserva.getUnidad().getPrecioNoche().multiply(BigDecimal.valueOf(noches)));
        }
        return reservaRepository.save(reserva);
    }
}
