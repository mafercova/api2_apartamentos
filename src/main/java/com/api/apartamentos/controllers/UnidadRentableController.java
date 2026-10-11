package com.api.apartamentos.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.api.apartamentos.models.UnidadRentableModel;
import com.api.apartamentos.services.UnidadRentableService;

@RestController
@RequestMapping("/api/unidades")
public class UnidadRentableController {
    private final UnidadRentableService service;
    public UnidadRentableController(UnidadRentableService service) { this.service = service; }
    @GetMapping public List<UnidadRentableModel> all() { return service.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<UnidadRentableModel> one(@PathVariable Long id) { return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @GetMapping("/propiedad/{idPropiedad}") public List<UnidadRentableModel> byProperty(@PathVariable Long idPropiedad) { return service.findByPropiedad(idPropiedad); }
    @PostMapping public ResponseEntity<UnidadRentableModel> create(@Valid @RequestBody UnidadRentableModel value) { return ResponseEntity.status(201).body(service.save(value)); }
    @PutMapping("/{id}") public ResponseEntity<UnidadRentableModel> update(@PathVariable Long id, @Valid @RequestBody UnidadRentableModel value) { return service.findById(id).map(existing -> { value.setIdUnidad(id); return ResponseEntity.ok(service.save(value)); }).orElseGet(() -> ResponseEntity.notFound().build()); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { if (service.findById(id).isEmpty()) return ResponseEntity.notFound().build(); service.deleteById(id); return ResponseEntity.noContent().build(); }
}
