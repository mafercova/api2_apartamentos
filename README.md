# Gestión de apartamentos

Sistema de administración de casas, pisos y habitaciones con clientes, propietarios, reservas, disponibilidad, pagos y reportes. Spring Boot, Java 21, Thymeleaf y MySQL. La interfaz permite administrar el sistema desde el navegador. Bootstrap 5.3.8 se incluye localmente con su licencia.

## Requisitos

- JDK 21 (JAVA_HOME debe apuntar al JDK).
- MySQL 8 con una base llamada apartamentos.
- Maven Wrapper incluido. La primera compilación necesita internet para descargar dependencias.

En MySQL, crear una base vacía si todavía no existe:

```sql
CREATE DATABASE IF NOT EXISTS apartamentos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Configuración y ejecución en Windows

Desde la carpeta que contiene pom.xml:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
$env:DB_URL = 'jdbc:mysql://localhost:3306/apartamentos'
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = 'tu_clave_mysql'
.\mvnw.cmd spring-boot:run
```

Abrir http://localhost:8000/. Las credenciales de ejemplo deben coincidir con tu instalación. Sin variables se conservan los valores locales del proyecto (usuario root, contraseña root). SERVER_PORT permite cambiar el puerto y DDL_AUTO permite cambiar la gestión del esquema. El valor predeterminado update crea/actualiza las tablas al iniciar; validate solo comprueba un esquema existente.

## Compilar, probar y ejecutar el JAR

Las pruebas completas necesitan MySQL y las tablas creadas. Para una base recién creada, iniciar una vez la aplicación con DDL_AUTO=update y después detenerla.

```powershell
.\mvnw.cmd clean verify '-Dspring.jpa.hibernate.ddl-auto=validate'
java -jar .\target\apartamentos-0.0.1-SNAPSHOT.jar
```

Las pruebas de integración crean registros temporales dentro de transacciones revertidas automáticamente. Pueden avanzar los contadores AUTO_INCREMENT. Ejecutarlas en una base de desarrollo.

Para pruebas unitarias y de pantallas sin conexión a MySQL:

```powershell
.\mvnw.cmd test '-Dtest=ClienteServiceTest,PagosServiceTest,UnidadRentableServiceTest,ReservaServiceTest,ReporteServiceTest,ReporteControllerTest,WebFrontendTest'
```

## Funciones y pantallas

| Pantalla | Función |
| --- | --- |
| / | Resumen |
| /propiedades | Casas/pisos, características y propietario |
| /unidades | Habitaciones/unidades, capacidad, precio y estado |
| /clientes | Arrendatarios (CLIENTE) y propietarios (PROPIETARIO) |
| /reservas | Reservas, estancias y validación de disponibilidad |
| /pagos | Registro, confirmación y reembolso |
| /reportes | Ocupación, ingresos, saldos y morosidad |

La disponibilidad por intervalos se administra mediante /api/disponibilidades. Los demás módulos también exponen rutas /api/. Los formularios de edición funcionan en modal o en una página independiente.

## Arquitectura y alcance

Servicios de dominio dentro de una sola aplicación Spring Boot: controladores → servicios → repositorios JPA → MySQL. Es un monolito con capa de servicios, sin despliegues independientes. El sistema está orientado a una demostración local de administración y no implementa inicio de sesión ni pasarela bancaria; los pagos se registran y confirman administrativamente.

- docs/ARQUITECTURA.md: relación con los requisitos y criterios de reportes.
- docs/FRONTEND.md: diseño y navegación.
- docs/CONEXIONES.md: comprobaciones de conexiones.
- docs/README.md: ejemplos y orden de prueba de la API.

## Demostración recomendada

1. Crear un cliente de tipo PROPIETARIO y otro de tipo CLIENTE.
2. Registrar una propiedad vinculada al propietario.
3. Registrar una unidad dentro de la propiedad.
4. Crear una reserva para el arrendatario y la unidad.
5. Registrar un pago parcial y confirmarlo.
6. Verificar saldo, ingresos y ocupación en Reportes (usar reserva CONFIRMADA para ocupación).
7. Intentar otra reserva solapada para comprobar su rechazo.

Los intervalos de reportes incluyen desde y excluyen hasta. Sin un calendario de pagos, la deuda vence una vez pasada la fecha de entrada. Los saldos se calculan con los estados actuales; los detalles están en docs/ARQUITECTURA.md.
