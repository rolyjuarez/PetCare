package bo.capital.tec.pet.common.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyMapper idempotencyMapper;
    private final ObjectMapper objectMapper;

    public boolean isProcessed(String eventId) {
        return idempotencyMapper.existsEventoProcesado(eventId) != null;
    }

    public void markProcessed(String eventId, String eventType, Long aggregateId, String service, Object payload) {
        try {
            EventoProcesado evento = new EventoProcesado();
            evento.setEventId(eventId);
            evento.setEventType(eventType);
            evento.setAggregateId(aggregateId);
            evento.setService(service);
            evento.setPayload(payload != null ? objectMapper.writeValueAsString(payload) : null);
            evento.setProcesadoEn(java.time.LocalDateTime.now());
            idempotencyMapper.insertEventoProcesado(evento);
        } catch (JsonProcessingException e) {
            log.error("Error serializando payload para evento {}", eventId, e);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            log.debug("Evento {} ya procesado, insert ignorado", eventId);
        }
    }

    public boolean tryMarkProcessed(String eventId, String eventType, Long aggregateId, String service, Object payload) {
        if (isProcessed(eventId)) {
            return false;
        }
        markProcessed(eventId, eventType, aggregateId, service, payload);
        return true;
    }
}
