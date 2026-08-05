# PETCare

PETCare es una plataforma web para la gestión de servicios de cuidado de mascotas, que incluye módulos de autenticación, clientes, proveedores, reservas, pagos, promociones, notificaciones y reportes. El proyecto combina un monolito Spring Boot (autenticación, catálogos y API gateway) con microservicios de reservas, proveedores y pagos, un frontend Angular y comunicación asíncrona basada en eventos con Kafka.

## Visión general

El sistema está compuesto por:
- Frontend Angular para la experiencia de usuario web.
- Backend Spring Boot monolito: autenticación JWT, catálogos y API gateway.
- Microservicios: `reservation-service`, `provider-service` y `payment-service`.
- PostgreSQL como base de datos principal (única, compartida por todos los servicios).
- Kafka + Outbox para comunicación event-driven.
- Prometheus y Grafana para observabilidad.
- Docker Compose para levantar el entorno completo.

## Arquitectura actual

### Frontend
- Angular 21
- Tailwind CSS
- Servicios para consumir la API REST
- Gestión de reservas, pagos, mascotas, autenticación y perfiles

### Backend
- Java 21
- Spring Boot 3.4.1
- Spring Web, Security, Validation, Mail, Actuator
- MyBatis para acceso a datos con mappers XML
- JWT para autenticación
- OpenAPI / Swagger para documentación de APIs
- Arquitectura modular por paquetes y dominios

### Event-driven

Los servicios se comunican mediante eventos de dominio en Kafka. Cada microservicio es productor y/o consumidor según el evento:

| Evento | Productor | Topic | Consumidor |
|---|---|---|---|
| `ReservaCreadaEvent` | reservation-service | `reserva.creada` | provider-service (crea la solicitud) |
| `ReservaAceptadaEvent` | provider-service | `reserva.aceptada` | reservation-service (reserva → CONFIRMADA) |
| `ReservaRechazadaEvent` | provider-service | `reserva.rechazada` | reservation-service (reserva → RECHAZADA) |
| `ReservaConfirmadaEvent` | reservation-service | `reserva.confirmada` | payment-service (crea y procesa el pago) |
| `PagoProcesadoEvent` | payment-service | `pago.completado` | — |
| Dead-letter de fallos | — | `reserva.dlt.v1` | — |

Detalles:
- Publicación vía `DomainEventPublisher` (Kafka con patrón Outbox en el monolito).
- Consumers Kafka con idempotencia (tabla `evento_procesado`) y dead-letter handling.
- Retención de topics: 7 días; 3 particiones, factor de replicación 1.

## Estructura del proyecto

```text
backend/           # Aplicación Spring Boot (monolito / API gateway)
frontend/          # Aplicación Angular
services/          # Microservicios (reservation, provider, payment)
basededatos/       # Scripts SQL y datos iniciales
diagramas/         # Diagramas C4 / PlantUML
monitoring/        # Prometheus y Grafana
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
- Proveedor / Servicio
- Reserva
- Pago
- Promoción
- Notificación
- Menu / Rol / Permiso
- Sucursal / Bitácora / Vacuna

## Microservicios

| Servicio | Puerto | Responsabilidad | Endpoints principales |
|---|---|---|---|
| `reservation-service` | 8081 | Reservas, disponibilidad, slots y estados | `/api/v1/reservas` |
| `provider-service` | 8082 | Solicitudes de reserva del proveedor | `/api/v1/proveedor/solicitudes` |
| `payment-service` | 8083 | Pagos, descuentos y reembolsos | `/api/v1/pagos` |

El backend monolito (8080) actúa como API gateway: `MicroserviceRoutingFilter` reenvía
`/api/v1/reservas/*` a `reservation-service`, `/api/v1/proveedor/*` a `provider-service`
y `/api/v1/pagos/*` a `payment-service`. Los microservicios **no se llaman por REST entre
sí**: se comunican mediante eventos asíncronos en Kafka.

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
- Backend: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
- reservation-service: http://localhost:8081
- provider-service: http://localhost:8082
- payment-service: http://localhost:8083
- PgAdmin: http://localhost:5050
- Kafka (listener host): localhost:9094
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Kafdrop (UI de Kafka, opcional): http://localhost:9000

## Variables de entorno

Algunas configuraciones del backend pueden venir desde variables de entorno, por ejemplo:

```bash
JWT_SECRET=...
MAIL_USERNAME=...
MAIL_PASSWORD=...
```

## Diagramas disponibles

Los diagramas de arquitectura (notación C4, PlantUML) se encuentran en la carpeta `diagramas`:

- 1-contexto.puml — vista general de contexto (usuarios, contenedores y sistemas externos)
- 1-contexto-backend.puml — contexto del backend monolito / API gateway
- 1-contexto-reservation-service.puml — contexto de reservation-service
- 1-contexto-provider-service.puml — contexto de provider-service
- 1-contexto-payment-service.puml — contexto de payment-service
- 2-contenedores.puml — vista de contenedores de toda la plataforma
- 3-event-driven-reservas.puml — flujo de eventos del ciclo de vida de una reserva
- 3-event-driven-pagos.puml — flujo de eventos del ciclo de pago

Los diagramas usan la librería C4-PlantUML mediante `!includeurl` (apuntando a
raw.githubusercontent.com), por lo que el código es compatible con cualquier visor
de PlantUML (extensión de VSCode, plantuml.com, servidores online, etc.).

## Estado actual del proyecto

El proyecto ya incorpora:
- API REST modular
- Autenticación con JWT
- Persistencia relacional con PostgreSQL
- Integración con Kafka para eventos asíncronos (reservas y pagos)
- Microservicios de reservas, proveedores y pagos con API gateway
- Outbox para publicación confiable
- Observabilidad con métricas y logs
- Contenedorización con Docker

