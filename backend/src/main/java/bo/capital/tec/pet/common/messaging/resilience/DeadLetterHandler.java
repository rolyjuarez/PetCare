package bo.capital.tec.pet.common.messaging.resilience;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@Profile("kafka")
@RequiredArgsConstructor
public class DeadLetterHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.dlt:petcare.internal.dlt.v1}")
    private String deadLetterTopic;

    public void sendToDeadLetter(String originalTopic, Object record, String key, Exception cause) {
        try {
            Map<String, Object> dltMessage = new LinkedHashMap<>();
            dltMessage.put("originalTopic", originalTopic);
            dltMessage.put("originalKey", key);
            dltMessage.put("originalPayload", record);
            dltMessage.put("errorType", cause.getClass().getName());
            dltMessage.put("errorMessage", cause.getMessage());
            dltMessage.put("failedAt", Instant.now().toString());

            String deadLetterKey = key != null ? key + "_" + Instant.now().toEpochMilli() : null;

            kafkaTemplate.send(deadLetterTopic, deadLetterKey, dltMessage)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Message sent to DLT: originalTopic={} key={} dltOffset={}",
                                    originalTopic, key, result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send message to DLT: {}", ex.getMessage(), ex);
                        }
                    });

            log.warn("Dead-lettered message from topic={} key={} error={}",
                    originalTopic, key, cause.getMessage());
        } catch (Exception e) {
            log.error("Failed to construct dead-letter message: {}", e.getMessage(), e);
        }
    }

    public void sendToDeadLetter(String originalTopic, Object record, Exception cause) {
        sendToDeadLetter(originalTopic, record, null, cause);
    }
}
