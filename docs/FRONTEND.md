# Frontend de apartamentos

Diseño del sistema de apartamentos: azul marino (#17324d), verde agua (#0c8d87), detalles dorados (#e5a93f), tipografía Georgia y paneles blancos. Implementado con Thymeleaf y Bootstrap 5.3.8.

## Pantallas

- /: resumen de la operación.
- /propiedades: registro y catálogo de casas/pisos.
- /unidades: registro y catálogo de habitaciones/unidades rentables.
- /clientes: registro de arrendatarios y propietarios.
- /reservas: registro e historial de estancias.
- /pagos: registro, confirmación y reembolso de pagos.
- /reportes: ocupación, ingresos y saldos de clientes.

Las cinco pantallas de gestión muestran registro a la izquierda y catálogo a la derecha. En pantallas pequeñas se apilan y las tablas tienen desplazamiento horizontal. La búsqueda reconoce mayúsculas y acentos. Editar abre el formulario del servidor en un modal; sin JavaScript se abre la página de edición habitual. Guardar conserva las validaciones y rutas existentes. Eliminar, cancelar y reembolsar solicitan confirmación.

Los formularios de registro se comparten mediante fragmentos Thymeleaf. CSS y JavaScript propios están en src/main/resources/static. Bootstrap se incluye en static/vendor/bootstrap junto con su licencia; el frontend no necesita CDN.

## Ejecutar

Con Java 21, MySQL disponible y la base apartamentos configurada en application.properties:

```powershell
./mvnw.cmd spring-boot:run
```

Abrir http://localhost:8000/clientes o http://localhost:8000/.

## Comprobaciones

WebFrontendTest verifica los cinco catálogos con registros y vacíos, el resumen, el filtro por propiedad y los formularios de edición. ReporteControllerTest verifica el renderizado y las consultas de reportes. La revisión en navegador utilizó HTML renderizado por Spring con datos de prueba, sin modificar registros reales: apariencia de escritorio y móvil, búsqueda sin acentos y apertura/cierre del modal.

Suite completa verificada: 28 pruebas aprobadas, incluido el contexto de Spring con MySQL y validación del esquema existente.
