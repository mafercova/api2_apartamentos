package com.api.apartamentos.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "propiedad_imagenes")
public class PropiedadImagenModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_imagen") private Long idImagen;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "id_propiedad", nullable = false)
    @JsonIgnoreProperties({"propietario", "hibernateLazyInitializer", "handler"}) private PropiedadModel propiedad;
    @Column(nullable = false, length = 500) private String url;
    @Column(length = 300) private String descripcion;
    public Long getIdImagen() { return idImagen; } public void setIdImagen(Long value) { idImagen = value; }
    public PropiedadModel getPropiedad() { return propiedad; } public void setPropiedad(PropiedadModel value) { propiedad = value; }
    public String getUrl() { return url; } public void setUrl(String value) { url = value; }
    public String getDescripcion() { return descripcion; } public void setDescripcion(String value) { descripcion = value; }
}
