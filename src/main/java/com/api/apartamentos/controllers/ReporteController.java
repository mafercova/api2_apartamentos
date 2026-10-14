package com.api.apartamentos.controllers;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.api.apartamentos.services.ReporteService;
@Controller
public class ReporteController {
 private final ReporteService service;
 public ReporteController(ReporteService service){this.service=service;}
 @GetMapping("/api/reportes/ocupacion") @ResponseBody
 public ReporteService.Ocupacion ocupacion(@RequestParam LocalDate desde,@RequestParam LocalDate hasta){return service.ocupacion(desde,hasta);}
 @GetMapping("/api/reportes/ingresos") @ResponseBody
 public ReporteService.Ingresos ingresos(@RequestParam LocalDate desde,@RequestParam LocalDate hasta){return service.ingresos(desde,hasta);}
 @GetMapping("/api/reportes/morosidad") @ResponseBody
 public java.util.List<ReporteService.Cuenta> morosidad(@RequestParam(required=false) LocalDate corte){return service.morosidad(corte==null?LocalDate.now():corte);}
 @GetMapping("/api/clientes/{id}/estado-cuenta") @ResponseBody
 public ReporteService.Cuenta cuenta(@PathVariable Long id,@RequestParam(required=false) LocalDate corte){return service.estadoCuenta(id,corte==null?LocalDate.now():corte);}
 @GetMapping("/reportes")
 public String reportes(@RequestParam(required=false) LocalDate desde,@RequestParam(required=false) LocalDate hasta,Model model){
  if(desde==null) desde=LocalDate.now().withDayOfMonth(1);if(hasta==null) hasta=desde.plusMonths(1);
  model.addAttribute("desde",desde);model.addAttribute("hasta",hasta);
  try{model.addAttribute("ocupacion",service.ocupacion(desde,hasta));model.addAttribute("ingresos",service.ingresos(desde,hasta));model.addAttribute("cuentas",service.cuentas(LocalDate.now()));}
  catch(IllegalArgumentException ex){model.addAttribute("error",ex.getMessage());}
  return "reportes";
 }
}
