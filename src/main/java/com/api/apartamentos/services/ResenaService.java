package com.api.apartamentos.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.api.apartamentos.models.ResenaModel;
import com.api.apartamentos.repositories.IResenaRepository;

@Service
public class ResenaService extends BaseCrudService<ResenaModel> {
    private final IResenaRepository resenaRepository;
    public ResenaService(IResenaRepository repository) { super(repository); resenaRepository = repository; }
    public List<ResenaModel> findByReserva(Long id) { return resenaRepository.findByReserva_IdReservacion(id); }
}
