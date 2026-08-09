package bo.capital.tec.pet.saga.application.service;

import bo.capital.tec.pet.saga.domain.model.CancelarReservaCommand;
import bo.capital.tec.pet.saga.domain.model.ConfirmarReservaCommand;
import bo.capital.tec.pet.saga.domain.model.CrearPagoCommand;
import bo.capital.tec.pet.saga.domain.model.EstadoSaga;
import bo.capital.tec.pet.saga.domain.model.LiberarDescuentoCommand;
import bo.capital.tec.pet.saga.domain.model.NotificarProveedorCommand;
import bo.capital.tec.pet.saga.domain.model.PagoFallidoEvent;
import bo.capital.tec.pet.saga.domain.model.PagoProcesadoEvent;
import bo.capital.tec.pet.saga.domain.model.PagoReembolsadoEvent;
import bo.capital.tec.pet.saga.domain.model.RechazarReservaCommand;
import bo.capital.tec.pet.saga.domain.model.ReservaAceptadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaCanceladaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaConfirmadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaCreadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaRechazadaEvent;
import bo.capital.tec.pet.saga.domain.model.SagaEstado;
import bo.capital.tec.pet.saga.domain.port.in.SagaCoordinator;
import bo.capital.tec.pet.saga.domain.port.out.SagaCommandPublisher;
import bo.capital.tec.pet.saga.domain.port.out.SagaEstadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso de la saga orquestada.
 *
 * <p>Mantiene la máquina de estados por reserva ({@link SagaEstado}) y, en cada
 * transición, emite un comando hacia el microservicio que debe actuar. La
 * compensación es centralizada: cuando un evento indica fallo, es este
 * servicio quien ordena la cancelación.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SagaOrquestadorService implements SagaCoordinator {

    private static final String COMPENSACION_PAGO_FALLIDO = "El pago no pudo completarse";

    private final SagaEstadoRepository sagaRepository;
    private final SagaCommandPublisher commandPublisher;
    private final IdempotenciaSagaService idempotencia;

    @Override
    @Transactional
    public void onReservaCreada(ReservaCreadaEvent evento) {
        if (!procesado(evento.getEventId(), "ReservaCreadaEvent", evento.getReservaId())) {
            return;
        }
        iniciarSaga(evento);
        commandPublisher.publicar(NotificarProveedorCommand.desde(evento));
        transicion(evento.getReservaId(), EstadoSaga.PROVEEDOR_NOTIFICADO,
                "Comando notificar-proveedor enviado a provider-service", null, evento.getEventId());
    }

    @Override
    @Transactional
    public void onReservaAceptada(ReservaAceptadaEvent evento) {
        if (!procesado(evento.getEventId(), "ReservaAceptadaEvent", evento.getReservaId())) {
            return;
        }
        transicion(evento.getReservaId(), EstadoSaga.RESERVA_ACEPTADA,
                "Reserva aceptada por proveedor " + evento.getProveedorId(), null, evento.getEventId());
        commandPublisher.publicar(ConfirmarReservaCommand.desde(evento));
    }

    @Override
    @Transactional
    public void onReservaRechazada(ReservaRechazadaEvent evento) {
        if (!procesado(evento.getEventId(), "ReservaRechazadaEvent", evento.getReservaId())) {
            return;
        }
        transicion(evento.getReservaId(), EstadoSaga.RECHAZADA,
                "Reserva rechazada por proveedor " + evento.getProveedorId(),
                evento.getMotivoRechazo(), evento.getEventId());
        commandPublisher.publicar(RechazarReservaCommand.desde(evento));
    }

    @Override
    @Transactional
    public void onReservaConfirmada(ReservaConfirmadaEvent evento) {
        if (!procesado(evento.getEventId(), "ReservaConfirmadaEvent", evento.getReservaId())) {
            return;
        }
        transicion(evento.getReservaId(), EstadoSaga.RESERVA_CONFIRMADA,
                "Reserva confirmada por reservation-service", null, evento.getEventId());
        commandPublisher.publicar(CrearPagoCommand.desde(evento));
        transicion(evento.getReservaId(), EstadoSaga.PAGO_SOLICITADO,
                "Comando crear-pago enviado a payment-service", null, evento.getEventId());
    }

    @Override
    @Transactional
    public void onReservaCancelada(ReservaCanceladaEvent evento) {
        if (!procesado(evento.getEventId(), "ReservaCanceladaEvent", evento.getReservaId())) {
            return;
        }
        transicion(evento.getReservaId(), EstadoSaga.CANCELADA,
                "Reserva cancelada (compensación ejecutada)",
                evento.getMotivo(), evento.getEventId());
    }

    @Override
    @Transactional
    public void onPagoProcesado(PagoProcesadoEvent evento) {
        if (!procesado(evento.getEventId(), "PagoProcesadoEvent", evento.getReservaId())) {
            return;
        }
        transicion(evento.getReservaId(), EstadoSaga.COMPLETADA,
                "Pago procesado, referencia " + evento.getReferenciaTransaccion(),
                null, evento.getEventId());
    }

    @Override
    @Transactional
    public void onPagoFallido(PagoFallidoEvent evento) {
        if (!procesado(evento.getEventId(), "PagoFallidoEvent", evento.getReservaId())) {
            return;
        }
        String motivo = evento.getMotivo() != null && !evento.getMotivo().isBlank()
                ? evento.getMotivo() : COMPENSACION_PAGO_FALLIDO;
        transicion(evento.getReservaId(), EstadoSaga.PAGO_FALLIDO,
                "Pago fallido, compensación iniciada (reserva + descuento)", motivo, evento.getEventId());
        commandPublisher.publicar(CancelarReservaCommand.desde(evento));
        commandPublisher.publicar(LiberarDescuentoCommand.desde(evento));
    }

    @Override
    @Transactional
    public void onPagoReembolsado(PagoReembolsadoEvent evento) {
        if (!procesado(evento.getEventId(), "PagoReembolsadoEvent", evento.getReservaId())) {
            return;
        }
        transicion(evento.getReservaId(), EstadoSaga.PAGO_REEMBOLSADO,
                "Pago reembolsado, referencia " + evento.getReferenciaTransaccion(),
                null, evento.getEventId());
        commandPublisher.publicar(LiberarDescuentoCommand.desde(evento));
    }

    private boolean procesado(String eventId, String tipoEvento, Long reservaId) {
        return idempotencia.marcarProcesado(eventId, tipoEvento, reservaId);
    }

    private void iniciarSaga(ReservaCreadaEvent evento) {
        sagaRepository.findByReservaId(evento.getReservaId()).ifPresentOrElse(
                existente -> log.debug("Saga ya iniciada para reserva {}", evento.getReservaId()),
                () -> sagaRepository.insert(SagaEstado.iniciar(
                        evento.getReservaId(), evento.getCodigo(), EstadoSaga.INICIADA,
                        "Reserva creada por reservation-service", evento.getEventId())));
    }

    private void transicion(Long reservaId, EstadoSaga estado, String paso, String motivo, String eventoId) {
        sagaRepository.updateEstado(reservaId, estado, paso, motivo, eventoId);
        log.info("Saga reserva {} -> {} ({})", reservaId, estado, paso);
    }
}
