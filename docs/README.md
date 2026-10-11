# Pruebas de la API

Los cuerpos JSON de cada entidad estan en `docs/json/`.

## Orden recomendado

1. Cliente
2. Propiedad
3. Propiedad imagen y unidad rentable
4. Disponibilidad
5. Reserva
6. Pago
7. Resena y notificacion

Los IDs usados en los ejemplos son valores de referencia. Sustituyelos por los IDs reales devueltos por cada respuesta `POST`.

## Probar desde VS Code

Abre `docs/pruebas-api.http` y usa la extension REST Client o el boton `Send Request` de VS Code.

Antes, inicia la aplicacion:

```text
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8000`.

## Probar con curl

Ejemplo para crear un cliente:

```bash
curl -X POST http://localhost:8000/api/clientes \\
  -H 'Content-Type: application/json' \\
  --data @docs/json/cliente.json
```

## Campos generados por el servidor

- IDs de todas las entidades.
- Fechas de registro, creacion, envio o resena.
- En reservas, `importeTotal` se calcula con el precio de la unidad cuando no se envia.
- En pagos, `referenciaPago` se genera automaticamente y el cliente se obtiene desde la reserva.
- En pagos, `fechaPago` se asigna al confirmar.

`UsuarioModel` aun no existe en el proyecto, por eso no hay JSON de usuarios en esta coleccion.
