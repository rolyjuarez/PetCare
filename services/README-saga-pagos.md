# Saga coreografiada: Reserva y Pago

Saga **coreografiada** (sin orquestador) entre `reservation-service` (8081) y
`payment-service` (8083) usando Kafka. Cada servicio publica/consume eventos de
dominio y compensa sus propias transacciones locales cuando el flujo falla.

## Flujo feliz

```text
reservation-service                    payment-service
      |                                      |
      |-- ReservaCreadaEvent --------------->|  (provider responde)
      |<---- ReservaAceptadaEvent -----------|
      |  (marca CONFIRMADA)                  |
      |-- ReservaConfirmadaEvent ----------->|  crea pago PENDIENTE
      |                                      |  procesar() via pasarela
      |<---- PagoProcesadoEvent -------------|  pago COMPLETADO
```

Topics: `reserva.creada`, `reserva.aceptada`, `reserva.rechazada`,
`reserva.confirmada`, `pago.completado`.

## Compensación: pago fallido

```text
payment-service                               reservation-service
      |                                             |
      |  procesar() -> pasarela rechaza             |
      |-- PagoFallidoEvent ------------------------>|
      |        (topic `pago.fallido`)               |  cancela la reserva (CANCELADA)
      |                                             |-- ReservaCanceladaEvent (compensación)
      |        (topic `reserva.cancelada`)          |
```

Si el pago se completó y luego se reembolsa, `payment-service` publica
`PagoReembolsadoEvent` en el topic `pago.reembolsado`.

## Confiabilidad

- **Idempotencia**: cada consumidor registra `event_id` en `evento_procesado`
  antes de procesar (`IdempotencyService`); los duplicados se ignoran.
- **Ack manual**: los listeners usan `MANUAL_IMMEDIATE`; errores no recuperables
  van al DLT (`reserva.dlt.v1`) tras reintentos con backoff.
- **Transacciones locales**: cada paso de la saga modifica su base en un
  `@Transactional` propio; nunca hay transacciones distribuidas.
- **Publicación**: `KafkaDomainEventPublisher` resuelve el topic según el tipo
  de evento y publica de forma asíncrona con retries.

## Estrategia de datos

Hoy **todos los microservicios comparten la misma base PostgreSQL**
(`petcaredb`). Esto es un punto único de acoplamiento: la saga funciona porque
cada servicio solo toca sus propias tablas (`pago`, `reserva`, `evento_procesado`),
pero un despliegue multi-base (una BD por servicio) permitiría aislar dominios.

**Camino de migración (recomendado, NO aplicado):**
1. Extraer las tablas `pago*` a una BD `paymentdb`; `reserva*` a `reservationdb`.
2. Configurar `spring.datasource.url` por servicio en cada `application.yml`.
3. Mover `evento_procesado` (o un `outbox`) a cada BD por servicio para
   mantener la idempotencia local.
4. Los catálogos compartidos (`persona`, `proveedor_servicio`, `promocion`)
   pasan a exponerse por API o por replicación de datos de referencia.
5. Validar con los tests de integración existentes (flujo feliz y compensación).

Mientras no se migre, **no** se debe crear una BD separada: la saga y los tests
asumen la base compartida actual.

## Tests

- `payment-service`: `PagoFlowIntegrationTest` cubre pago pendiente, procesar,
  rechazo de tarjeta (`PagoFallidoEvent`), reembolso (`PagoReembolsadoEvent`) y
  duplicados.
- `reservation-service`: `ReservaFlowIntegrationTest` cubre la compensación
  (pago fallido -> reserva CANCELADA + `ReservaCanceladaEvent`), aceptación,
  rechazo y duplicados.
