# Revisión para entrega

## Resultado

Compilación limpia y empaquetado correctos con Maven Wrapper y Java 21. Comando: ./mvnw.cmd -o clean verify -Dspring.jpa.hibernate.ddl-auto=validate.

28 pruebas ejecutadas: 0 fallos, 0 errores y 0 omitidas. Incluyen arranque con MySQL, renderizado del frontend, reglas de reservas/pagos/reportes y flujo de formularios hasta la base real con rollback.

El JAR ejecutable se inició por separado en el puerto 8001 con ddl-auto=validate. Se verificaron 22 rutas HTTP del JAR (pantallas, API, reportes, recursos propios y Bootstrap local): todas respondieron 200. El estado de salud es UP. El frontend ya no depende de CDN.

## Correcciones de preparación

- Guía principal de instalación, variables de entorno, compilación, arranque y demostración.
- Ejemplos reales de API en lugar de referencias a archivos inexistentes.
- Bootstrap incluido localmente con su licencia.
- Retirados comentarios de ejercicios pendientes ajenos a los requisitos.
- Parámetros configurables: DB_URL, DB_USERNAME, DB_PASSWORD, SERVER_PORT y DDL_AUTO.

## Relación con el enunciado

| Requisito | Implementación |
| --- | --- |
| Propiedades y características | Propiedad y unidad rentable; formularios y API |
| Arrendatarios y propietarios | Clientes con tipo; relación de propiedad |
| Disponibilidad y reservas | CRUD de disponibilidad en API, validación de bloqueos y solapamientos |
| Pagos y estados de cuenta | Registro, confirmación, reembolso y saldo por cliente |
| Ocupación, ingresos, deudores | Pantalla /reportes y API |
| Arquitectura por servicios | Controladores, servicios de dominio y repositorios documentados |

## Alcance de la entrega

La arquitectura tiene una sola aplicación con capa de servicios; no son microservicios independientes. La disponibilidad por intervalos se administra por API. Los cobros son registros administrativos y no transacciones bancarias. No hay inicio de sesión. Para una demostración local se necesitan Java 21 y MySQL configurado; no se incluye información personal de la base del equipo.

La revisión acredita los escenarios y comprobaciones anteriores; no supone garantizar ausencia absoluta de defectos en cualquier entorno.
