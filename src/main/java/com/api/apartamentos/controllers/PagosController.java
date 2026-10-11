package com.api.apartamentos.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.api.apartamentos.models.PagosModel;
import com.api.apartamentos.services.PagosService;

@RestController
@RequestMapping("/api/pagos")
public class PagosController {
    private final PagosService service;
    public PagosController(PagosService service) { this.service = service; }
    @GetMapping public List<PagosModel> all() { return service.findAll(); }
    @GetMapping("/reserva/{idReserva}") public List<PagosModel> byReserva(@PathVariable Long idReserva) { return service.findByReserva(idReserva); }
    @GetMapping("/{id}") public ResponseEntity<PagosModel> one(@PathVariable Long id) { return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @PostMapping public ResponseEntity<PagosModel> create(@Valid @RequestBody PagosModel value) { return ResponseEntity.status(201).body(service.save(value)); }
    @PutMapping("/{id}") public ResponseEntity<PagosModel> update(@PathVariable Long id, @Valid @RequestBody PagosModel value) {
        return service.findById(id).map(existing -> {
            existing.setImporte(value.getImporte());
            existing.setFechaVencimiento(value.getFechaVencimiento());
            existing.setMetodo(value.getMetodo());
            existing.setEstado(value.getEstado());
            return ResponseEntity.ok(service.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/{id}/confirmar") public ResponseEntity<PagosModel> confirm(@PathVariable Long id) { return ResponseEntity.ok(service.confirmar(id)); }
    @PostMapping("/{id}/reembolsar") public ResponseEntity<PagosModel> refund(@PathVariable Long id) { return ResponseEntity.ok(service.reembolsar(id)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { if (service.findById(id).isEmpty()) return ResponseEntity.notFound().build(); service.eliminar(id); return ResponseEntity.noContent().build(); }
}
