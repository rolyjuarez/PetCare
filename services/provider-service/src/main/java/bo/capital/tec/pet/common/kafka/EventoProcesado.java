package bo.capital.tec.pet.common.kafka;

import lombok.Data;

@Data
public class EventoProcesado {
    private String eventId;
    private String eventType;
    private String service;
    private Long aggregateId;
    private String payload;
    private java.time.LocalDateTime procesadoEn;
}
