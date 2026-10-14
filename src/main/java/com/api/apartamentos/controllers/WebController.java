package com.api.apartamentos.controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.apartamentos.models.ClienteModel;
import com.api.apartamentos.models.PropiedadModel;
import com.api.apartamentos.models.ReservaModel;
import com.api.apartamentos.models.PagosModel;
import com.api.apartamentos.services.ClienteService;
import com.api.apartamentos.services.PagosService;
import com.api.apartamentos.services.PropiedadService;
import com.api.apartamentos.services.ReservaService;
import com.api.apartamentos.services.UnidadRentableService;

import jakarta.validation.Valid;

@Controller
public class WebController {
    private final ClienteService clienteService;
    private final PropiedadService propiedadService;
    private final ReservaService reservaService;
    private final UnidadRentableService unidadRentableService;
    private final PagosService pagosService;

    public WebController(ClienteService clienteService, PropiedadService propiedadService,
            ReservaService reservaService, UnidadRentableService unidadRentableService,
            PagosService pagosService) {
        this.clienteService = clienteService;
        this.propiedadService = propiedadService;
        this.reservaService = reservaService;
        this.unidadRentableService = unidadRentableService;
        this.pagosService = pagosService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("clientesTotal", clienteService.findAll().size());
        model.addAttribute("propiedadesTotal", propiedadService.findAll().size());
        model.addAttribute("reservasTotal", reservaService.findAll().size());
        model.addAttribute("pagosTotal", pagosService.findAll().size());
        model.addAttribute("clientesRecientes", clienteService.findAll().stream().limit(5).toList());
        model.addAttribute("propiedadesRecientes", propiedadService.findAll().stream().limit(5).toList());
        return "index";
    }

    @GetMapping("/clientes")
    public String clientes(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("cliente", new ClienteModel());
        model.addAttribute("modo", "Crear");
        return "clientes/lista";
    }

    @GetMapping("/clientes/nuevo")
    public String nuevoCliente(Model model) {
        model.addAttribute("cliente", new ClienteModel());
        model.addAttribute("modo", "Crear");
        return "clientes/formulario";
    }

    @PostMapping("/clientes")
    public String crearCliente(@org.springframework.validation.annotation.Validated(ClienteModel.Creacion.class) @ModelAttribute("cliente") ClienteModel cliente,
            BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("modo", "Crear");
            return "clientes/formulario";
        }
        clienteService.saveCliente(cliente);
        redirectAttributes.addFlashAttribute("success", "Cliente creado correctamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/clientes/{id}/editar")
    public String editarCliente(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ClienteModel> cliente = clienteService.findById(id);
        if (cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El cliente no existe.");
            return "redirect:/clientes";
        }
        model.addAttribute("cliente", cliente.get());
        model.addAttribute("modo", "Editar");
        return "clientes/formulario";
    }

