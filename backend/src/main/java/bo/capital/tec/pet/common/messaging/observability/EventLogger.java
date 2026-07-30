package bo.capital.tec.pet.common.messaging.observability;

import bo.capital.tec.pet.common.event.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventLogger {

    private final ObjectMapper objectMapper;

    public void logEventPublished(DomainEvent event, String topic) {
        MDC.put("eventId", event.getEventId());
        MDC.put("eventType", event.getClass().getSimpleName());
        MDC.put("aggregateType", event.getAggregateType());
        MDC.put("aggregateId", String.valueOf(event.getAggregateId()));
        MDC.put("topic", topic);

        try {
            log.info("EVENT_PUBLISHED topic={} eventType={} aggregateType={} aggregateId={} payload={}",
                    topic, event.getClass().getSimpleName(),
                    event.getAggregateType(), event.getAggregateId(),
                    objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize event for logging: {}", e.getMessage());
        }

        MDC.clear();
    }

    public void logEventConsumed(DomainEvent event, String topic, String consumerGroup) {
        MDC.put("eventId", event.getEventId());
        MDC.put("eventType", event.getClass().getSimpleName());
        MDC.put("aggregateType", event.getAggregateType());
        MDC.put("aggregateId", String.valueOf(event.getAggregateId()));
        MDC.put("topic", topic);
        MDC.put("consumerGroup", consumerGroup);

        log.info("EVENT_CONSUMED topic={} eventType={} aggregateType={} aggregateId={} consumerGroup={}",
                topic, event.getClass().getSimpleName(),
                event.getAggregateType(), event.getAggregateId(), consumerGroup);

        MDC.clear();
    }

    public void logEventError(DomainEvent event, String topic, String error) {
        MDC.put("eventId", event.getEventId());
        MDC.put("eventType", event.getClass().getSimpleName());
        MDC.put("topic", topic);

        log.error("EVENT_ERROR topic={} eventType={} eventId={} error={}",
                topic, event.getClass().getSimpleName(), event.getEventId(), error);

        MDC.clear();
    }
}
