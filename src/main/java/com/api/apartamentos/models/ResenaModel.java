package com.api.apartamentos.models;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "resenas")
public class ResenaModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_resena") private Long idResena;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "id_reservacion", nullable = false)
    @JsonIgnoreProperties({"unidad", "cliente", "hibernateLazyInitializer", "handler"}) private ReservaModel reserva;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"}) private ClienteModel cliente;
    @Column(name = "calificacion_general", nullable = false) private int calificacionGeneral;
    @Column(name = "calificacion_comunicacion", nullable = false) private int calificacionComunicacion;
    @Column(length = 1000) private String comentario;
    @Column(name = "fecha_resena", nullable = false) private LocalDateTime fechaResena;
    @PrePersist void onCreate() { if (fechaResena == null) fechaResena = LocalDateTime.now(); }
    public Long getIdResena() { return idResena; } public void setIdResena(Long value) { idResena = value; }
    public ReservaModel getReserva() { return reserva; } public void setReserva(ReservaModel value) { reserva = value; }
    public ClienteModel getCliente() { return cliente; } public void setCliente(ClienteModel value) { cliente = value; }
    public int getCalificacionGeneral() { return calificacionGeneral; } public void setCalificacionGeneral(int value) { calificacionGeneral = value; }
    public int getCalificacionComunicacion() { return calificacionComunicacion; } public void setCalificacionComunicacion(int value) { calificacionComunicacion = value; }
    public String getComentario() { return comentario; } public void setComentario(String value) { comentario = value; }
    public LocalDateTime getFechaResena() { return fechaResena; }
}
