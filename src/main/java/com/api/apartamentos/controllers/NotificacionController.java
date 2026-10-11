package com.api.apartamentos.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.api.apartamentos.models.NotificacionModel;
import com.api.apartamentos.services.NotificacionService;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    private final NotificacionService service;
    public NotificacionController(NotificacionService service) { this.service = service; }
    @GetMapping public List<NotificacionModel> all() { return service.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<NotificacionModel> one(@PathVariable Long id) { return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @GetMapping("/destinatario/{idCliente}") public List<NotificacionModel> byRecipient(@PathVariable Long idCliente) { return service.findByDestinatario(idCliente); }
    @PostMapping public ResponseEntity<NotificacionModel> create(@Valid @RequestBody NotificacionModel value) { return ResponseEntity.status(201).body(service.save(value)); }
    @PutMapping("/{id}") public ResponseEntity<NotificacionModel> update(@PathVariable Long id, @RequestBody NotificacionModel value) { if (service.findById(id).isEmpty()) return ResponseEntity.notFound().build(); value.setIdNotificacion(id); return ResponseEntity.ok(service.save(value)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { if (service.findById(id).isEmpty()) return ResponseEntity.notFound().build(); service.deleteById(id); return ResponseEntity.noContent().build(); }
}
