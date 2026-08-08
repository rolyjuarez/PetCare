package bo.capital.tec.pet.saga.infrastructure.adapter.out.persistence;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Registro persistido de la tabla {@code evento_procesado}.
 */
@Data
public class EventoProcesadoRecord {

    private String eventId;
    private String eventType;
    private String service;
    private Long aggregateId;
    private LocalDateTime procesadoEn;
}
