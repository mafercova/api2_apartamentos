package com.api.apartamentos.services;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import com.api.apartamentos.models.*;
import com.api.apartamentos.repositories.*;
class ReservaServiceTest {
 IReservaRepository reservas=mock(IReservaRepository.class);
 IUnidadRentableRepository unidades=mock(IUnidadRentableRepository.class);
 IClienteRepository clientes=mock(IClienteRepository.class);
 IDisponibilidadRepository disponibilidad=mock(IDisponibilidadRepository.class);
 ReservaService service=new ReservaService(reservas,unidades,clientes,disponibilidad);
 ReservaModel r;
 @BeforeEach void preparar(){
  PropiedadModel p=new PropiedadModel();UnidadRentableModel u=new UnidadRentableModel();u.setIdUnidad(1L);u.setPropiedad(p);u.setPrecioNoche(new BigDecimal("500"));
  ClienteModel c=new ClienteModel();c.setId(1L);
  r=new ReservaModel();UnidadRentableModel referencia=new UnidadRentableModel();referencia.setIdUnidad(1L);r.setUnidad(referencia);r.setCliente(c);r.setFechaEntrada(LocalDateTime.of(2026,10,1,15,0));r.setFechaSalida(LocalDateTime.of(2026,10,3,11,0));
  when(unidades.bloquearPorId(1L)).thenReturn(Optional.of(u));when(clientes.findById(1L)).thenReturn(Optional.of(c));when(disponibilidad.findByUnidad_IdUnidad(1L)).thenReturn(List.of());when(reservas.save(any())).thenAnswer(i->i.getArgument(0));
 }
 @Test void calculaPrecioPersistidoPorNochesDeCalendario(){assertEquals(new BigDecimal("1000"),service.save(r).getImporteTotal());}
 @Test void rechazaBloqueo(){DisponibilidadModel d=new DisponibilidadModel();d.setDisponible(false);d.setFechaInicio(r.getFechaEntrada());d.setFechaFin(r.getFechaSalida());when(disponibilidad.findByUnidad_IdUnidad(1L)).thenReturn(List.of(d));assertThrows(IllegalArgumentException.class,()->service.save(r));}
 @Test void rechazaSolapamiento(){when(reservas.existeSolapamiento(1L,null,r.getFechaEntrada(),r.getFechaSalida())).thenReturn(true);assertThrows(IllegalArgumentException.class,()->service.save(r));}
}
