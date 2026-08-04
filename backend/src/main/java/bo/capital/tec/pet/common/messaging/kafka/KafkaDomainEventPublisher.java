package bo.capital.tec.pet.common.messaging.kafka;

import bo.capital.tec.pet.common.event.DomainEvent;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.common.messaging.observability.EventLogger;
import bo.capital.tec.pet.common.messaging.observability.MessageMetrics;
import bo.capital.tec.pet.common.messaging.outbox.OutboxPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(value = "app.events.mode", havingValue = "kafka", matchIfMissing = false)
@Primary
@RequiredArgsConstructor
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final OutboxPublisher outboxPublisher;
    private final EventLogger eventLogger;
    private final MessageMetrics messageMetrics;

    @Override
    public void publish(DomainEvent event) {
        String topic = resolveTopic(event);
        outboxPublisher.publishEvent(event);
        eventLogger.logEventPublished(event, topic);
        messageMetrics.incrementPublished(topic);
    }

    private String resolveTopic(DomainEvent event) {
        return switch (event.getAggregateType()) {
            case "RESERVA" -> "petcare.reserva.event.v1";
            case "PAGO" -> "petcare.pago.event.v1";
            case "USUARIO" -> "petcare.usuario.event.v1";
            case "MASCOTA" -> "petcare.mascota.event.v1";
            case "PROMOCION" -> "petcare.promocion.event.v1";
            default -> "petcare.internal.dlt.v1";
        };
    }
}
