package bo.capital.tec.pet.common.dto;

import java.io.Serializable;
import java.time.Instant;

public interface Command extends Serializable {
    String getCommandId();
    String getCommandType();
    String getAggregateType();
    Long getAggregateId();
    Instant getTimestamp();
}
