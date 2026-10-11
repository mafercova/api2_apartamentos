package com.api.apartamentos.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.api.apartamentos.models.DisponibilidadModel;
import com.api.apartamentos.services.DisponibilidadService;

@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadController {
    private final DisponibilidadService service;
    public DisponibilidadController(DisponibilidadService service) { this.service = service; }
    @GetMapping public List<DisponibilidadModel> all() { return service.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<DisponibilidadModel> one(@PathVariable Long id) { return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @GetMapping("/unidad/{idUnidad}") public List<DisponibilidadModel> byUnit(@PathVariable Long idUnidad) { return service.findByUnidad(idUnidad); }
    @PostMapping public ResponseEntity<DisponibilidadModel> create(@Valid @RequestBody DisponibilidadModel value) { return ResponseEntity.status(201).body(service.save(value)); }
    @PutMapping("/{id}") public ResponseEntity<DisponibilidadModel> update(@PathVariable Long id, @RequestBody DisponibilidadModel value) { if (service.findById(id).isEmpty()) return ResponseEntity.notFound().build(); value.setIdDisponibilidad(id); return ResponseEntity.ok(service.save(value)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { if (service.findById(id).isEmpty()) return ResponseEntity.notFound().build(); service.deleteById(id); return ResponseEntity.noContent().build(); }
}
