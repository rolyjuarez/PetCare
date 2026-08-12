# CQRS en PetCare

## ¿Dónde se aplica CQRS?

CQRS (Command Query Responsibility Segregation) se aplica en el proyecto PetCare principalmente en **reservation-service**, y parcialmente en **payment-service** y **provider-service**.

---

## 1️⃣ Aplicación COMPLETA en `reservation-service`

### Separación de responsabilidades

#### **WRITE SIDE (Lado Escritura - Comandos)**

```
services/reservation-service/src/main/java/bo/capital/tec/pet/modules/reserva/application/command/
├── ReservaCommandUseCase.java          # Interfaz de casos de uso (CQRS write side)
├── ReservaCommandService.java          # Implementación: maneja CrearReservaCommand, etc.
├── CrearReservaCommand.java            # Comando: crear reserva
├── ActualizarReservaCommand.java       # Comando: actualizar reserva
├── CancelarReservaCommand.java         # Comando: cancelar reserva (compensación)
├── ConfirmarReservaCommand.java        # Comando: confirmar reserva
├── RechazarReservaCommand.java         # Comando: rechazar reserva
├── ReservaDatosValidator.java          # Valida los datos de entrada
└── ReservaNotifier.java                # Publica eventos de dominio

Puertos de salida WRITE:
└── domain/port/out/ReservaCommandRepository   # Interfaz de persistencia (WRITE)
```

**Flujo de escritura:**
```
Controller (HTTP/Kafka)
  → ReservaCommandService
    → Valida datos (ReservaDatosValidator)
    → Aplica lógica de negocio
    → Persiste en BD (ReservaCommandRepository)
    → Publica eventos de dominio (ReservaNotifier)
```

#### **READ SIDE (Lado Lectura - Queries)**

```
services/reservation-service/src/main/java/bo/capital/tec/pet/modules/reserva/application/query/
├── ReservaQueryService.java            # Interfaz de casos de uso (CQRS read side)
├── ReservaQueryServiceImpl.java         # Implementación: consultas de lectura
└── ReservaQueryMapper.java             # Mapea DTOs de lectura

Puertos de salida READ:
└── domain/port/out/ReservaQueryRepository    # Interfaz de persistencia (READ)
```

**Flujo de lectura:**
```
Controller (GET /reservas/{id})
  → ReservaQueryService
    → Consulta BD (ReservaQueryRepository - SOLO LECTURA)
    → Mapea a DTO de respuesta
    → Devuelve el dato
```

### Diagrama de separación CQRS en reservation-service

```
┌─────────────────────────────────────────────────────────────┐
│                    HTTP Controllers                         │
├──────────────────┬──────────────────────────────────────────┤
│                  │                                          │
│  ReservaCommand  │        ReservaQuery                     │
│  Controller      │        Controller                        │
│  (POST/PUT)      │        (GET)                            │
│                  │                                          │
└──────┬───────────┴──────────────┬──────────────────────────┘
       │                          │
       │ WRITE SIDE              │ READ SIDE
       ↓                          ↓
┌──────────────────┐      ┌──────────────────┐
│ ReservaCommand   │      │ ReservaQuery     │
│ Service          │      │ Service          │
│ (casos de uso    │      │ (casos de uso    │
│  de escritura)   │      │  de lectura)     │
└────────┬─────────┘      └────────┬─────────┘
         │                         │
         ↓                         ↓
┌──────────────────┐      ┌──────────────────┐
│ ReservaCommand   │      │ ReservaQuery     │
│ Repository       │      │ Repository       │
│ (insert/update)  │      │ (select only)    │
└────────┬─────────┘      └────────┬─────────┘
         │                         │
         └──────────┬──────────────┘
                    │
                    ↓
            ┌───────────────┐
            │ PostgreSQL    │
            │ petcaredb     │
            │ tabla:reserva │
            └───────────────┘
```

---

## 2️⃣ Aplicación PARCIAL en `payment-service`

### Separación entre Comandos y Eventos

**WRITE SIDE:**
- `CrearPagoCommand` → consume del topic `saga.comando.crear-pago`
- `CrearPagoCommandConsumer` → procesa el comando y crea un pago
- `PagoServiceImpl` → aplica lógica de descuentos y procesa el pago

**READ SIDE / EVENTS:**
- Publica eventos: `PagoProcesadoEvent`, `PagoFallidoEvent`, `PagoReembolsadoEvent`
- Consume estos eventos en `saga-orchestrator`

```
Orquestador (WRITE)
  ↓ CrearPagoCommand
Kafka topic: saga.comando.crear-pago
  ↓
CrearPagoCommandConsumer (WRITE)
  ↓
PagoService (lógica de descuentos y transacción)
  ↓
PagoProcesadoEvent (READ event para saga)
Kafka topic: pago.completado
  ↓
saga-orchestrator (lee el evento)
```

---

## 3️⃣ Aplicación PARCIAL en `provider-service`

### Separación entre Comandos y Eventos

