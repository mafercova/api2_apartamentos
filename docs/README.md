# Pruebas de la API

Instrucciones de instalación y arranque en ../README.md. La API funciona en http://localhost:8000/api.

## Orden

Cliente propietario → cliente arrendatario → propiedad → unidad → disponibilidad opcional → reserva → pago → reportes.

Los ejemplos usan IDs ficticios; sustituirlos por los IDs devueltos por cada POST. Enviar JSON con Content-Type: application/json. También se puede realizar el flujo principal desde el frontend.

Crear un cliente con POST /api/clientes:

```json
{"tipo":"CLIENTE","nombres":"Ana","apellidos":"Pérez","email":"ana@example.com","password":"ejemplo-local","telefono":"2290000000","estatus":"ACTIVO"}
```

Para un propietario usar tipo PROPIETARIO. Las contraseñas no aparecen en las respuestas.

Crear una propiedad con POST /api/propiedades:

```json
{"propietario":{"id":1},"tipo":"CASA","titulo":"Casa jardín","descripcion":"Casa de dos habitaciones","direccion":"Calle de ejemplo 10","ciudad":"Veracruz","estadoFederativo":"Veracruz","codigoPostal":"91000","pais":"México","latitud":19.17,"longitud":-96.13,"precioNoche":500,"capacidad":4,"numHabitaciones":2,"numBanos":1,"metrosCuadrados":70,"estado":"DISPONIBLE"}
```

Crear una unidad con POST /api/unidades:

```json
{"propiedad":{"idPropiedad":1},"nombre":"Habitación jardín","tipo":"HABITACION","capacidad":2,"precioNoche":500,"estado":"DISPONIBLE"}
```

Crear un bloqueo con POST /api/disponibilidades (usar otro intervalo para permitir la reserva de ejemplo):

```json
{"unidad":{"idUnidad":1},"fechaInicio":"2090-11-01T00:00:00","fechaFin":"2090-11-05T00:00:00","disponible":false}
```

Crear una reserva con POST /api/reservas:

```json
{"unidad":{"idUnidad":1},"cliente":{"id":2},"fechaEntrada":"2090-10-15T15:00:00","fechaSalida":"2090-10-17T11:00:00","estado":"CONFIRMADA"}
```

El servidor calcula importeTotal cuando no se envía.

Registrar pago con POST /api/pagos:

```json
{"reserva":{"idReservacion":1},"importe":400,"fechaVencimiento":"2090-10-15","metodo":"TRANSFERENCIA","estado":"PENDIENTE"}
```

- POST /api/pagos/1/confirmar: registra el cobro.
- POST /api/pagos/1/reembolsar: registra el reembolso de un pago confirmado.
- GET /api/clientes/2/estado-cuenta: saldo del arrendatario.
- GET /api/reportes/ocupacion?desde=2090-10-01&hasta=2090-11-01
- GET /api/reportes/ingresos?desde=2026-10-01&hasta=2026-11-01 (por fecha real del cobro).
- GET /api/reportes/morosidad
- GET /actuator/health: estado de la aplicación.

Los IDs, fechas de registro, referencia del pago y fecha de cobro se generan en el servidor. El cliente del pago se deriva de su reserva.
