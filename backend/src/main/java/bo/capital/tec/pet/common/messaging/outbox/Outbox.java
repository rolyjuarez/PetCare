package bo.capital.tec.pet.common.messaging.outbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outbox {
    private Long id;
    private String eventId;
    private String aggregateType;
    private Long aggregateId;
    private String eventType;
    private String topic;
    private String payload;
    private String status;
    private Integer retryCount;
    private String lastError;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private LocalDateTime nextRetryAt;
}
