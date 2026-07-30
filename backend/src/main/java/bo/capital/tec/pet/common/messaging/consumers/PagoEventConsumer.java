package bo.capital.tec.pet.common.messaging.consumers;

import bo.capital.tec.pet.common.event.DomainEvent;
import bo.capital.tec.pet.common.messaging.observability.EventLogger;
import bo.capital.tec.pet.common.messaging.observability.MessageMetrics;
import bo.capital.tec.pet.common.messaging.resilience.DeadLetterHandler;
import bo.capital.tec.pet.common.messaging.resilience.IdempotencyService;
import bo.capital.tec.pet.modules.notificacion.api.NotificacionApi;
import bo.capital.tec.pet.modules.notificacion.entity.Notificacion;
import bo.capital.tec.pet.modules.pago.event.PagoConfirmadoEvent;
import bo.capital.tec.pet.modules.pago.event.PagoReembolsadoEvent;
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
public class PagoEventConsumer {

    private final IdempotencyService idempotencyService;
    private final EventLogger eventLogger;
    private final MessageMetrics messageMetrics;
    private final DeadLetterHandler deadLetterHandler;
    private final NotificacionApi notificacionApi;

    @KafkaListener(topics = "${app.kafka.topic.pago-event:petcare.pago.event.v1}",
                   groupId = "${spring.kafka.consumer.group-id:petcare-consumer}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consume(DomainEvent event, Acknowledgment ack) {
        String messageId = event.getEventId();
        String topic = "petcare.pago.event.v1";

        if (idempotencyService.isProcessed(messageId)) {
            log.trace("Duplicate event skipped: eventId={} type={}", messageId, event.getClass().getSimpleName());
            ack.acknowledge();
            return;
        }

        try {
            eventLogger.logEventConsumed(event, topic, "petcare-consumer");
            messageMetrics.incrementConsumed(topic);

            switch (event) {
                case PagoConfirmadoEvent e -> handlePagoConfirmado(e);
                case PagoReembolsadoEvent e -> handlePagoReembolsado(e);
                default -> log.warn("Unknown pago event type: {}", event.getClass().getName());
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

    private void handlePagoConfirmado(PagoConfirmadoEvent event) {
        createNotification(event.getClienteId(), "Pago Confirmado",
                "Su pago de Bs " + event.getMonto() + " para la reserva " + event.getReservaCodigo()
                        + " ha sido confirmado.", "EXITO");
    }

    private void handlePagoReembolsado(PagoReembolsadoEvent event) {
        createNotification(event.getClienteId(), "Pago Reembolsado",
                "Su pago de Bs " + event.getMonto() + " para la reserva " + event.getReservaCodigo()
                        + " ha sido reembolsado.", "INFO");
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
