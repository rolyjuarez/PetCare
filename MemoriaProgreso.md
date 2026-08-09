# Memoria de Progreso — provider-service

Sesión: CRUD completo de proveedores, disponibilidad, servicios propios y promociones globales.

## Lo completado

### Proveedor CRUD (persona/usuario/dirección/especialidades)
- Alta completa en una sola operación: `POST /api/v1/proveedores/full` (crea persona, usuario, dirección y proveedor).
- Actualización simple y completa (`PUT /proveedores/{id}`, `PUT /proveedores/{id}/full`).
- `GET /proveedores/my` (proveedor autenticado), `GET /proveedores/{id}`, listado paginado `GET /proveedores`.
- Especialidades por proveedor: `PUT /proveedores/{id}/especialidad/{servicioId}` (configura `requiereCertificado`).
- Soft delete con validación de reservas: `GET /proveedores/{id}/has-reservas`, `DELETE /proveedores/{id}`.
- Fix: `ProveedorCrudServiceImpl` siempre inserta `requiereCertificado=false` al crear especialidades (create y full-update), acorde a la columna `NOT NULL` de producción.

### Disponibilidad
- CRUD completo en `/api/v1/disponibilidades` (con filtros `proveedorId`/`servicioId`, consultas `by-proveedor-servicio`, `by-servicio`, soft delete).
- Seguridad: lecturas accesibles a CLIENTE/ADMIN/PROVEEDOR; escrituras solo PROVEEDOR.

### Servicios propios del proveedor
- `POST/GET/PUT/DELETE /proveedores/my/servicios`, activación `PUT /proveedores/my/servicios/{id}/activo`.
- Soporta modalidades (`proveedor_servicio_modalidad` con `costo_adicional` y `activo`).
- Catálogo público: `GET /proveedores/{id}/servicios` (solo activos).

### Promociones
- Globales (Admin): CRUD en `/api/v1/promociones`, activas en `/promociones/active`.
- Propias del proveedor: `/proveedores/my/promociones` (CRUD + `notificar`).
- `PromocionMapper.selectActive` filtrado a globales (`p.proveedor_id IS NULL`).
- Descuentos: `/proveedores/{id}/descuentos` y `/proveedores/{id}/descuentos/activos`.

### H2 (test)
- `schema.sql` con `ciudad`, `estado`, `direccion`, `proveedor_servicio_modalidad`, `disponibilidad` (con `activo`), `usuario` (`ultimo_acceso`, `token_refresh`), `promocion.proveedor_id` nullable.
- `data.sql` idempotente (`DELETE` + IDs explícitos + `RESTART WITH 100`) por la compartición del H2 entre contextos Spring.
- Tests: `ProveedorCrudIntegrationTest`, `DisponibilidadIntegrationTest`, `ProveedorServicioIntegrationTest`; ampliado `PromocionFlowIntegrationTest` (10 casos globales/propias) y `@Sql` en `ProveedorFlowIntegrationTest`.
- Resultado: `mvn -o test` → 41 tests, BUILD SUCCESS.

## Endpoints internos (`/api/v1/interna/**`)
- Proveedor, servicio, modalidades, valida modalidad, costo adicional, requiere-certificado, disponibilidades, promoción activa, incrementar/decrementar usos. PermitAll.

## Postman
- `postman/provider-service.postman_collection.json` (v2.1): 7 carpetas, 48 requests. Validado el JSON.

## Pendientes / próximo paso
- (ninguno pendiente de esta sesión)
