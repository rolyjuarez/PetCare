# Saga orquestada: Reserva, Proveedor y Pago

Saga **orquestada** (patrón *orchestrator*) coordinada por el servicio
`saga-orchestrator` (8085) mediante Kafka. El orquestador mantiene la máquina de
estados de cada reserva y emite **comandos** hacia los microservicios; cuando un
paso falla, es el orquestador quien ordena la **compensación** de forma
centralizada.

## Arquitectura del orquestador (Clean Architecture)

```text
saga-orchestrator
├── domain/model           eventos y comandos de dominio (POJOs)
├── domain/port/in         SagaCoordinator (caso de uso)
├── domain/port/out        SagaEstadoRepository, SagaCommandPublisher,
│                          EventoProcesadoRepository
├── application/service    SagaOrquestadorService (máquina de estados),
│                          IdempotenciaSagaService
└── infrastructure
    ├── adapter/in/kafka   listeners de eventos de dominio
    ├── adapter/out/kafka  publicador de comandos
    ├── adapter/out/persistence  mappers MyBatis (saga_estado, evento_procesado)
    └── config             configs de Kafka (producer, consumer, topics, DLT)
```

Las dependencias apuntan hacia adentro: `infrastructure` implementa los puertos
de `domain` y `application` usa esos puertos, sin acoplamiento a Kafka/MyBatis.

## Flujo feliz

```text
reservation-service            saga-orchestrator (8085)            provider-service / payment-service
      |                               |                                   |
      |-- ReservaCreadaEvent --------->|                                   |
      |     (reserva.creada)           |-- NotificarProveedorCommand ----->| provider crea solicitudes
      |                               |     (saga.comando.notificar-proveedor)
      |<------------------------------------ ReservaAceptadaEvent ---------| proveedor acepta
      |                               |-- ConfirmarReservaCommand ------->| reservation marca CONFIRMADA
      |                               |     (saga.comando.confirmar-reserva)
      |-- ReservaConfirmadaEvent ----->|                                   |
      |     (reserva.confirmada)       |-- CrearPagoCommand -------------->| payment crea pago PENDIENTE
      |                               |     (saga.comando.crear-pago)
      |<------------------------------------ PagoProcesadoEvent -----------| pago COMPLETADO
      |                               |  saga -> COMPLETADA
```

Topics de eventos: `reserva.creada`, `reserva.aceptada`, `reserva.rechazada`,
`reserva.confirmada`, `reserva.cancelada`, `pago.completado`, `pago.fallido`,
`pago.reembolsado`.

Topics de comandos: `saga.comando.notificar-proveedor`,
`saga.comando.confirmar-reserva`, `saga.comando.rechazar-reserva`,
`saga.comando.crear-pago`, `saga.comando.cancelar-reserva`.

## Compensación centralizada

Cuando el pago falla, **el orquestador** es quien decide la compensación y la
ordena con un comando; `reservation-service` ya no reacciona a eventos de pago
por su cuenta:

```text
payment-service -> PagoFallidoEvent (pago.fallido)
       -> saga-orchestrator: saga PAGO_FALLIDO
       -> CancelarReservaCommand (saga.comando.cancelar-reserva)
       -> reservation-service: reserva CANCELADA + ReservaCanceladaEvent
       -> saga-orchestrator: saga CANCELADA
```

El proveedor también puede rechazar: `ReservaRechazadaEvent` -> el orquestador
emite `RechazarReservaCommand` -> la reserva queda RECHAZADA y la saga termina en
`RECHAZADA`.

## Máquina de estados (tabla `saga_estado`)

Por cada reserva se registra una fila en `saga_estado` con su `reserva_id`,
`estado`, `paso_actual` y `motivo`. Estados: `INICIADA`, `PROVEEDOR_NOTIFICADO`,
`RESERVA_ACEPTADA`, `RESERVA_CONFIRMADA`, `PAGO_SOLICITADO`, `PAGO_FALLIDO`,
`COMPLETADA`, `PAGO_REEMBOLSADO`, `RECHAZADA`, `CANCELADA`.

## Confiabilidad

- **Idempotencia**: el orquestador registra `event_id` en `evento_procesado`
  (service = `saga-orchestrator`) y cada microservicio registra el
  `command_id` antes de ejecutar un comando; los duplicados se ignoran.
- **Ack manual**: todos los listeners usan `MANUAL_IMMEDIATE`; errores no
  recuperables van al DLT (`saga.dlt.v1`) tras reintentos con backoff fijo.
- **Transacciones locales**: cada paso de la saga modifica su base en un
  `@Transactional` propio; nunca hay transacciones distribuidas.
- **Publicación**: `KafkaSagaCommandPublisher` resuelve el topic de comandos
  según el tipo y publica con `acks=all` e idempotencia de productor.

## Estrategia de datos

Hoy **todos los microservicios comparten la misma base PostgreSQL**
(`petcaredb`). La saga funciona porque cada servicio toca sus propias tablas
(`pago`, `reserva`, `solicitud_reserva`, `saga_estado`, `evento_procesado`),
pero un despliegue multi-base (una BD por servicio) permitiría aislar dominios.

Mientras no se migre, **no** se debe crear una BD separada: la saga y los tests
asumen la base compartida actual. La migración `basededatos/11_saga_orquestada.sql`
crea la tabla `saga_estado`.

## Tests

- `payment-service`: `PagoFlowIntegrationTest` cubre pago pendiente, procesar,
  rechazo de tarjeta (`PagoFallidoEvent`), reembolso (`PagoReembolsadoEvent`) y
  consumo de `CrearPagoCommand` con duplicados.
- `reservation-service`: `ReservaFlowIntegrationTest` cubre compensación vía
  `CancelarReservaCommand`, confirmación/rechazo vía comandos del orquestador y
  duplicados.
- `provider-service`: `ProveedorFlowIntegrationTest` cubre creación de
  solicitudes al recibir `NotificarProveedorCommand`, aceptar/rechazar
  (`ReservaAceptadaEvent`/`ReservaRechazadaEvent`) y duplicados.
