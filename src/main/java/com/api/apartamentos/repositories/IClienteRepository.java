package com.api.apartamentos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.apartamentos.models.ClienteModel;

public interface IClienteRepository extends JpaRepository<ClienteModel, Long> {
    //aqui no lleva codigo
}
