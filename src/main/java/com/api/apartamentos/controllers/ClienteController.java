package com.api.apartamentos.controllers;

import com.api.apartamentos.models.ClienteModel;
import com.api.apartamentos.services.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Obtener todos los clientes
    @GetMapping
    public List<ClienteModel> getAllClientes() {
        return clienteService.getAllClientes();
    }

    // Obtener un cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteModel> getClienteById(@PathVariable Long id) {
        Optional <ClienteModel> cliente = clienteService.getClienteById(id);
        return cliente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());        
    }

    // Crear un nuevo cliente
    @PostMapping
    public ClienteModel createCliente(@Valid @RequestBody ClienteModel cliente) {
        return clienteService.saveCliente(cliente);
    }

    // Actualizar un cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteModel> updateCliente(@PathVariable Long id, @Valid @RequestBody ClienteModel clienteDetails) {
        Optional<ClienteModel> clienteOptional = clienteService.getClienteById(id);
        if (clienteOptional.isPresent()) {
            ClienteModel clienteToUpdate = clienteOptional.get();
            clienteToUpdate.setNombres(clienteDetails.getNombres());
            clienteToUpdate.setApellidos(clienteDetails.getApellidos());
            clienteToUpdate.setEmail(clienteDetails.getEmail());
            clienteToUpdate.setTipo(clienteDetails.getTipo());
            clienteToUpdate.setTelefono(clienteDetails.getTelefono());
            clienteToUpdate.setFechaNacimiento(clienteDetails.getFechaNacimiento());
            clienteToUpdate.setIne(clienteDetails.getIne());
            clienteToUpdate.setDireccion(clienteDetails.getDireccion());
            clienteToUpdate.setEstatus(clienteDetails.getEstatus());
            if (clienteDetails.getPassword() != null && !clienteDetails.getPassword().isBlank()) clienteToUpdate.setPassword(clienteDetails.getPassword());

            ClienteModel updatedCliente = clienteService.saveCliente(clienteToUpdate);
            return ResponseEntity.ok(updatedCliente);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    // Eliminar un cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        Optional<ClienteModel> cliente = clienteService.getClienteById(id);
        if (cliente.isPresent()) {
            clienteService.deleteCliente(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}