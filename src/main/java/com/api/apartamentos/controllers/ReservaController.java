package com.api.apartamentos.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.api.apartamentos.models.ReservaModel;
import com.api.apartamentos.services.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService service;
    public ReservaController(ReservaService service) { this.service = service; }
    @GetMapping public List<ReservaModel> all() { return service.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<ReservaModel> one(@PathVariable Long id) { return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @PostMapping public ResponseEntity<ReservaModel> create(@Valid @RequestBody ReservaModel value) { return ResponseEntity.status(201).body(service.save(value)); }
    @PutMapping("/{id}") public ResponseEntity<ReservaModel> update(@PathVariable Long id, @RequestBody ReservaModel value) { if (service.findById(id).isEmpty()) return ResponseEntity.notFound().build(); value.setIdReservacion(id); return ResponseEntity.ok(service.save(value)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { if (service.findById(id).isEmpty()) return ResponseEntity.notFound().build(); service.deleteById(id); return ResponseEntity.noContent().build(); }
}
