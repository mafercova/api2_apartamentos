package com.api.apartamentos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.apartamentos.models.ClienteModel;
import com.api.apartamentos.models.PagosModel;
import com.api.apartamentos.models.ReservaModel;
import com.api.apartamentos.repositories.IPagosRepository;
import com.api.apartamentos.repositories.IReservaRepository;

@ExtendWith(MockitoExtension.class)
class PagosServiceTest {
    @Mock
    private IPagosRepository pagoRepository;
    @Mock
    private IReservaRepository reservaRepository;

    private PagosService service;
    private ReservaModel reserva;

    @BeforeEach
    void setUp() {
        service = new PagosService(pagoRepository, reservaRepository);
        ClienteModel cliente = new ClienteModel();
        cliente.setId(7L);
        reserva = new ReservaModel();
        reserva.setIdReservacion(3L);
        reserva.setCliente(cliente);
        reserva.setEstado("PENDIENTE");
        reserva.setImporteTotal(new BigDecimal("1000.00"));
        when(reservaRepository.findById(3L)).thenReturn(Optional.of(reserva));
    }

    @Test
    void generaImporteYReferenciaDesdeLaReserva() {
        PagosModel pago = nuevoPago();
        when(pagoRepository.totalAplicado(3L, null)).thenReturn(BigDecimal.ZERO);
        when(pagoRepository.save(any(PagosModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PagosModel guardado = service.save(pago);

        assertEquals(new BigDecimal("1000.00"), guardado.getImporte());
        assertNotNull(guardado.getReferenciaPago());
        assertEquals(reserva.getCliente(), guardado.getCliente());
    }

    @Test
    void rechazaUnPagoQueSuperaElSaldoDeLaReserva() {
        PagosModel pago = nuevoPago();
        pago.setImporte(new BigDecimal("200.00"));
        when(pagoRepository.totalAplicado(3L, null)).thenReturn(new BigDecimal("900.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.save(pago));

        assertEquals("El total de pagos supera el importe de la reserva", exception.getMessage());
    }

    @Test
    void rechazaUnaTransicionNoPermitida() {
        PagosModel existente = nuevoPago();
        existente.setIdPago(8L);
        existente.setEstado(PagosModel.EstadoPago.PAGADO);
        existente.setImporte(new BigDecimal("1000.00"));
        existente.setReferenciaPago("PAG-EXISTENTE");
        existente.setCreatedAt(LocalDateTime.now());
        when(pagoRepository.findById(8L)).thenReturn(Optional.of(existente));

        PagosModel actualizacion = new PagosModel();
        actualizacion.setIdPago(8L);
        actualizacion.setEstado(PagosModel.EstadoPago.PENDIENTE);
        actualizacion.setFechaVencimiento(LocalDate.now().plusDays(1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.save(actualizacion));

        assertEquals("Transición de pago no permitida: PAGADO -> PENDIENTE", exception.getMessage());
    }

    private PagosModel nuevoPago() {
        PagosModel pago = new PagosModel();
        ReservaModel referencia = new ReservaModel();
        referencia.setIdReservacion(3L);
        pago.setReserva(referencia);
        pago.setFechaVencimiento(LocalDate.now().plusDays(1));
        return pago;
    }
}
