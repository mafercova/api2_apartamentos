package com.api.apartamentos.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.api.apartamentos.models.ClienteModel;
import com.api.apartamentos.repositories.IClienteRepository;
import jakarta.validation.Validation;

class ClienteServiceTest {
    @Test void actualizaClienteSinExigirOtraContrasena() {
        var repo = mock(IClienteRepository.class);
        var service = new ClienteService(repo);
        var anterior = new ClienteModel(); anterior.setId(1L);
        String hash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("original");
        anterior.setPassword(hash);
        when(repo.findById(1L)).thenReturn(Optional.of(anterior));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        var cambio = new ClienteModel(); cambio.setId(1L); cambio.setPassword("");
        assertEquals(hash, service.saveCliente(cambio).getPassword());
    }
    @Test void contrasenaSoloEsObligatoriaAlCrear() {
        var c = new ClienteModel(); c.setNombres("Ana"); c.setApellidos("López"); c.setEmail("ana@example.com");
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertTrue(validator.validate(c).isEmpty());
            assertTrue(validator.validate(c, ClienteModel.Creacion.class).stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }
    }
}
