package com.api.apartamentos.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "propiedades")
public class PropiedadModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_propiedad")
    private Long idPropiedad;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_propietario", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private ClienteModel propietario;
    @Column(name = "tipo_propiedad", nullable = false, length = 40)
    private String tipo;
    @Column(nullable = false, length = 100) private String titulo;
    @Column(nullable = false, length = 500) private String descripcion;
    @Column(nullable = false, length = 150) private String direccion;
    @Column(nullable = false, length = 50) private String ciudad;
    @Column(name = "estado", nullable = false, length = 50) private String estadoFederativo;
    @Column(name = "codigo_postal", nullable = false, length = 10) private String codigoPostal;
    @Column(nullable = false, length = 50) private String pais;
    @Column(nullable = false) private double latitud;
    @Column(nullable = false) private double longitud;
    @Column(name = "precio_noche", nullable = false, precision = 12, scale = 2) private BigDecimal precioNoche;
    @Column(nullable = false) private int capacidad;
    @Column(name = "num_habitaciones", nullable = false) private int numHabitaciones;
    @Column(name = "num_banos", nullable = false) private int numBanos;
    @Column(name = "metro_cuadrados", nullable = false) private int metrosCuadrados;
    @Column(length = 500) private String comodidades;
    @Column(length = 500) private String reglas;
    @Enumerated(EnumType.STRING) @Column(name = "estado_hab", nullable = false, length = 20)
    private EstadoPropiedad estado = EstadoPropiedad.DISPONIBLE;
    @Column(name = "fecha_registro", nullable = false) private LocalDateTime fechaRegistro;
    @Column(name = "fecha_actualizacion", nullable = false) private LocalDateTime fechaActualizacion;

    @PrePersist void onCreate() { fechaRegistro = LocalDateTime.now(); fechaActualizacion = fechaRegistro; }
    @PreUpdate void onUpdate() { fechaActualizacion = LocalDateTime.now(); }
    public enum EstadoPropiedad { DISPONIBLE, NO_DISPONIBLE, MANTENIMIENTO }

    public Long getIdPropiedad() { return idPropiedad; }
    public void setIdPropiedad(Long value) { idPropiedad = value; }
    public ClienteModel getPropietario() { return propietario; }
    public void setPropietario(ClienteModel value) { propietario = value; }
    public String getTipo() { return tipo; }
    public void setTipo(String value) { tipo = value; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String value) { titulo = value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { descripcion = value; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String value) { direccion = value; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String value) { ciudad = value; }
    public String getEstadoFederativo() { return estadoFederativo; }
    public void setEstadoFederativo(String value) { estadoFederativo = value; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String value) { codigoPostal = value; }
    public String getPais() { return pais; }
    public void setPais(String value) { pais = value; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double value) { latitud = value; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double value) { longitud = value; }
    public BigDecimal getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(BigDecimal value) { precioNoche = value; }
    public BigDecimal getPrecio_noche() { return precioNoche; }
    public void setPrecio_noche(BigDecimal value) { precioNoche = value; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int value) { capacidad = value; }
    public int getNumHabitaciones() { return numHabitaciones; }
    public void setNumHabitaciones(int value) { numHabitaciones = value; }
    public int getNumBanos() { return numBanos; }
    public void setNumBanos(int value) { numBanos = value; }
    public int getMetrosCuadrados() { return metrosCuadrados; }
    public void setMetrosCuadrados(int value) { metrosCuadrados = value; }
    public String getComodidades() { return comodidades; }
    public void setComodidades(String value) { comodidades = value; }
    public String getReglas() { return reglas; }
    public void setReglas(String value) { reglas = value; }
    public EstadoPropiedad getEstado() { return estado; }
    public void setEstado(EstadoPropiedad value) { estado = value; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
}
