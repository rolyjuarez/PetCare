# Reservation-service — refactor a CQRS + Clean Architecture

Refactor de `services/reservation-service` (rama `sesion-7-api-gateway`) para
separar el lado de **escritura** (command) del lado de **lectura** (query)
aplicando Clean Architecture. La arquitectura anterior (service + impl +
controller + entity + mapper + command + consumer) fue reemplazada por completo;
no quedó código muerto.

## Estructura final (`modules/reserva`)

```text
application/command   ReservaCommandUseCase, ReservaCommandService,
                      ReservaDatosValidator, ReservaNotifier,
                      CrearReservaCommand, ActualizarReservaCommand,
                      ConfirmarReservaCommand, RechazarReservaCommand,
                      CancelarReservaCommand
application/query     ReservaQueryService, ReservaQueryServiceImpl,
                      ReservaQueryMapper
domain/model          Reserva, EstadoReserva, Disponibilidad, SlotHorario
domain/port/out       ReservaCommandRepository, ReservaQueryRepository,
                      EstadoReservaRepository, CatalogInfoRepository,
                      NotificationPort
domain/service        ReservaValidator (reglas puras),
                      DisponibilidadCalculator (slots por duración)
infrastructure
  adapter/in/web      ReservaCommandController, ReservaQueryController,
                      ReservaInternaController
  adapter/in/kafka    ConfirmarRechazarCommandConsumer,
                      CancelarReservaCommandConsumer
  adapter/out/persistence  MyBatisReservaCommandRepository,
                      MyBatisReservaQueryRepository,
                      MyBatisEstadoReservaRepository,
                      MyBatisCatalogInfoRepository, MyBatisNotificationPort
dto/event             intactos
```

Las dependencias apuntan hacia adentro: `application` usa los puertos de
`domain` y `infrastructure` los implementa, sin acoplamiento a Spring/Kafka/MyBatis.

## Decisiones

- **Misma base de datos `petcaredb`** con repositorios separados (command/query)
  sobre MyBatis y un único `DataSource`.
- **Reemplazo total** de `ReservaService`/`ReservaServiceImpl`/`ReservaController`
  (eliminados). Los commands del saga (`Confirmar`, `Rechazar`, `Cancelar`) se
  movieron a `application/command`; `ActualizarReservaCommand.toCrearReservaCommand()`
  permite reutilizar `validarParaGuardar`.
- Entidades y FQCN movidos a `domain.model` (`type-aliases-package` actualizado),
  mappers a `infrastructure.adapter.out.persistence` y sus XML con namespace nuevo.
- Nuevos `update` en MyBatis: `updateEstado`, `updateRespuesta`, `softDelete`;
  `selectBooked` acepta `excluirId`.
- **Filtros del listado implementados**: `GET /api/v1/reservas` ahora aplica
  `mascotaId`, `servicioId`, `estado` y `fechaDesde`/`fechaHasta` (SQL dinámico
  `selectFiltered`/`countFiltered` con join a `estado_reserva`); antes eran
  aceptados por la API pero ignorados.
- **Sin cambios** en endpoints públicos (`/api/v1/reservas/**`), consumidores Kafka,
  eventos de dominio, notificaciones y emails.

## Verificación

- `mvn clean verify` → BUILD SUCCESS.
- 65/65 tests (unitarios + integración H2 `MODE=PostgreSQL` + Kafka embebido):
  `ReservaCommandServiceTest`, `ReservaDatosValidatorTest`,
  `ReservaQueryServiceImplTest`, `ReservaQueryServiceImplSlotsTest`,
  `ReservaValidatorTest`, `DisponibilidadCalculatorTest`,
  `ConfirmarRechazarCommandConsumerTest`, `CancelarReservaCommandConsumerTest`,
  `ReservaCommandControllerTest`, `ReservaQueryControllerTest`,
  `ReservaInternaControllerTest`, `ReservaFlowIntegrationTest`.
- Se corrigió un race en `ReservaFlowIntegrationTest` (evento stale de Kafka de
  tests anteriores) filtrando el evento por `reservaId` y un XML stale
  (`DisponibilidadMapper.xml`) que se resolvió con `mvn clean`.

## Estado git

Cambios presentes en working tree, **sin commitear** (decisión del usuario).
