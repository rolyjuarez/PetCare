package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

/**
 * Comando emitido por el orquestador hacia un microservicio. A diferencia de un
 * evento (algo que ya ocurrió), un comando expresa una orden: "haz esto".
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(chain = true)
public abstract class SagaCommand {

    private String commandId = UUID.randomUUID().toString();
    private Instant issuedAt = Instant.now();
    private String aggregateType;
    private Long aggregateId;

    protected SagaCommand(String aggregateType, Long aggregateId) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
    }
}
