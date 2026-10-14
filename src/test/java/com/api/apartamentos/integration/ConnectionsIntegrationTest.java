package com.api.apartamentos.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import com.api.apartamentos.models.*;
import com.api.apartamentos.repositories.*;
import com.api.apartamentos.services.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ConnectionsIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    @Autowired ClienteService clientes;
    @Autowired PropiedadService propiedades;
    @Autowired UnidadRentableService unidades;
    @Autowired IReservaRepository reservas;
    @Autowired IPagosRepository pagos;
    @Autowired ReporteService reportes;
    @Autowired DisponibilidadService disponibilidades;

    @Test void conectaFormulariosServiciosMysqlYReportes() throws Exception {
        var cliente = nuevoCliente(ClienteModel.TipoCliente.CLIENTE);
        var propietario = nuevoCliente(ClienteModel.TipoCliente.PROPIETARIO);
        var propiedad = nuevaPropiedad(propietario);
        var unidad = nuevaUnidad(propiedad);
        em.flush(); em.clear();
        assertEquals(propietario.getId(), propiedades.findById(propiedad.getIdPropiedad()).orElseThrow().getPropietario().getId());
        assertEquals(propiedad.getIdPropiedad(), unidades.findById(unidad.getIdUnidad()).orElseThrow().getPropiedad().getIdPropiedad());

        mvc.perform(post("/reservas").param("clienteId", cliente.getId().toString())
                .param("unidadId", unidad.getIdUnidad().toString()).param("fechaEntrada", "2090-10-15T15:00")
                .param("fechaSalida", "2090-10-17T11:00").param("estado", "CONFIRMADA"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/reservas"));
        em.flush(); em.clear();
        var reserva = reservas.findAll().stream().filter(r -> r.getUnidad().getIdUnidad().equals(unidad.getIdUnidad())).findFirst().orElseThrow();
        assertEquals(cliente.getId(), reserva.getCliente().getId());
        assertEquals(0, new BigDecimal("1000").compareTo(reserva.getImporteTotal()));

        mvc.perform(post("/pagos").param("reservaId", reserva.getIdReservacion().toString())
                .param("importe", "400.00").param("fechaVencimiento", LocalDate.now().plusDays(1).toString()).param("metodo", "TRANSFERENCIA"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/pagos"));
        em.flush(); em.clear();
        var pago = pagos.findByReserva_IdReservacion(reserva.getIdReservacion()).getFirst();
        assertEquals(cliente.getId(), pago.getCliente().getId());
        mvc.perform(post("/pagos/" + pago.getIdPago() + "/confirmar"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/pagos"));
        em.flush(); em.clear();
        assertEquals(PagosModel.EstadoPago.PAGADO, pagos.findById(pago.getIdPago()).orElseThrow().getEstado());
        assertEquals(0, new BigDecimal("600").compareTo(reportes.estadoCuenta(cliente.getId(), LocalDate.now()).saldo()));

        mvc.perform(get("/api/clientes/" + cliente.getId() + "/estado-cuenta"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.saldo").value(600));
        for (String path : new String[]{"/propiedades", "/unidades", "/reservas", "/pagos", "/reportes",
                "/api/propiedades/" + propiedad.getIdPropiedad(), "/api/unidades/" + unidad.getIdUnidad(),
                "/api/reservas/" + reserva.getIdReservacion(), "/api/pagos/" + pago.getIdPago()}) {
            mvc.perform(get(path)).andExpect(status().isOk());
        }
        mvc.perform(post("/pagos/" + pago.getIdPago() + "/reembolsar")).andExpect(redirectedUrl("/pagos"));
        em.flush(); em.clear();
        assertEquals(0, new BigDecimal("1000").compareTo(reportes.estadoCuenta(cliente.getId(), LocalDate.now()).saldo()));
    }

    @Test void conectaBloqueoDeDisponibilidadConReservas() throws Exception {
        var cliente = nuevoCliente(ClienteModel.TipoCliente.CLIENTE);
        var unidad = nuevaUnidad(nuevaPropiedad(nuevoCliente(ClienteModel.TipoCliente.PROPIETARIO)));
        var bloqueo = new DisponibilidadModel(); bloqueo.setUnidad(unidad); bloqueo.setDisponible(false);
        bloqueo.setFechaInicio(LocalDateTime.of(2090, 10, 15, 0, 0)); bloqueo.setFechaFin(LocalDateTime.of(2090, 10, 18, 0, 0));
        disponibilidades.save(bloqueo); em.flush(); em.clear();
        mvc.perform(post("/reservas").param("clienteId", cliente.getId().toString())
                .param("unidadId", unidad.getIdUnidad().toString()).param("fechaEntrada", "2090-10-15T15:00")
                .param("fechaSalida", "2090-10-17T11:00").param("estado", "CONFIRMADA"))
            .andExpect(status().isOk()).andExpect(view().name("reservas/formulario"))
            .andExpect(model().attribute("error", "La unidad tiene un bloqueo de disponibilidad en ese rango"));
        assertFalse(reservas.findAll().stream().anyMatch(r -> r.getUnidad().getIdUnidad().equals(unidad.getIdUnidad())));
    }

    private ClienteModel nuevoCliente(ClienteModel.TipoCliente tipo) {
        var c = new ClienteModel(); c.setTipo(tipo); c.setNombres("Prueba conexión"); c.setApellidos("Temporal");
        c.setEmail(UUID.randomUUID() + "@example.com"); c.setPassword("prueba-integracion"); return clientes.saveCliente(c);
    }
    private PropiedadModel nuevaPropiedad(ClienteModel propietario) {
        var p = new PropiedadModel(); p.setPropietario(propietario); p.setTipo("CASA"); p.setTitulo("Prueba conexión temporal");
        p.setDescripcion("Registro de prueba con rollback"); p.setDireccion("Dirección de prueba"); p.setCiudad("Veracruz");
        p.setEstadoFederativo("Veracruz"); p.setCodigoPostal("91000"); p.setPais("México"); p.setCapacidad(2);
        p.setNumHabitaciones(1); p.setNumBanos(1); p.setMetrosCuadrados(50); p.setPrecioNoche(new BigDecimal("500"));
        return propiedades.save(p);
    }
    private UnidadRentableModel nuevaUnidad(PropiedadModel propiedad) {
        var u = new UnidadRentableModel(); u.setPropiedad(propiedad); u.setNombre("Unidad temporal"); u.setTipo("HABITACION");
        u.setCapacidad(2); u.setPrecioNoche(new BigDecimal("500")); return unidades.save(u);
    }
}
