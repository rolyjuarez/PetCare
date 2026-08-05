# PETCare

PETCare es una plataforma web para la gestión de servicios de cuidado de mascotas, que incluye módulos de autenticación, clientes, proveedores, reservas, pagos, promociones, notificaciones y reportes. El proyecto está implementado como un monolito modular en backend, con un frontend Angular y soporte para eventos asíncronos con Kafka.

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

### Backend
- Java 21
- Spring Boot 3.4.1
- Spring Web, Security, Validation, Mail, Actuator
- MyBatis para acceso a datos con mappers XML
- JWT para autenticación
- OpenAPI / Swagger para documentación de APIs
- Arquitectura modular por paquetes y dominios

### Event-driven
- Eventos de dominio para reservas, pagos, usuarios y mascotas
- Publicación vía DomainEventPublisher
- Modo Kafka con patrón Outbox para confiabilidad
- Consumers Kafka para generar notificaciones y acciones reactivas
- Manejo de idempotencia y dead-letter handling

## Estructura del proyecto

```text
backend/           # Aplicación Spring Boot
frontend/          # Aplicación Angular
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
- PgAdmin: http://localhost:5050
- Kafka: localhost:9093
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

- 1-contexto.puml — vista de contexto de la plataforma
- 2-contenedores.puml — vista de contenedores
- 3-componentes.puml — vista de componentes
- 3-event-driven-reservas.puml — saga coreografiada de reservas (eventos, topics, productores y consumidores)
- 3-event-driven-pagos.puml — saga coreografiada de pagos (eventos, topics, productores y consumidores)

Los diagramas de eventos usan la librería C4-PlantUML mediante `!includeurl`, por lo
que renderizan en cualquier visor de PlantUML (VSCode, plantuml.com, etc.).

Para el detalle del saga, ver `services/README-saga-pagos.md`.

## Estado actual del proyecto

El proyecto ya incorpora:
- API REST modular
- Autenticación con JWT
- Persistencia relacional con PostgreSQL
- Integración con Kafka para eventos asíncronos
- Outbox para publicación confiable
- Observabilidad con métricas y logs
- Contenedorización con Docker

