package com.api.apartamentos.models;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "notificaciones")
public class NotificacionModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_notificacion") private Long idNotificacion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "id_remitente", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"}) private ClienteModel remitente;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "id_destinatario", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"}) private ClienteModel destinatario;
    @Column(nullable = false, length = 150) private String asunto;
    @Column(nullable = false, length = 1000) private String mensaje;
    @Column(nullable = false, length = 20) private String estado = "NO_LEIDA";
    @Column(name = "fecha_envio", nullable = false) private LocalDateTime fechaEnvio;
    @PrePersist void onCreate() { if (fechaEnvio == null) fechaEnvio = LocalDateTime.now(); }
    public Long getIdNotificacion() { return idNotificacion; } public void setIdNotificacion(Long value) { idNotificacion = value; }
    public ClienteModel getRemitente() { return remitente; } public void setRemitente(ClienteModel value) { remitente = value; }
    public ClienteModel getDestinatario() { return destinatario; } public void setDestinatario(ClienteModel value) { destinatario = value; }
    public String getAsunto() { return asunto; } public void setAsunto(String value) { asunto = value; }
    public String getMensaje() { return mensaje; } public void setMensaje(String value) { mensaje = value; }
    public String getEstado() { return estado; } public void setEstado(String value) { estado = value; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
}
