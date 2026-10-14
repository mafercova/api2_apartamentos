package com.api.apartamentos.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.nio.file.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.api.apartamentos.models.*;
import com.api.apartamentos.services.*;

@WebMvcTest(WebController.class)
class WebFrontendTest {
    @Autowired MockMvc mvc;
    @MockitoBean ClienteService clientes;
    @MockitoBean PropiedadService propiedades;
    @MockitoBean UnidadRentableService unidades;
    @MockitoBean ReservaService reservas;
    @MockitoBean PagosService pagos;

    @BeforeEach void datos() {
        var cliente = new ClienteModel(); cliente.setId(1L); cliente.setNombres("Ana"); cliente.setApellidos("López"); cliente.setEmail("ana@example.com");
        var propietario = new ClienteModel(); propietario.setId(2L); propietario.setNombres("Sergio"); propietario.setApellidos("Alvarado"); propietario.setEmail("sergio@example.com"); propietario.setTipo(ClienteModel.TipoCliente.PROPIETARIO);
        var propiedad = new PropiedadModel(); propiedad.setIdPropiedad(1L); propiedad.setTitulo("Casa del Parque"); propiedad.setPropietario(propietario); propiedad.setTipo("CASA"); propiedad.setCiudad("Veracruz"); propiedad.setPais("México"); propiedad.setPrecioNoche(new BigDecimal("850")); propiedad.setCapacidad(4);
        var unidad = new UnidadRentableModel(); unidad.setIdUnidad(1L); unidad.setPropiedad(propiedad); unidad.setNombre("Habitación jardín"); unidad.setTipo("HABITACION"); unidad.setCapacidad(2); unidad.setPrecioNoche(new BigDecimal("500"));
        var reserva = new ReservaModel(); reserva.setIdReservacion(1L); reserva.setUnidad(unidad); reserva.setCliente(cliente); reserva.setFechaEntrada(LocalDateTime.of(2026,10,15,15,0)); reserva.setFechaSalida(LocalDateTime.of(2026,10,17,11,0)); reserva.setImporteTotal(new BigDecimal("1000"));
        var pago = new PagosModel(); pago.setIdPago(1L); pago.setReserva(reserva); pago.setCliente(cliente); pago.setImporte(new BigDecimal("500")); pago.setReferenciaPago("PAG-0001"); pago.setFechaVencimiento(LocalDate.of(2026,10,15));
        when(clientes.findAll()).thenReturn(List.of(cliente,propietario)); when(clientes.findById(1L)).thenReturn(Optional.of(cliente));
        when(propiedades.findAll()).thenReturn(List.of(propiedad)); when(propiedades.findById(1L)).thenReturn(Optional.of(propiedad));
        when(unidades.findAll()).thenReturn(List.of(unidad)); when(unidades.findById(1L)).thenReturn(Optional.of(unidad)); when(unidades.findByPropiedad(1L)).thenReturn(List.of(unidad));
        when(reservas.findAll()).thenReturn(List.of(reserva)); when(reservas.findById(1L)).thenReturn(Optional.of(reserva));
        when(pagos.findAll()).thenReturn(List.of(pago)); when(pagos.findById(1L)).thenReturn(Optional.of(pago));
    }
    @Test void renderizaCatalogosConRegistroYEdicion() throws Exception {
        for (String domain : List.of("clientes","propiedades","unidades","reservas","pagos")) {
            var response = mvc.perform(get("/" + domain)).andExpect(status().isOk())
                .andExpect(content().string(containsString("data-search")))
                .andExpect(content().string(containsString("data-edit-dialog")))
                .andExpect(content().string(containsString("id=\"registro\"")))
                .andExpect(content().string(containsString("action=\"/" + domain + "\""))).andReturn();
            preview("/" + domain, response.getResponse().getContentAsString());
        }
        var response = mvc.perform(get("/")).andExpect(status().isOk()).andReturn();
        preview("/", response.getResponse().getContentAsString());
        mvc.perform(get("/propiedades/1/unidades")).andExpect(status().isOk()).andExpect(content().string(containsString("Unidades de Casa del Parque")));
    }
    @Test void renderizaFormulariosDeEdicionParaModal() throws Exception {
        for (String domain : List.of("clientes","propiedades","unidades","reservas","pagos")) {
            var response = mvc.perform(get("/" + domain + "/1/editar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"/" + domain + "/1\""))).andReturn();
            preview("/" + domain + "/1/editar", response.getResponse().getContentAsString());
        }
    }
    @Test void muestraCatalogosVaciosSinErrores() throws Exception {
        when(clientes.findAll()).thenReturn(List.of()); when(propiedades.findAll()).thenReturn(List.of());
        when(unidades.findAll()).thenReturn(List.of()); when(reservas.findAll()).thenReturn(List.of()); when(pagos.findAll()).thenReturn(List.of());
        for (String domain : List.of("clientes","propiedades","unidades","reservas","pagos")) mvc.perform(get("/" + domain)).andExpect(status().isOk());
    }
    private void preview(String path, String html) throws Exception {
        String destination = System.getProperty("frontend.preview.dir");
        if (destination == null) return;
        Path file = Path.of(destination).resolve(path.substring(1)).resolve("index.html");
        Files.createDirectories(file.getParent()); Files.writeString(file, html);
    }
}
