package com.api.apartamentos.services;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import com.api.apartamentos.models.*;
import com.api.apartamentos.repositories.*;
class ReporteServiceTest {
 IReservaRepository reservas=mock(IReservaRepository.class);
 IPagosRepository pagos=mock(IPagosRepository.class);
 IUnidadRentableRepository unidades=mock(IUnidadRentableRepository.class);
 IClienteRepository clientes=mock(IClienteRepository.class);
 ReporteService service=new ReporteService(reservas,pagos,unidades,clientes);
 ReservaModel reserva; UnidadRentableModel unidad;
 @BeforeEach void preparar(){
  ClienteModel c=new ClienteModel();c.setId(1L);c.setNombres("Ana");c.setApellidos("López");
  unidad=new UnidadRentableModel();unidad.setIdUnidad(1L);
  reserva=new ReservaModel();reserva.setIdReservacion(1L);reserva.setCliente(c);reserva.setUnidad(unidad);reserva.setImporteTotal(new BigDecimal("1000"));reserva.setEstado("CONFIRMADA");reserva.setFechaEntrada(LocalDateTime.of(2026,10,1,15,0));reserva.setFechaSalida(LocalDateTime.of(2026,10,4,11,0));
  when(reservas.findAll()).thenReturn(List.of(reserva));when(pagos.findAll()).thenReturn(List.of());
 }
 @Test void deudaSinPagosTambienEsMorosidad(){
  var c=service.morosidad(LocalDate.of(2026,10,5)).getFirst();assertEquals(0,c.vencido().compareTo(new BigDecimal("1000")));
 }
 @Test void abonoReduceSaldoYDeuda(){
  PagosModel p=new PagosModel();p.setReserva(reserva);p.setEstado(PagosModel.EstadoPago.PAGADO);p.setImporte(new BigDecimal("400"));p.setFechaPago(LocalDate.of(2026,10,2));p.setFechaVencimiento(LocalDate.of(2026,10,1));when(pagos.findAll()).thenReturn(List.of(p));
  var c=service.cuentas(LocalDate.of(2026,10,5)).getFirst();assertEquals(0,c.saldo().compareTo(new BigDecimal("600")));assertEquals(0,c.vencido().compareTo(new BigDecimal("600")));
 }
 @Test void ingresosExcluyenPendientesYReembolsos(){
  PagosModel p=new PagosModel();p.setEstado(PagosModel.EstadoPago.REEMBOLSADO);p.setImporte(new BigDecimal("400"));p.setFechaPago(LocalDate.of(2026,10,2));when(pagos.findAll()).thenReturn(List.of(p));
  assertEquals(0,service.ingresos(LocalDate.of(2026,10,1),LocalDate.of(2026,11,1)).total().signum());
 }
 @Test void ocupacionRecortaPeriodoYExcluyeCanceladas(){
  when(unidades.findAll()).thenReturn(List.of(unidad));var o=service.ocupacion(LocalDate.of(2026,10,2),LocalDate.of(2026,10,6));assertEquals(2,o.nochesOcupadas());assertEquals(new BigDecimal("50.00"),o.porcentaje());
  reserva.setEstado("CANCELADA");assertEquals(0,service.ocupacion(LocalDate.of(2026,10,2),LocalDate.of(2026,10,6)).nochesOcupadas());assertTrue(service.morosidad(LocalDate.of(2026,10,5)).isEmpty());
 }
 @Test void rechazaRangoInvertido(){assertThrows(IllegalArgumentException.class,()->service.ingresos(LocalDate.of(2026,10,2),LocalDate.of(2026,10,1)));}
}
