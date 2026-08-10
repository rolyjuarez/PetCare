# PETCare

PETCare es una plataforma web para la gestión de servicios de cuidado de mascotas, que incluye módulos de autenticación, clientes, proveedores, reservas, pagos, promociones, notificaciones y reportes. El proyecto está implementado como un backend Spring Boot (auth, catálogos y administración) junto a microservicios de reservas, proveedores, pagos y un orquestador de saga, con un frontend Angular y soporte para eventos asíncronos con Kafka.

## Visión general

El sistema está compuesto por:
- Frontend Angular para la experiencia de usuario web.
- Backend Spring Boot con módulos por dominio.
- PostgreSQL como base de datos principal.
- Kafka + Outbox para comunicación event-driven.
- Prometheus y Grafana para observabilidad.
- Docker Compose para levantar el entorno completo.

## Arquitectura actual

### Frontend
- Angular 21
- Tailwind CSS
- Servicios para consumir la API REST
- Gestión de reservas, pagos, mascotas, autenticación y perfiles

### Backend y microservicios
- Java 21 / Spring Boot 3.4.1
- `gateway-service` (8086): API Gateway (Spring Cloud Gateway). Punto de entrada único: valida JWT, aplica CORS y rate limiting, registra cada request y enruta por prefijo de ruta hacia backend y microservicios
- `backend` (8080): auth JWT, catálogos, clientes, mascotas, promociones y notificaciones; un `MicroserviceRoutingFilter` reenvía `/reservas**`, `/proveedor**` y `/pagos**` a los microservicios (fallback si se accede directo)
- `reservation-service` (8081): dueño de la reserva
- `provider-service` (8082): CRUD de proveedores, servicios propios, disponibilidad, promociones y solicitudes
- `payment-service` (8083): pagos con pipeline de descuentos
- `saga-orchestrator` (8085): máquina de estados y compensación centralizada
- Spring Web, Security, Validation, Mail, Actuator
- MyBatis para acceso a datos con mappers XML
- JWT para autenticación
- OpenAPI / Swagger para documentación de APIs

### Event-driven
- Eventos de dominio para reservas, pagos, usuarios y mascotas
- Saga orquestada con comandos `saga.comando.*` y topics de dominio `reserva.*`, `pago.*`
- Consumidores Kafka con idempotencia (evento_procesado) y ack manual
- Compensación centralizada en el orquestador (cancelar-reserva, liberar-descuento)
- Manejo de dead-letter (DLT `saga.dlt.v1`)

## Estructura del proyecto

```text
backend/           # Aplicación Spring Boot (auth, catálogos, administración)
frontend/          # Aplicación Angular
services/          # Microservicios (gateway, reservation, provider, payment, saga-orchestrator)
basededatos/       # Scripts SQL y datos iniciales
diagramas/         # Diagramas C4 / PlantUML (niveles 1, 2 y 3)
monitoring/        # Prometheus y Grafana
postman/           # Colecciones Postman
Docker Compose     # Orquestación del entorno
```

## Tecnologías principales

- Java 21
- Spring Boot 3.4.1
- PostgreSQL
- MyBatis
- Angular 21
- Kafka
- Docker / Docker Compose
- Prometheus / Grafana
- Tailwind CSS

## Módulos principales del backend

- Auth
- Persona / Cliente / Usuario
- Mascota
- Proveedor / Servicio / Disponibilidad
- Reserva
- Pago
- Promoción (globales y del proveedor)
- Notificación
- Menu / Rol / Permiso
- Sucursal / Bitácora / Vacuna

Los módulos de Reserva, Proveedor, Pago y la saga viven en `services/`; el resto
se mantiene en `backend/`.

## Ejecución con Docker Compose

Requisitos:
- Docker
- Docker Compose

Comandos:

```bash
docker compose up --build
```

Servicios expuestos:
- Frontend: http://localhost:4200
- API Gateway: http://localhost:8086
- Backend: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
- reservation-service: http://localhost:8081
- provider-service: http://localhost:8082
- payment-service: http://localhost:8083
- saga-orchestrator: http://localhost:8085
- PgAdmin: http://localhost:5050
- Kafka: localhost:9094
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

## API Gateway (gateway-service)

El gateway es el punto de entrada único de la API. Corre en el puerto **8086** y
enruta según el prefijo de la ruta:

