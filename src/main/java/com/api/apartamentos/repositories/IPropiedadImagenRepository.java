package com.api.apartamentos.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.apartamentos.models.PropiedadImagenModel;

public interface IPropiedadImagenRepository extends JpaRepository<PropiedadImagenModel, Long> {
    List<PropiedadImagenModel> findByPropiedad_IdPropiedad(Long idPropiedad);
}
