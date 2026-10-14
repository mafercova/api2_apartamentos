package com.api.apartamentos.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.api.apartamentos.services.ReporteService;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean ReporteService service;

    @Test void muestraReportesYEstadosDeCuenta() throws Exception {
        LocalDate d = LocalDate.of(2026,10,1), h = d.plusMonths(1);
        when(service.ocupacion(d,h)).thenReturn(new ReporteService.Ocupacion(d,h,1,2,new BigDecimal("50.00")));
        when(service.ingresos(d,h)).thenReturn(new ReporteService.Ingresos(d,h,new BigDecimal("400")));
        when(service.cuentas(any())).thenReturn(List.of(new ReporteService.Cuenta(1L,"Ana López",new BigDecimal("1000"),new BigDecimal("400"),new BigDecimal("600"),new BigDecimal("600"))));
        mvc.perform(get("/reportes").param("desde",d.toString()).param("hasta",h.toString()))
            .andExpect(status().isOk()).andExpect(view().name("reportes"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Ana López")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("50.00%")));
    }
    @Test void exponeIngresosComoJson() throws Exception {
        LocalDate d = LocalDate.of(2026,10,1), h = d.plusMonths(1);
        when(service.ingresos(d,h)).thenReturn(new ReporteService.Ingresos(d,h,new BigDecimal("400")));
        mvc.perform(get("/api/reportes/ingresos").param("desde",d.toString()).param("hasta",h.toString()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(400));
    }
    @Test void rechazaPeriodoInvalido() throws Exception {
        when(service.ingresos(any(),any())).thenThrow(new IllegalArgumentException("El fin debe ser posterior al inicio"));
        mvc.perform(get("/api/reportes/ingresos").param("desde","2026-11-01").param("hasta","2026-10-01"))
            .andExpect(status().isBadRequest());
    }
}
