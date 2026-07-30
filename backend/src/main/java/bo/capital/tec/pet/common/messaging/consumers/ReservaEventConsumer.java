package bo.capital.tec.pet.common.messaging.consumers;

import bo.capital.tec.pet.common.event.DomainEvent;
import bo.capital.tec.pet.common.messaging.observability.EventLogger;
import bo.capital.tec.pet.common.messaging.observability.MessageMetrics;
import bo.capital.tec.pet.common.messaging.resilience.DeadLetterHandler;
import bo.capital.tec.pet.common.messaging.resilience.IdempotencyService;
import bo.capital.tec.pet.modules.notificacion.api.NotificacionApi;
import bo.capital.tec.pet.modules.notificacion.entity.Notificacion;
import bo.capital.tec.pet.modules.reserva.event.ReservaCanceladaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCompletadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaConfirmadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCreadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("kafka")
@RequiredArgsConstructor
public class ReservaEventConsumer {

    private final IdempotencyService idempotencyService;
    private final EventLogger eventLogger;
    private final MessageMetrics messageMetrics;
    private final DeadLetterHandler deadLetterHandler;
    private final NotificacionApi notificacionApi;

    @KafkaListener(topics = "${app.kafka.topic.reserva-event:petcare.reserva.event.v1}",
                   groupId = "${spring.kafka.consumer.group-id:petcare-consumer}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consume(DomainEvent event, Acknowledgment ack) {
        String messageId = event.getEventId();
        String topic = "petcare.reserva.event.v1";

        if (idempotencyService.isProcessed(messageId)) {
            log.trace("Duplicate event skipped: eventId={} type={}", messageId, event.getClass().getSimpleName());
            ack.acknowledge();
            return;
        }

        try {
            eventLogger.logEventConsumed(event, topic, "petcare-consumer");
            messageMetrics.incrementConsumed(topic);

            switch (event) {
                case ReservaCreadaEvent e -> handleReservaCreada(e);
                case ReservaConfirmadaEvent e -> handleReservaConfirmada(e);
                case ReservaCanceladaEvent e -> handleReservaCancelada(e);
                case ReservaCompletadaEvent e -> handleReservaCompletada(e);
                default -> log.warn("Unknown reserva event type: {}", event.getClass().getName());
            }

            idempotencyService.markProcessed(messageId);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing event: eventId={} type={}: {}",
                    messageId, event.getClass().getSimpleName(), e.getMessage(), e);
            messageMetrics.incrementError(topic);
            deadLetterHandler.sendToDeadLetter(topic, event, e);
            ack.acknowledge();
        }
    }

    private void handleReservaCreada(ReservaCreadaEvent event) {
        createNotification(event.getClienteId(), "Reserva Creada",
                "Su reserva " + event.getCodigo() + " ha sido creada exitosamente.", "EXITO");
        if (event.getProveedorId() != null) {
            createNotification(event.getProveedorId(), "Nueva Reserva",
                    "Se le ha asignado la reserva " + event.getCodigo() + ".", "INFO");
        }
    }

    private void handleReservaConfirmada(ReservaConfirmadaEvent event) {
        createNotification(event.getClienteId(), "Reserva Confirmada",
                "Su reserva " + event.getCodigo() + " ha sido confirmada.", "EXITO");
        if (event.getProveedorId() != null) {
            createNotification(event.getProveedorId(), "Reserva Confirmada",
                    "La reserva " + event.getCodigo() + " ha sido confirmada.", "INFO");
        }
    }

    private void handleReservaCancelada(ReservaCanceladaEvent event) {
        createNotification(event.getClienteId(), "Reserva Cancelada",
                "Su reserva " + event.getCodigo() + " ha sido cancelada.", "ADVERTENCIA");
        if (event.getProveedorId() != null) {
            createNotification(event.getProveedorId(), "Reserva Cancelada",
                    "La reserva " + event.getCodigo() + " ha sido cancelada.", "ADVERTENCIA");
        }
    }

    private void handleReservaCompletada(ReservaCompletadaEvent event) {
        createNotification(event.getClienteId(), "Servicio Completado",
                "El servicio de la reserva " + event.getCodigo() + " ha sido completado.", "EXITO");
        if (event.getProveedorId() != null) {
            createNotification(event.getProveedorId(), "Servicio Completado",
                    "La reserva " + event.getCodigo() + " ha sido marcada como completada.", "EXITO");
        }
    }

    private void createNotification(Long usuarioId, String titulo, String mensaje, String tipo) {
        try {
            Notificacion notificacion = Notificacion.builder()
                    .usuarioId(usuarioId)
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo(tipo)
                    .leida(false)
                    .build();
            notificacionApi.insert(notificacion);
        } catch (Exception e) {
            log.warn("Error creating notification for usuario {}: {}", usuarioId, e.getMessage());
        }
    }
}
