package bo.capital.tec.pet.common.messaging.resilience;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final Map<String, LocalDateTime> processedMessages = new ConcurrentHashMap<>();

    @Value("${app.idempotency.retention-minutes:1440}")
    private int retentionMinutes;

    public boolean isProcessed(String messageId) {
        LocalDateTime processedAt = processedMessages.get(messageId);
        if (processedAt == null) {
            return false;
        }
        if (processedAt.plusMinutes(retentionMinutes).isBefore(LocalDateTime.now())) {
            processedMessages.remove(messageId);
            return false;
        }
        return true;
    }

    public void markProcessed(String messageId) {
        processedMessages.put(messageId, LocalDateTime.now());
        log.trace("Message marked as processed: messageId={}", messageId);
    }

    public void evictExpired() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(retentionMinutes);
        processedMessages.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
    }

    public int size() {
        return processedMessages.size();
    }
}
