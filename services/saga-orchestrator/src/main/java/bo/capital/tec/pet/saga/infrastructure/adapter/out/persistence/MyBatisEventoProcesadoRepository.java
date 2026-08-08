package bo.capital.tec.pet.saga.infrastructure.adapter.out.persistence;

import bo.capital.tec.pet.saga.domain.port.out.EventoProcesadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Adaptador de salida: implementa el puerto de idempotencia usando MyBatis
 * sobre la tabla compartida {@code evento_procesado}.
 */
@Repository
@RequiredArgsConstructor
public class MyBatisEventoProcesadoRepository implements EventoProcesadoRepository {

    private final EventoProcesadoMapper mapper;

    @Override
    public boolean existsByEventId(String eventId) {
        return mapper.exists(eventId) != null;
    }

    @Override
    public void insert(String eventId, String eventType, Long aggregateId, String service) {
        EventoProcesadoRecord record = new EventoProcesadoRecord();
        record.setEventId(eventId);
        record.setEventType(eventType);
        record.setAggregateId(aggregateId);
        record.setService(service);
        record.setProcesadoEn(LocalDateTime.now());
        mapper.insert(record);
    }
}
