package com.api.apartamentos.services;
import java.math.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.api.apartamentos.models.*;
import com.api.apartamentos.models.PagosModel.EstadoPago;
import com.api.apartamentos.repositories.*;
@Service
@Transactional(readOnly = true)
public class ReporteService {
 private final IReservaRepository reservas;
 private final IPagosRepository pagos;
 private final IUnidadRentableRepository unidades;
 private final IClienteRepository clientes;
 public ReporteService(IReservaRepository r, IPagosRepository p, IUnidadRentableRepository u, IClienteRepository c) { reservas=r; pagos=p; unidades=u; clientes=c; }
 public record Cuenta(Long clienteId, String cliente, BigDecimal cargos, BigDecimal pagado, BigDecimal saldo, BigDecimal vencido) {}
 public record Ingresos(LocalDate desde, LocalDate hasta, BigDecimal total) {}
 public record Ocupacion(LocalDate desde, LocalDate hasta, long nochesOcupadas, long nochesTotales, BigDecimal porcentaje) {}
 private void validar(LocalDate d, LocalDate h) { if(d==null || h==null || !h.isAfter(d)) throw new IllegalArgumentException("El fin debe ser posterior al inicio"); }
 public List<Cuenta> cuentas(LocalDate corte) {
  if(corte==null) throw new IllegalArgumentException("La fecha de corte es obligatoria");
  var ps=pagos.findAll(); Map<Long,Cuenta> result=new LinkedHashMap<>();
  for(var r:reservas.findAll()) {
   if(Set.of("CANCELADA","RECHAZADA").contains(r.getEstado())) continue;
   var cargos=ps.stream().filter(p->Objects.equals(p.getReserva().getIdReservacion(),r.getIdReservacion())).toList();
   BigDecimal abonado=cargos.stream().filter(p->p.getEstado()==EstadoPago.PAGADO && p.getFechaPago()!=null && !p.getFechaPago().isAfter(corte)).map(PagosModel::getImporte).reduce(BigDecimal.ZERO,BigDecimal::add);
   BigDecimal saldo=r.getImporteTotal().subtract(abonado).max(BigDecimal.ZERO);
   var vigentes=cargos.stream().filter(p->p.getEstado()!=EstadoPago.RECHAZADO && p.getEstado()!=EstadoPago.REEMBOLSADO).toList();
   BigDecimal programado=vigentes.stream().map(PagosModel::getImporte).reduce(BigDecimal.ZERO,BigDecimal::add);
   BigDecimal vencido=vigentes.stream().filter(p->(p.getEstado()!=EstadoPago.PAGADO || p.getFechaPago()==null || p.getFechaPago().isAfter(corte)) && p.getFechaVencimiento().isBefore(corte)).map(PagosModel::getImporte).reduce(BigDecimal.ZERO,BigDecimal::add);
   if(r.getFechaEntrada().toLocalDate().isBefore(corte)) vencido=vencido.add(r.getImporteTotal().subtract(programado).max(BigDecimal.ZERO));
   vencido=vencido.min(saldo);
   Long id=r.getCliente().getId();
   Cuenta prev=result.getOrDefault(id,new Cuenta(id,r.getCliente().getNombres()+" "+r.getCliente().getApellidos(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO));
   result.put(id,new Cuenta(id,prev.cliente(),prev.cargos().add(r.getImporteTotal()),prev.pagado().add(abonado),prev.saldo().add(saldo),prev.vencido().add(vencido)));
  }
  return List.copyOf(result.values());
 }
 public Cuenta estadoCuenta(Long id,LocalDate corte) {
  var c=clientes.findById(id).orElseThrow(()->new IllegalArgumentException("El cliente no existe"));
  return cuentas(corte).stream().filter(x->x.clienteId().equals(id)).findFirst().orElse(new Cuenta(id,c.getNombres()+" "+c.getApellidos(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO));
 }
 public List<Cuenta> morosidad(LocalDate corte) {return cuentas(corte).stream().filter(c->c.vencido().signum()>0).toList();}
 public Ingresos ingresos(LocalDate desde,LocalDate hasta) {
  validar(desde,hasta);
  return new Ingresos(desde,hasta,pagos.findAll().stream().filter(p->p.getEstado()==EstadoPago.PAGADO && p.getFechaPago()!=null && !p.getFechaPago().isBefore(desde) && p.getFechaPago().isBefore(hasta)).map(PagosModel::getImporte).reduce(BigDecimal.ZERO,BigDecimal::add));
 }
 public Ocupacion ocupacion(LocalDate desde,LocalDate hasta) {
  validar(desde,hasta);long ocupadas=0;var rs=reservas.findAll();var us=unidades.findAll();
  for(var u:us) {
   Set<LocalDate> noches=new HashSet<>();
   for(var r:rs) {
    if(!Objects.equals(r.getUnidad().getIdUnidad(),u.getIdUnidad()) || !Set.of("CONFIRMADA","CHECKIN","CHECKOUT").contains(r.getEstado())) continue;
    LocalDate inicio=r.getFechaEntrada().toLocalDate().isBefore(desde)?desde:r.getFechaEntrada().toLocalDate();
    LocalDate fin=r.getFechaSalida().toLocalDate().isAfter(hasta)?hasta:r.getFechaSalida().toLocalDate();
    for(LocalDate d=inicio;d.isBefore(fin);d=d.plusDays(1)) noches.add(d);
   }
   ocupadas+=noches.size();
  }
  long total=ChronoUnit.DAYS.between(desde,hasta)*us.size();
  return new Ocupacion(desde,hasta,ocupadas,total,total==0?BigDecimal.ZERO:BigDecimal.valueOf(ocupadas).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total),2,RoundingMode.HALF_UP));
 }
}
