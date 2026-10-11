package com.api.apartamentos.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.apartamentos.models.NotificacionModel;

public interface INotificacionRepository extends JpaRepository<NotificacionModel, Long> {
    List<NotificacionModel> findByDestinatario_Id(Long idCliente);
}
