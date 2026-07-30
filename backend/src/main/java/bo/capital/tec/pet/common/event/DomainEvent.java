package bo.capital.tec.pet.common.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(chain = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class DomainEvent {

    private String eventId = UUID.randomUUID().toString();
    private Instant occurredOn = Instant.now();
    private String aggregateType;
    private Long aggregateId;
    private String source;

    protected DomainEvent(String aggregateType, Long aggregateId) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.source = aggregateType;
    }

    protected DomainEvent(String aggregateType, Long aggregateId, String source) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.source = source;
    }
}
