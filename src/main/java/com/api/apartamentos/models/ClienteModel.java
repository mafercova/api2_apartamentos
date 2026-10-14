package com.api.apartamentos.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "clientes")
public class ClienteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", nullable = false, length = 20)
    private TipoCliente tipo = TipoCliente.CLIENTE;

    @Column(name = "nombre", nullable = false, length = 35)
    @NotBlank
    private String nombres;
    @Column(name = "apellidos", nullable = false, length = 35)
    @NotBlank
    private String apellidos;
    @Column(name = "email", unique = true, nullable = false, length = 100)
    @NotBlank @Email
    private String email;
    @Column(name = "telefono", length = 20)
    private String telefono;
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column(name = "numero_ine", unique = true, length = 20)
    private String ine;
    @Column(name = "direccion", length = 150)
    private String direccion;
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", nullable = false, length = 15)
    private ClienteStatus estatus = ClienteStatus.ACTIVO;
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false, length = 255)
    @NotBlank(groups = Creacion.class)
    private String password;

    @PrePersist
    void onCreate() {
        if (fechaRegistro == null) fechaRegistro = LocalDateTime.now();
        if (estatus == null) estatus = ClienteStatus.ACTIVO;
        if (tipo == null) tipo = TipoCliente.CLIENTE;
    }

    public interface Creacion extends jakarta.validation.groups.Default {}

    public enum ClienteStatus { ACTIVO, INACTIVO, SUSPENDIDO }
    public enum TipoCliente { PROPIETARIO, CLIENTE, EMPRESA }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoCliente getTipo() { return tipo; }
    public void setTipo(TipoCliente tipo) { this.tipo = tipo; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getIne() { return ine; }
    public void setIne(String ine) { this.ine = ine; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public ClienteStatus getEstatus() { return estatus; }
    public void setEstatus(ClienteStatus estatus) { this.estatus = estatus; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
