package com.api.apartamentos.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.api.apartamentos.models.NotificacionModel;
import com.api.apartamentos.repositories.INotificacionRepository;

@Service
public class NotificacionService extends BaseCrudService<NotificacionModel> {
    private final INotificacionRepository notificacionRepository;
    public NotificacionService(INotificacionRepository repository) { super(repository); notificacionRepository = repository; }
    public List<NotificacionModel> findByDestinatario(Long id) { return notificacionRepository.findByDestinatario_Id(id); }
}
