package bo.capital.tec.pet.common.messaging.outbox;

import bo.capital.tec.pet.common.event.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("kafka")
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public void publishEvent(DomainEvent event) {
        try {
            String topic = resolveTopic(event);
            String payload = objectMapper.writeValueAsString(event);

            Outbox outbox = Outbox.builder()
                    .eventId(event.getEventId())
                    .aggregateType(event.getAggregateType())
                    .aggregateId(event.getAggregateId())
                    .eventType(event.getClass().getSimpleName())
                    .topic(topic)
                    .payload(payload)
                    .status("PENDING")
                    .retryCount(0)
                    .build();

            outboxMapper.insert(outbox);
            log.debug("Outbox entry created: eventId={} type={} topic={}",
                    event.getEventId(), event.getClass().getSimpleName(), topic);
        } catch (Exception e) {
            log.error("Error writing to outbox for event {}: {}",
                    event.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    private String resolveTopic(DomainEvent event) {
        return switch (event.getAggregateType()) {
            case "RESERVA" -> "petcare.reserva.event.v1";
            case "PAGO" -> "petcare.pago.event.v1";
            case "USUARIO" -> "petcare.usuario.event.v1";
            case "MASCOTA" -> "petcare.mascota.event.v1";
            default -> "petcare.internal.dlt.v1";
        };
    }
}