| Prefijo de ruta (en `/api/v1`) | Destino |
| --- | --- |
| `/reservas/**` | reservation-service (8081) |
| `/proveedor/**`, `/proveedores/**` | provider-service (8082) |
| `/pagos/reserva/**`, `/pagos/*/procesar`, `/pagos/*/reembolsar` | payment-service (8083) |
| cualquier otra ruta `/api/v1/**`, `/uploads/**` | backend (8080) |

Funciones que agrega:
- **JWT**: valida el token en rutas protegidas y responde `401` si es inválido o expiró; las rutas públicas (`/auth/login`, `/auth/register`, `/auth/forgot-password`, `/auth/reset-password`, `/auth/refresh`, `/uploads/**`, `/actuator/**`) no exigen token.
- **CORS**: permite el frontend Angular (`http://localhost:4200`).
- **Rate limiting**: bucket por cliente (usa `X-Forwarded-For`), responde `429` al superar el límite.
- **Logging**: registra método, ruta, status y duración de cada request.
- **Errores**: responde `502` si el servicio destino está caído y `404` para rutas no mapeadas, siempre con cuerpo JSON.

### Ejecución local (sin Docker)

```bash
cd services/gateway-service
mvn spring-boot:run
```

Por defecto enruta hacia `localhost:8080/8081/8082/8083`. Si los servicios
corren en otros hosts/URLs, se sobreescribe con variables de entorno:
`BACKEND_SERVICE_URL`, `RESERVATION_SERVICE_URL`, `PROVIDER_SERVICE_URL`, `PAYMENT_SERVICE_URL`.

### Frontend apuntando al gateway

- **Docker**: el `nginx.conf` del frontend ya hace `proxy_pass http://gateway-service:8086`.
- **Local (Angular dev server)**: `frontend/proxy.conf.json` apunta a `http://localhost:8086`, por lo que el gateway debe estar corriendo junto a los demás servicios:

```bash
# Terminal 1: backend y microservicios (Maven o Docker)
# Terminal 2: gateway
cd services/gateway-service
mvn spring-boot:run
# Terminal 3: frontend
cd frontend
npm start
```

Los tests del gateway se corren con:

```bash
cd services/gateway-service
mvn test
```

## Variables de entorno

Algunas configuraciones del backend pueden venir desde variables de entorno, por ejemplo:

```bash
JWT_SECRET=...
MAIL_USERNAME=...
MAIL_PASSWORD=...
```

## Diagramas disponibles

Los diagramas de arquitectura (PlantUML) se encuentran en la carpeta `diagramas`:

- 1-contexto.puml — vista de contexto de la plataforma (C4 Nivel 1)
- 2-contenedores.puml — vista de contenedores (C4 Nivel 2)
- 3-componentes.puml — vista global de componentes de la saga orquestada (C4 Nivel 3)
- 3-componentes-saga-orchestrator.puml — componentes internos del orquestador (C4 Nivel 3)
- 3-componentes-reservation-service.puml — componentes internos de reservation-service (C4 Nivel 3)
- 3-componentes-provider-service.puml — componentes internos de provider-service (C4 Nivel 3)
- 3-componentes-payment-service.puml — componentes internos de payment-service (C4 Nivel 3)
- petcaredb.puml — esquema de base de datos de petcaredb (ERD)
- petcaredb_provider.puml — esquema de base de datos de petcaredb_provider (ERD)

Los diagramas C4 usan la librería C4-PlantUML mediante `!includeurl`, por lo
que renderizan en cualquier visor de PlantUML (VSCode, plantuml.com, etc.).

Para el detalle del saga, ver `services/README-saga-pagos.md`.

## Estado actual del proyecto

El proyecto ya incorpora:
- API REST modular (API Gateway + backend + 4 microservicios)
- API Gateway como punto de entrada único (JWT, CORS, rate limiting y logging)
- Autenticación con JWT
- Persistencia relacional con PostgreSQL (base compartida)
- Saga orquestada de reservas con compensación centralizada
- Integración con Kafka para eventos asíncronos y comandos de la saga
- Pipeline de descuentos en pagos (promociones y modalidades)
- Idempotencia (evento_procesado) y dead-letter (saga.dlt.v1)
- Observabilidad con métricas y logs
- Contenedorización con Docker

