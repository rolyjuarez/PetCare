package bo.capital.tec.pet.common.messaging.consumers;

import bo.capital.tec.pet.common.event.DomainEvent;
import bo.capital.tec.pet.common.messaging.observability.EventLogger;
import bo.capital.tec.pet.common.messaging.observability.MessageMetrics;
import bo.capital.tec.pet.common.messaging.resilience.DeadLetterHandler;
import bo.capital.tec.pet.common.messaging.resilience.IdempotencyService;
import bo.capital.tec.pet.modules.promocion.event.PromocionCreadaEvent;
import bo.capital.tec.pet.modules.promocion.service.PromocionService;
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
public class PromocionEventConsumer {

    private final IdempotencyService idempotencyService;
    private final EventLogger eventLogger;
    private final MessageMetrics messageMetrics;
    private final DeadLetterHandler deadLetterHandler;
    private final PromocionService promocionService;

    @KafkaListener(topics = "${app.kafka.topic.promocion-event:petcare.promocion.event.v1}",
                   groupId = "${spring.kafka.consumer.group-id:petcare-consumer}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consume(DomainEvent event, Acknowledgment ack) {
        String messageId = event.getEventId();
        String topic = "petcare.promocion.event.v1";

        if (idempotencyService.isProcessed(messageId)) {
            log.trace("Duplicate event skipped: eventId={} type={}", messageId, event.getClass().getSimpleName());
            ack.acknowledge();
            return;
        }

        try {
            eventLogger.logEventConsumed(event, topic, "petcare-consumer");
            messageMetrics.incrementConsumed(topic);

            if (event instanceof PromocionCreadaEvent e) {
                int notificados = promocionService.notificarClientes(e.getPromocionId());
                log.info("Promoción {} notificada a {} clientes (asíncrono)", e.getCodigo(), notificados);
            } else {
                log.warn("Unknown promocion event type: {}", event.getClass().getName());
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
}
