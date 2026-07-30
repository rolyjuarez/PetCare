package bo.capital.tec.pet.common.messaging.outbox;

import bo.capital.tec.pet.common.event.DomainEvent;
import bo.capital.tec.pet.common.messaging.observability.MessageMetrics;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@Profile("kafka")
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MessageMetrics messageMetrics;

    @Value("${app.outbox.batch-size:100}")
    private int batchSize;

    @Value("${app.outbox.max-retries:5}")
    private int maxRetries;

    @Value("${app.outbox.retry-delay-minutes:5}")
    private int retryDelayMinutes;

    @Value("${app.outbox.stale-threshold-minutes:1440}")
    private int staleThresholdMinutes;

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:5000}")
    @Transactional
    public void processPending() {
        LocalDateTime now = LocalDateTime.now();
        List<Outbox> pending = outboxMapper.selectPending(batchSize, now);

        for (Outbox entry : pending) {
            try {
                outboxMapper.markProcessing(entry.getId());

                DomainEvent payload = objectMapper.readValue(entry.getPayload(), DomainEvent.class);
                kafkaTemplate.send(entry.getTopic(), entry.getEventId(), payload)
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                outboxMapper.markProcessed(entry.getId(), LocalDateTime.now());
                                messageMetrics.incrementPublished(entry.getTopic());
                                log.debug("Outbox event sent: eventId={} topic={} offset={}",
                                        entry.getEventId(), entry.getTopic(),
                                        result.getRecordMetadata().offset());
                            } else {
                                handleFailure(entry, ex.getMessage());
                            }
                        });
            } catch (Exception e) {
                handleFailure(entry, e.getMessage());
            }
        }
    }

    @Scheduled(fixedDelayString = "${app.outbox.stale-poll-interval-ms:300000}")
    @Transactional
    public void retryStale() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(staleThresholdMinutes);
        List<Outbox> stale = outboxMapper.selectStale(batchSize, threshold, maxRetries);

        for (Outbox entry : stale) {
            log.warn("Retrying stale outbox entry: id={} eventId={} retryCount={}",
                    entry.getId(), entry.getEventId(), entry.getRetryCount());
            try {
                outboxMapper.markProcessing(entry.getId());
                outboxMapper.incrementRetry(entry.getId(), "Retry attempt",
                        LocalDateTime.now().plusMinutes(retryDelayMinutes));

                DomainEvent payload = objectMapper.readValue(entry.getPayload(), DomainEvent.class);
                kafkaTemplate.send(entry.getTopic(), entry.getEventId(), payload);
            } catch (Exception e) {
                log.error("Error retrying outbox entry {}: {}", entry.getId(), e.getMessage());
            }
        }
    }

    @Scheduled(cron = "${app.outbox.cleanup-cron:0 0 3 * * ?}")
    @Transactional
    public void cleanupOldEntries() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        long deleted = outboxMapper.deleteProcessedOlderThan(cutoff);
        if (deleted > 0) {
            log.info("Cleaned up {} processed outbox entries older than {}", deleted, cutoff);
        }
    }

    private void handleFailure(Outbox entry, String error) {
        log.error("Failed to send outbox event id={} eventId={}: {}",
                entry.getId(), entry.getEventId(), error);
        messageMetrics.incrementError(entry.getTopic());

        LocalDateTime nextRetry = LocalDateTime.now().plusMinutes(retryDelayMinutes);

        if (entry.getRetryCount() >= maxRetries) {
            outboxMapper.markFailed(entry.getId(), error, null);
            log.warn("Outbox entry {} moved to FAILED after {} retries",
                    entry.getId(), entry.getRetryCount());
        } else {
            outboxMapper.incrementRetry(entry.getId(), error, nextRetry);
        }
    }
}
