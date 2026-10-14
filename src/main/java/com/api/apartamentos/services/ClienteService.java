package com.api.apartamentos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.apartamentos.repositories.IClienteRepository;
import com.api.apartamentos.models.ClienteModel;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class ClienteService extends BaseCrudService<ClienteModel> {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ClienteService(IClienteRepository clienteRepository) {
        super(clienteRepository);
    }

    public List<ClienteModel> getAllClientes() {
        return findAll();
    }

    public Optional<ClienteModel>  getClienteById(Long id) {
        return findById(id);
    }

    public ClienteModel saveCliente(ClienteModel cliente) {
        if (cliente.getPassword() == null || cliente.getPassword().isBlank()) {
            if (cliente.getId() == null) throw new IllegalArgumentException("La contraseña es obligatoria al crear un cliente");
            ClienteModel anterior = findById(cliente.getId()).orElseThrow(() -> new IllegalArgumentException("El cliente no existe"));
            cliente.setPassword(anterior.getPassword());
        }
        if (cliente.getPassword() != null && !cliente.getPassword().matches("^\\$2[aby]\\$.*")) {
            cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        }
        return save(cliente);
    }

    public void deleteCliente(Long id) {
        deleteById(id);
    }

    
}