    @PostMapping("/clientes/{id}")
    public String actualizarCliente(@PathVariable Long id, @Valid @ModelAttribute("cliente") ClienteModel datos,
            BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        Optional<ClienteModel> existente = clienteService.findById(id);
        if (existente.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El cliente no existe.");
            return "redirect:/clientes";
        }
        if (result.hasErrors()) {
            model.addAttribute("modo", "Editar");
            return "clientes/formulario";
        }
        ClienteModel cliente = existente.get();
        cliente.setTipo(datos.getTipo());
        cliente.setNombres(datos.getNombres());
        cliente.setApellidos(datos.getApellidos());
        cliente.setEmail(datos.getEmail());
        cliente.setTelefono(datos.getTelefono());
        cliente.setFechaNacimiento(datos.getFechaNacimiento());
        cliente.setIne(datos.getIne());
        cliente.setDireccion(datos.getDireccion());
        cliente.setEstatus(datos.getEstatus());
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) cliente.setPassword(datos.getPassword());
        clienteService.saveCliente(cliente);
        redirectAttributes.addFlashAttribute("success", "Cliente actualizado correctamente.");
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/{id}/eliminar")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (clienteService.findById(id).isPresent()) {
            clienteService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Cliente eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "El cliente no existe.");
        }
        return "redirect:/clientes";
    }

    @GetMapping("/propiedades")
    public String propiedades(Model model) {
        model.addAttribute("propiedades", propiedadService.findAll());
        model.addAttribute("propiedad", new PropiedadModel());
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("modo", "Crear");
        return "propiedades/lista";
    }

    @GetMapping("/propiedades/nueva")
    public String nuevaPropiedad(Model model) {
        model.addAttribute("propiedad", new PropiedadModel());
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("modo", "Crear");
        return "propiedades/formulario";
    }

    @PostMapping("/propiedades")
    public String crearPropiedad(@ModelAttribute("propiedad") PropiedadModel propiedad,
            @RequestParam Long propietarioId, RedirectAttributes redirectAttributes) {
        Optional<ClienteModel> propietario = clienteService.findById(propietarioId);
        if (propietario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El propietario seleccionado no existe.");
            return "redirect:/propiedades/nueva";
        }
        propiedad.setPropietario(propietario.get());
        propiedadService.save(propiedad);
        redirectAttributes.addFlashAttribute("success", "Propiedad creada correctamente.");
        return "redirect:/propiedades";
    }

    @GetMapping("/propiedades/{id}/editar")
    public String editarPropiedad(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PropiedadModel> propiedad = propiedadService.findById(id);
        if (propiedad.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La propiedad no existe.");
            return "redirect:/propiedades";
        }
        model.addAttribute("propiedad", propiedad.get());
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("modo", "Editar");
        return "propiedades/formulario";
    }

    @PostMapping("/propiedades/{id}")
    public String actualizarPropiedad(@PathVariable Long id, @ModelAttribute("propiedad") PropiedadModel datos,
            @RequestParam Long propietarioId, RedirectAttributes redirectAttributes) {
        Optional<PropiedadModel> existente = propiedadService.findById(id);
        Optional<ClienteModel> propietario = clienteService.findById(propietarioId);
        if (existente.isEmpty() || propietario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar la propiedad.");
            return "redirect:/propiedades";
        }
        PropiedadModel propiedad = existente.get();
        propiedad.setPropietario(propietario.get());
        propiedad.setTipo(datos.getTipo());
        propiedad.setTitulo(datos.getTitulo());
        propiedad.setDescripcion(datos.getDescripcion());
        propiedad.setDireccion(datos.getDireccion());
        propiedad.setCiudad(datos.getCiudad());
        propiedad.setEstadoFederativo(datos.getEstadoFederativo());
        propiedad.setCodigoPostal(datos.getCodigoPostal());
        propiedad.setPais(datos.getPais());
        propiedad.setLatitud(datos.getLatitud());
        propiedad.setLongitud(datos.getLongitud());
        propiedad.setPrecioNoche(datos.getPrecioNoche());
        propiedad.setCapacidad(datos.getCapacidad());
        propiedad.setNumHabitaciones(datos.getNumHabitaciones());
        propiedad.setNumBanos(datos.getNumBanos());
        propiedad.setMetrosCuadrados(datos.getMetrosCuadrados());
        propiedad.setComodidades(datos.getComodidades());
        propiedad.setReglas(datos.getReglas());
        propiedad.setEstado(datos.getEstado());
        propiedadService.save(propiedad);
        redirectAttributes.addFlashAttribute("success", "Propiedad actualizada correctamente.");
        return "redirect:/propiedades";
    }

    @PostMapping("/propiedades/{id}/eliminar")
    public String eliminarPropiedad(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (propiedadService.findById(id).isPresent()) {
            propiedadService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Propiedad eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "La propiedad no existe.");
        }
        return "redirect:/propiedades";
    }

    @GetMapping("/unidades")
    public String unidades(Model model) {
        model.addAttribute("unidades", unidadRentableService.findAll());
        model.addAttribute("propiedadFiltro", null);
        prepararFormularioUnidad(model, new com.api.apartamentos.models.UnidadRentableModel(), "Crear");
        return "unidades/lista";
    }

    @GetMapping("/propiedades/{id}/unidades")
    public String unidadesPorPropiedad(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PropiedadModel> propiedad = propiedadService.findById(id);
        if (propiedad.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La propiedad no existe.");
            return "redirect:/propiedades";
        }
        model.addAttribute("unidades", unidadRentableService.findByPropiedad(id));
        model.addAttribute("propiedadFiltro", propiedad.get());
        var nueva = new com.api.apartamentos.models.UnidadRentableModel();
        nueva.setPropiedad(propiedad.get());
        prepararFormularioUnidad(model, nueva, "Crear");
        return "unidades/lista";
    }

    @GetMapping("/unidades/nueva")
    public String nuevaUnidad(Model model) {
        prepararFormularioUnidad(model, new com.api.apartamentos.models.UnidadRentableModel(), "Crear");
        return "unidades/formulario";
    }

    @PostMapping("/unidades")
    public String crearUnidad(@ModelAttribute("unidad") com.api.apartamentos.models.UnidadRentableModel unidad,
            @RequestParam Long propiedadId, RedirectAttributes redirectAttributes, Model model) {
        Optional<PropiedadModel> propiedad = propiedadService.findById(propiedadId);
        if (propiedad.isEmpty()) {
            prepararFormularioUnidad(model, unidad, "Crear");
            model.addAttribute("error", "La propiedad seleccionada no existe.");
            return "unidades/formulario";
        }
        unidad.setPropiedad(propiedad.get());
        try {
            unidadRentableService.save(unidad);
            redirectAttributes.addFlashAttribute("success", "Unidad rentable creada correctamente.");
            return "redirect:/unidades";
        } catch (IllegalArgumentException exception) {
            prepararFormularioUnidad(model, unidad, "Crear");
            model.addAttribute("error", exception.getMessage());
            return "unidades/formulario";
        }
    }

    @GetMapping("/unidades/{id}/editar")
    public String editarUnidad(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<com.api.apartamentos.models.UnidadRentableModel> unidad = unidadRentableService.findById(id);
        if (unidad.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La unidad no existe.");
            return "redirect:/unidades";
        }
        prepararFormularioUnidad(model, unidad.get(), "Editar");
        return "unidades/formulario";
    }

    @PostMapping("/unidades/{id}")
    public String actualizarUnidad(@PathVariable Long id,
            @ModelAttribute("unidad") com.api.apartamentos.models.UnidadRentableModel datos,
            @RequestParam Long propiedadId, RedirectAttributes redirectAttributes, Model model) {
        Optional<com.api.apartamentos.models.UnidadRentableModel> existente = unidadRentableService.findById(id);
        Optional<PropiedadModel> propiedad = propiedadService.findById(propiedadId);
        if (existente.isEmpty() || propiedad.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar la unidad.");
            return "redirect:/unidades";
        }
        datos.setIdUnidad(id);
        datos.setPropiedad(propiedad.get());
        try {
            unidadRentableService.save(datos);
            redirectAttributes.addFlashAttribute("success", "Unidad rentable actualizada correctamente.");
            return "redirect:/unidades";
        } catch (IllegalArgumentException exception) {
            prepararFormularioUnidad(model, datos, "Editar");
            model.addAttribute("error", exception.getMessage());
            return "unidades/formulario";
        }
    }

    @PostMapping("/unidades/{id}/eliminar")
    public String eliminarUnidad(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (unidadRentableService.findById(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La unidad no existe.");
        } else {
            try {
                unidadRentableService.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Unidad rentable eliminada correctamente.");
            } catch (org.springframework.dao.DataIntegrityViolationException exception) {
                redirectAttributes.addFlashAttribute("error", "No se puede eliminar una unidad con reservas o disponibilidades relacionadas.");
            }
        }
        return "redirect:/unidades";
    }

    private void prepararFormularioUnidad(Model model, com.api.apartamentos.models.UnidadRentableModel unidad, String modo) {
        model.addAttribute("unidad", unidad);
        model.addAttribute("propiedades", propiedadService.findAll());
        model.addAttribute("modo", modo);
    }

    @GetMapping("/reservas")
    public String reservas(Model model) {
        model.addAttribute("reservas", reservaService.findAll());
        prepararFormularioReserva(model, new ReservaModel(), "Crear");
        return "reservas/lista";
    }

    @GetMapping("/reservas/nueva")
    public String nuevaReserva(Model model) {
        prepararFormularioReserva(model, new ReservaModel(), "Crear");
        return "reservas/formulario";
    }

    @PostMapping("/reservas")
    public String crearReserva(@ModelAttribute("reserva") ReservaModel reserva,
            @RequestParam Long clienteId, @RequestParam Long unidadId,
            RedirectAttributes redirectAttributes, Model model) {
        Optional<ClienteModel> cliente = clienteService.findById(clienteId);
        var unidad = unidadRentableService.findById(unidadId);
        if (cliente.isEmpty() || unidad.isEmpty()) {
            prepararFormularioReserva(model, reserva, "Crear");
            model.addAttribute("error", "El cliente o la unidad seleccionada no existe.");
            return "reservas/formulario";
        }
        reserva.setCliente(cliente.get());
        reserva.setUnidad(unidad.get());
        try {
            reservaService.save(reserva);
            redirectAttributes.addFlashAttribute("success", "Reserva creada correctamente.");
            return "redirect:/reservas";
        } catch (IllegalArgumentException exception) {
            prepararFormularioReserva(model, reserva, "Crear");
            model.addAttribute("error", exception.getMessage());
            return "reservas/formulario";
        }
    }

    @GetMapping("/reservas/{id}/editar")
    public String editarReserva(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ReservaModel> reserva = reservaService.findById(id);
        if (reserva.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La reserva no existe.");
            return "redirect:/reservas";
        }
        prepararFormularioReserva(model, reserva.get(), "Editar");
        return "reservas/formulario";
    }

    @PostMapping("/reservas/{id}")
    public String actualizarReserva(@PathVariable Long id, @ModelAttribute("reserva") ReservaModel datos,
            @RequestParam Long clienteId, @RequestParam Long unidadId,
            RedirectAttributes redirectAttributes, Model model) {
        Optional<ReservaModel> existente = reservaService.findById(id);
        Optional<ClienteModel> cliente = clienteService.findById(clienteId);
        var unidad = unidadRentableService.findById(unidadId);
        if (existente.isEmpty() || cliente.isEmpty() || unidad.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar la reserva.");
            return "redirect:/reservas";
        }
        datos.setIdReservacion(id);
        datos.setCliente(cliente.get());
        datos.setUnidad(unidad.get());
        try {
            reservaService.save(datos);
            redirectAttributes.addFlashAttribute("success", "Reserva actualizada correctamente.");
            return "redirect:/reservas";
        } catch (IllegalArgumentException exception) {
            prepararFormularioReserva(model, datos, "Editar");
            model.addAttribute("error", exception.getMessage());
            return "reservas/formulario";
        }
    }

    @PostMapping("/reservas/{id}/cancelar")
    public String cancelarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<ReservaModel> reserva = reservaService.findById(id);
        if (reserva.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La reserva no existe.");
        } else {
            reserva.get().setEstado("CANCELADA");
            reservaService.save(reserva.get());
            redirectAttributes.addFlashAttribute("success", "Reserva cancelada correctamente.");
        }
        return "redirect:/reservas";
    }

    private void prepararFormularioReserva(Model model, ReservaModel reserva, String modo) {
        model.addAttribute("reserva", reserva);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("unidades", unidadRentableService.findAll());
        model.addAttribute("modo", modo);
    }

    @GetMapping("/pagos")
    public String pagos(Model model) {
        model.addAttribute("pagos", pagosService.findAll());
        PagosModel nuevo = new PagosModel();
        nuevo.setFechaVencimiento(java.time.LocalDate.now().plusDays(1));
        prepararFormularioPago(model, nuevo, "Crear");
        return "pagos/lista";
    }

    @GetMapping("/pagos/nuevo")
    public String nuevoPago(Model model) {
        PagosModel pago = new PagosModel();
        pago.setFechaVencimiento(java.time.LocalDate.now().plusDays(1));
        prepararFormularioPago(model, pago, "Crear");
        return "pagos/formulario";
    }

    @PostMapping("/pagos")
    public String crearPago(@ModelAttribute("pago") PagosModel pago, @RequestParam Long reservaId,
            RedirectAttributes redirectAttributes, Model model) {
        Optional<ReservaModel> reserva = reservaService.findById(reservaId);
        if (reserva.isEmpty()) {
            prepararFormularioPago(model, pago, "Crear");
            model.addAttribute("error", "La reserva seleccionada no existe.");
            return "pagos/formulario";
        }
        pago.setReserva(reserva.get());
        try {
            pagosService.save(pago);
            redirectAttributes.addFlashAttribute("success", "Pago registrado correctamente.");
            return "redirect:/pagos";
        } catch (IllegalArgumentException | org.springframework.dao.DataIntegrityViolationException exception) {
            prepararFormularioPago(model, pago, "Crear");
            model.addAttribute("error", exception.getMessage());
            return "pagos/formulario";
        }
    }

    @GetMapping("/pagos/{id}/editar")
    public String editarPago(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PagosModel> pago = pagosService.findById(id);
        if (pago.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El pago no existe.");
            return "redirect:/pagos";
        }
        prepararFormularioPago(model, pago.get(), "Editar");
        return "pagos/formulario";
    }

    @PostMapping("/pagos/{id}")
    public String actualizarPago(@PathVariable Long id, @ModelAttribute("pago") PagosModel datos,
            @RequestParam Long reservaId, RedirectAttributes redirectAttributes, Model model) {
        Optional<PagosModel> existente = pagosService.findById(id);
        Optional<ReservaModel> reserva = reservaService.findById(reservaId);
        if (existente.isEmpty() || reserva.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el pago.");
            return "redirect:/pagos";
        }
        PagosModel pago = datos;
        pago.setIdPago(id);
        pago.setReserva(reserva.get());
        try {
            pagosService.save(pago);
            redirectAttributes.addFlashAttribute("success", "Pago actualizado correctamente.");
            return "redirect:/pagos";
        } catch (IllegalArgumentException | org.springframework.dao.DataIntegrityViolationException exception) {
            prepararFormularioPago(model, pago, "Editar");
            model.addAttribute("error", exception.getMessage());
            return "pagos/formulario";
        }
    }

    @PostMapping("/pagos/{id}/confirmar")
    public String confirmarPago(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pagosService.confirmar(id);
            redirectAttributes.addFlashAttribute("success", "Pago confirmado correctamente.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/pagos";
    }

    @PostMapping("/pagos/{id}/reembolsar")
    public String reembolsarPago(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pagosService.reembolsar(id);
            redirectAttributes.addFlashAttribute("success", "Pago reembolsado correctamente.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/pagos";
    }

    @PostMapping("/pagos/{id}/eliminar")
    public String eliminarPago(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pagosService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Pago eliminado correctamente.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/pagos";
    }

    private void prepararFormularioPago(Model model, PagosModel pago, String modo) {
        model.addAttribute("pago", pago);
        model.addAttribute("reservas", reservaService.findAll());
        model.addAttribute("modo", modo);
    }
}
