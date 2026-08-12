# Diagramas C4 - PetCare (Sesión 8 - CQRS)

Rama: `sesion-8-cqrs`

## 📋 Resumen de Diagramas Creados

Todos los diagramas están creados en **PlantUML** y pueden visualizarse en:
- VS Code con extensión "PlantUML" 
- [PlantUML Online Editor](https://www.plantuml.com/plantuml/uml/)

---

## 🎯 C1 - Diagrama de Contexto

**Archivo:** `1-contexto.puml`

```
Personas:
├── Cliente (busca, reserva, paga servicios)
├── Proveedor (ofrece servicios, responde solicitudes)
└── Administrador (gestiona plataforma)

Sistema PetCare ↔ 
├── Pasarela de Pagos
├── Servidor SMTP
├── Google Maps
└── Kafka Broker
```

---

## 📦 C2 - Diagramas de Contenedores (6 servicios)

### Gateway Service (`2-contenedores-gateway-service.puml`)
```
Usuario HTTP
    ↓
API Gateway (8086)
├── JWT Validation
├── Rate Limiting
├── Request Logging
    ↓
Enruta a:
├── Backend (8080)
├── Reservation Service (8081)
├── Provider Service (8082)
├── Payment Service (8083)
└── Saga Orchestrator (8085)
```

### Backend Service (`2-contenedores-backend.puml`)
```
Backend (8080)
├── Auth (login, register, refresh)
├── Clientes & Mascotas
├── Catálogos
└── Notificaciones
    ↓
petcaredb
```

### Reservation Service con CQRS (`2-contenedores-reservation-cqrs.puml`)
```
POST/PUT → WRITE SIDE (ReservaCommandService)
           ├── Valida
           ├── Persiste (INSERT/UPDATE)
           └── Publica eventos

GET → READ SIDE (ReservaQueryService)
      ├── Consulta (SELECT)
      └── Mapea DTOs
      
↔ Kafka (comandos y eventos)
↔ petcaredb
```

### Payment Service (`2-contenedores-payment-service.puml`)
```
CrearPagoCommand (Kafka)
    ↓
Payment Service (8083)
├── Consulta promociones (Provider Service /interna/**)
├── Aplica DescuentoPipeline
│   ├── PipePromocionProveedor
│   ├── PipeRecargoModalidad
│   └── PipeTerminal
├── Procesa en Pasarela
├── Persiste pago
└── Publica PagoProcesadoEvent o PagoFallidoEvent
    ↓
petcaredb
```

### Provider Service (`2-contenedores-provider-service.puml`)
```
NotificarProveedorCommand (Kafka)
    ↓
Provider Service (8082)
├── Crea SolicitudReserva
├── Publica ReservaAceptadaEvent/ReservaRechazadaEvent
└── Expone /interna/** (promociones, costos, disponibilidad)
    ↓
petcaredb_provider (BD SEPARADA)
```

### Saga Orchestrator (`2-contenedores-saga-orchestrator.puml`)
```
Saga Orchestrator (8085) - Clean Architecture
├── Domain Layer (SagaOrquestadorService)
│   └── Máquina de estados
├── Application Layer (CommandPublishers)
│   ├── NotificarProveedorCommand
│   ├── ConfirmarReservaCommand
│   ├── CrearPagoCommand
│   ├── CancelarReservaCommand (compensación)
│   └── LiberarDescuentoCommand (compensación)
├── Infrastructure Layer (Kafka Listeners)
│   ├── ReservaEventosListener
│   └── PagoEventosListener
    ↓
petcaredb (saga_estado, evento_procesado)
```

---

## 🔧 C3 - Diagramas de Componentes

### Gateway Service (`3-componentes-gateway-service.puml`)
```
HTTP Request
    ↓
JwtAuthGlobalFilter (order -80)
├── Valida JWT Bearer
├── Extrae username
└── Permite public paths (/auth/login, /uploads/**, /actuator/**)
    ↓
RateLimiterGlobalFilter (order -90)
├── Token bucket por IP/cliente
└── Retorna 429 si excede límite
    ↓
RequestLoggingGlobalFilter (order -100)
├── Registra method, path, status, duration
    ↓
Routes Configuration (application.yml)
├── /api/v1/reservas/** → Reservation Service
├── /api/v1/proveedor/** → Provider Service
├── /api/v1/pagos/** → Payment Service
└── /api/v1/auth/** → Backend Auth
```

### Reservation Service CQRS (`3-componentes-reservation-service.puml`)
```
WRITE SIDE:
└── POST/PUT /reservas
    ├── ReservaCommandService
    ├── ReservaDatosValidator
    ├── ReservaCommandRepository (INSERT/UPDATE)
    └── ReservaNotifier (publica eventos)
        ↓ petcaredb

READ SIDE:
└── GET /reservas
    ├── ReservaQueryService
    ├── ReservaQueryRepository (SELECT)
    └── ReservaQueryMapper
        ↓ petcaredb

Kafka Integration:
├── ConfirmarRechazarCommandConsumer
├── CancelarReservaCommandConsumer
└── IdempotencyService (evento_procesado)
```

### Payment Service (`3-componentes-payment-service.puml`)
```
Kafka: saga.comando.crear-pago
    ↓
CrearPagoCommandConsumer
    ↓
PagoServiceImpl
├── DescuentoPipeline
│   ├── PipePromocionProveedor
│   ├── PipeRecargoModalidad
│   └── PipeTerminal
├── PagoProviderClient (GET /interna/**)
├── PasarelaClient (procesa pago)
└── KafkaDomainEventPublisher
    ↓
petcaredb (tabla pago)
    ↓
Kafka: pago.completado o pago.fallido
```

### Provider Service (`3-componentes-provider-service.puml`)
```
Kafka: saga.comando.notificar-proveedor
    ↓
NotificarProveedorCommandConsumer
    ↓
ProveedorServiceImpl
├── SolicitudReservaRepository (INSERT)
└── KafkaDomainEventPublisher
    ↓
petcaredb_provider

Internal API:
└── CatalogoInternoController (/interna/**)
    ├── Promociones activas
    ├── Costos adicionales
    └── Disponibilidades
        ↓ payment-service y reservation-service
```

---

## 📊 C4 - Diagramas de Secuencia

### Flujo de Pago (`4-secuencia-pago.puml`)

**Happy Path:**
```
Cliente POST /pagos/procesar
    ↓
Payment Service
├── Consulta promociones (Provider /interna/**)
├── Aplica DescuentoPipeline
├── Procesa en pasarela
├── INSERT pago (COMPLETADO)
└── Publica PagoProcesadoEvent
    ↓
Saga Orchestrator
└── UPDATE saga_estado (COMPLETADA)
    ↓
Response 200 OK
```

**Error Path (con Compensación):**
```
Pago falla
    ↓
Payment Service: INSERT pago (FALLIDO)
    ↓
Publica PagoFallidoEvent
    ↓
Saga Orchestrator
├── UPDATE saga_estado (PAGO_FALLIDO)
├── Publica CancelarReservaCommand
│   └── Reservation Service: UPDATE reserva (CANCELADA)
└── Publica LiberarDescuentoCommand
    └── Payment Service: UPDATE promocion (decrementa usos_actuales)
        ↓
Response con motivo PAGO_FALLIDO
```

### Flujo de Reserva (`4-secuencia-reserva.puml`)

```
1. Cliente: POST /reservas
   ↓
2. Reservation Service
   ├── INSERT reserva (PENDIENTE)
   └── Publica ReservaCreadaEvent
       ↓
3. Saga Orchestrator
   ├── INSERT saga_estado (INICIADA)
   └── Publica NotificarProveedorCommand
       ↓
4. Provider Service
   ├── INSERT solicitud_reserva (PENDIENTE)
   └── Publica ReservaAceptadaEvent
       ↓
5. Saga Orchestrator
   ├── UPDATE saga_estado (RESERVA_ACEPTADA)
   └── Publica ConfirmarReservaCommand
       ↓
6. Reservation Service
   ├── UPDATE reserva (CONFIRMADA)
   └── Publica ReservaConfirmadaEvent
       ↓
7. Saga Orchestrator
   ├── UPDATE saga_estado (PAGO_SOLICITADO)
   └── Publica CrearPagoCommand
       ↓
8. Payment Service
   (ver flujo de pago)
       ↓
9. Response: Reserva pendiente de pago
```

---

## 🗄️ Entidad-Relación (`5-entidad-relacion.puml`)

### petcaredb (BD Compartida)
```
PERSONA
├── nombre, apellidos, email, telefono
    ↓
USUARIO
├── username, password_hash
    ├→ CLIENTE (cliente_id)
    └→ PROVEEDOR (usuario_id, en petcaredb_provider)
        ↓
MASCOTA (cliente_id)
├── nombre, raza, especie, peso
    ↓
SERVICIO (catálogo global)
    ↓
RESERVA (cliente_id, mascota_id, servicio_id)
├── estado: PENDIENTE, CONFIRMADA, CANCELADA
├── fecha_servicio, hora_inicio, hora_fin
    ↓
PAGO (reserva_id, cliente_id)
├── estado: PENDIENTE, COMPLETADO, FALLIDO
├── monto_original, monto_descuento, monto_recargo, monto_final
    ↓
PROMOCION (servicio_id, proveedor_id nullable)
├── descuento_porcentaje, usos_totales, usos_actuales
    ↓
SAGA_ESTADO (reserva_id UNIQUE)
├── estado máquina de estados
├── paso_actual, motivo_fallo
    ↓
EVENTO_PROCESADO (event_id UNIQUE, idempotencia)
```

### petcaredb_provider (BD Separada para Provider)
```
PROVEEDOR (usuario_id FK)
├── empresa_nombre, empresa_ruc, experiencia_anios, rating
    ↓
PROVEEDOR_SERVICIO (proveedor_id, servicio_id)
├── precio_base
    ↓
PROVEEDOR_SERVICIO_MODALIDAD
├── costo_adicional
    ↓
DISPONIBILIDAD (proveedor_id, servicio_id)
├── dia_semana, hora_inicio, hora_fin
    ↓
SOLICITUD_RESERVA (proveedor_id, reserva_id)
├── estado: PENDIENTE, ACEPTADA, RECHAZADA
    ↓
PROMOCION (proveedor_id nullable)
├── descuento_porcentaje, usos_actuales
    ↓
EVENTO_PROCESADO (idempotencia)
```

---

## 🔄 Patrones CQRS Aplicados

| Servicio | WRITE | READ | Kafka | BD |
|----------|-------|------|-------|-----|
| **Reservation** | ReservaCommandService | ReservaQueryService | Comandos + Eventos | petcaredb |
| **Payment** | CrearPagoCommandConsumer | PagoController | Comando + Eventos | petcaredb |
| **Provider** | NotificarProveedorCommandConsumer | CatalogoInternoController | Comando + Eventos | petcaredb_provider |
| **Saga Orchestrator** | Publica comandos | Consume eventos | 6 topics | petcaredb |

---

## 📝 Documentación Adicional

Ver archivo: `CQRS_IMPLEMENTATION.md`

Contiene:
- Implementación detallada de CQRS en cada servicio
- Flujo completo de 8 pasos
- Tabla resumen de aplicación de CQRS
- Beneficios y patrones

---

## 🚀 Visualización

Para ver los diagramas:

1. **VS Code con PlantUML:**
   - Instalar extensión "PlantUML" (jebbs.plantuml)
   - Abrir archivo `.puml`
   - Click derecho → "Preview Current Diagram"

2. **Online:**
   - Copiar contenido del archivo `.puml`
   - Ir a https://www.plantuml.com/plantuml/uml/
   - Pegar contenido

3. **Exportar a imagen:**
   - VS Code: Click derecho → "Export Current Diagram as SVG/PNG"
   - O: `plantuml -Tpng archivo.puml`

---

**Última actualización:** Sesión 8 - CQRS  
**Rama:** sesion-8-cqrs  
**Estado:** ✅ Completo
