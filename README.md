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
- `backend` (8080): auth JWT, catálogos, clientes, mascotas, promociones y notificaciones; un `MicroserviceRoutingFilter` reenvía `/reservas**`, `/proveedor**` y `/pagos**` a los microservicios
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
services/          # Microservicios (reservation, provider, payment, saga-orchestrator)
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
- API REST modular (backend + 4 microservicios)
- Autenticación con JWT
- Persistencia relacional con PostgreSQL (base compartida)
- Saga orquestada de reservas con compensación centralizada
- Integración con Kafka para eventos asíncronos y comandos de la saga
- Pipeline de descuentos en pagos (promociones y modalidades)
- Idempotencia (evento_procesado) y dead-letter (saga.dlt.v1)
- Observabilidad con métricas y logs
- Contenedorización con Docker

