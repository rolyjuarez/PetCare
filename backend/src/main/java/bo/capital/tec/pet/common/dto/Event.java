package bo.capital.tec.pet.common.dto;

import java.io.Serializable;
import java.time.Instant;

public interface Event extends Serializable {
    String getEventId();
    String getAggregateType();
    Long getAggregateId();
    Instant getOccurredOn();
    String getEventType();
}
