# Verificación de conexiones

Verificación realizada sobre la aplicación ejecutándose en http://localhost:8000 y la base MySQL apartamentos en localhost:3306.

- 28 rutas HTTP locales verificadas con respuesta 200: panel, catálogos, formularios, API, reportes, CSS y JavaScript.
- Estado de salud de la aplicación: UP.
- Destinos POST de los formularios renderizados: coinciden con las rutas del controlador. El filtro GET de reportes se envía a su misma página.
- Bootstrap CSS y JavaScript se incluyen localmente y responden 200; ya no dependen de CDN.
- Pruebas ConnectionsIntegrationTest: 2 aprobadas con MySQL real y ddl-auto=validate.
- Relaciones comprobadas: propietario–propiedad, propiedad–unidad, cliente/unidad–reserva, reserva/cliente–pago.
- Flujo probado: formulario de reserva → persistencia → formulario de pago → confirmación → estado de cuenta → reembolso → actualización de saldo.
- Bloqueos de disponibilidad: impiden reservas en el intervalo bloqueado.
- Los datos temporales de las pruebas se revirtieron con @Transactional; se verificó que no quedaran registros temporales. Los contadores AUTO_INCREMENT pueden avanzar aun con rollback.

Las pruebas de conexión no requieren cambiar las rutas ni la configuración de producción. El test permanece en src/test/java/com/api/apartamentos/integration/ConnectionsIntegrationTest.java y requiere MySQL disponible.

```powershell
./mvnw.cmd test '-Dtest=ConnectionsIntegrationTest' '-Dspring.jpa.hibernate.ddl-auto=validate'
```