**WRITE SIDE:**
- `NotificarProveedorCommand` → consume del topic `saga.comando.notificar-proveedor`
- `NotificarProveedorCommandConsumer` → procesa el comando y crea solicitudes de reserva
- `ProveedorServiceImpl` → crea `SolicitudReserva`

**READ SIDE / EVENTS:**
- Publica eventos: `ReservaAceptadaEvent`, `ReservaRechazadaEvent`
- Estos eventos son consumidos por `saga-orchestrator`

```
Orquestador (WRITE)
  ↓ NotificarProveedorCommand
Kafka topic: saga.comando.notificar-proveedor
  ↓
NotificarProveedorCommandConsumer (WRITE)
  ↓
ProveedorService (crea solicitudes de reserva)
  ↓
ReservaAceptadaEvent (READ event para saga)
Kafka topic: reserva.aceptada
  ↓
saga-orchestrator (lee el evento)
```

---

## 4️⃣ CQRS en `saga-orchestrator`

El orquestador **COORDINA** CQRS entre microservicios:

### Patrón de orquestación

```
┌────────────────────────────────────────────────────────────┐
│           SAGA ORCHESTRATOR (CQRS COORDINATOR)             │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  WRITE SIDE (Comandos):                                  │
│  └── Publica NotificarProveedorCommand                   │
│  └── Publica CrearPagoCommand                            │
│  └── Publica ConfirmarReservaCommand                     │
│  └── Publica CancelarReservaCommand (compensación)       │
│  └── Publica LiberarDescuentoCommand                     │
│                                                            │
│  READ SIDE (Eventos de dominio):                          │
│  └── Consume ReservaCreadaEvent                          │
│  └── Consume ReservaAceptadaEvent                        │
│  └── Consume ReservaConfirmadaEvent                      │
│  └── Consume PagoProcesadoEvent                          │
│  └── Consume PagoFallidoEvent                            │
│  └── Consume PagoReembolsadoEvent                        │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

---

## 📊 Flujo completo CQRS + SAGA

```
1. WRITE en reservation-service:
   POST /api/v1/reservas
   → CrearReservaCommand
   → ReservaCommandService
   → Persiste Reserva (WRITE)
   → Publica ReservaCreadaEvent

2. READ en saga-orchestrator:
   Consume ReservaCreadaEvent
   → Lee el evento
   → Máquina de estados INICIADA → PROVEEDOR_NOTIFICADO
   → Publica NotificarProveedorCommand (WRITE)

3. WRITE en provider-service:
   Consume NotificarProveedorCommand
   → CrearSolicitudesReserva
   → Persiste solicitud_reserva (WRITE)
   → Publica ReservaAceptadaEvent

4. READ en saga-orchestrator:
   Consume ReservaAceptadaEvent
   → Lee el evento
   → Máquina de estados PROVEEDOR_NOTIFICADO → RESERVA_ACEPTADA
   → Publica ConfirmarReservaCommand (WRITE)

5. WRITE en reservation-service:
   Consume ConfirmarReservaCommand
   → ConfirmarRechazarCommandConsumer
   → Persiste Reserva estado CONFIRMADA (WRITE)
   → Publica ReservaConfirmadaEvent

6. READ en saga-orchestrator:
   Consume ReservaConfirmadaEvent
   → Lee el evento
   → Máquina de estados RESERVA_ACEPTADA → PAGO_SOLICITADO
   → Publica CrearPagoCommand (WRITE)

7. WRITE en payment-service:
   Consume CrearPagoCommand
   → Calcula descuentos (consulta provider-service)
   → Procesa pago
   → Publica PagoProcesadoEvent (READ para saga)

8. READ en saga-orchestrator:
   Consume PagoProcesadoEvent
   → Lee el evento
   → Máquina de estados PAGO_SOLICITADO → COMPLETADA
   → FIN de la saga

   ❌ Si falla (PagoFallidoEvent):
   → Máquina de estados PAGO_SOLICITADO → PAGO_FALLIDO
   → Publica CancelarReservaCommand (WRITE de compensación)
   → Publica LiberarDescuentoCommand (WRITE de compensación)
```

---

## 🎯 Resumen: Dónde aplica CQRS

| Servicio | WRITE (Comandos) | READ (Queries/Events) | Tipo |
|----------|------------------|-----------------------|------|
| **reservation-service** | ReservaCommandService | ReservaQueryService | ✅ COMPLETO |
| **payment-service** | CrearPagoCommand Consumer | PagoProcesadoEvent | ✅ Parcial |
| **provider-service** | NotificarProveedor Consumer | ReservaAceptadaEvent | ✅ Parcial |
| **saga-orchestrator** | Publica comandos | Consume eventos | ✅ Coordinador |

---

## 💡 Beneficios de CQRS en PetCare

1. **Separación de responsabilidades**: lectura ≠ escritura
2. **Escalabilidad**: la lectura se puede escalar independientemente
3. **Idempotencia**: cada comando se procesa una sola vez (tabla `evento_procesado`)
4. **Event sourcing**: los eventos son la fuente de verdad para la saga
5. **Compensación centralizada**: la saga coordina los comandos de compensación cuando algo falla
