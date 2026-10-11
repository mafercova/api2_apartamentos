package com.api.apartamentos.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "pagos", uniqueConstraints = @UniqueConstraint(name = "uk_pagos_referencia", columnNames = "referencia_pago"))
public class PagosModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reservacion", nullable = false)
    @JsonIgnoreProperties({"unidad", "cliente", "hibernateLazyInitializer", "handler"})
    private ReservaModel reserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private ClienteModel cliente;

    @Positive
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @NotNull
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "referencia_pago", unique = true, nullable = false, length = 100)
    private String referenciaPago;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", nullable = false, length = 30)
    private MetodoPago metodo = MetodoPago.TRANSFERENCIA;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (estado == null) estado = EstadoPago.PENDIENTE;
        if (metodo == null) metodo = MetodoPago.TRANSFERENCIA;
    }

    public enum MetodoPago { TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, EFECTIVO }
    public enum EstadoPago { PENDIENTE, EN_PROCESO, PAGADO, RECHAZADO, VENCIDO, REEMBOLSADO }

    public Long getIdPago() { return idPago; }
    public void setIdPago(Long value) { idPago = value; }
    public ReservaModel getReserva() { return reserva; }
    public void setReserva(ReservaModel value) { reserva = value; }
    public ClienteModel getCliente() { return cliente; }
    public void setCliente(ClienteModel value) { cliente = value; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal value) { importe = value; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate value) { fechaVencimiento = value; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate value) { fechaPago = value; }
    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String value) { referenciaPago = value; }
    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago value) { metodo = value; }
    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago value) { estado = value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime value) { createdAt = value; }
}
