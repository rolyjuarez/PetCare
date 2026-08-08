package bo.capital.tec.pet.saga.application.service;

import bo.capital.tec.pet.saga.domain.port.out.EventoProcesadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * Garantiza que cada evento se procese una sola vez usando la tabla compartida
 * {@code evento_procesado}. La clave primaria {@code event_id} hace el insert
 * idempotente: si el evento ya existe, se ignora.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotenciaSagaService {

    private final EventoProcesadoRepository repositorio;

    public boolean marcarProcesado(String eventId, String tipoEvento, Long aggregateId) {
        if (repositorio.existsByEventId(eventId)) {
            log.debug("Evento {} ya procesado, ignorado", eventId);
            return false;
        }
        try {
            repositorio.insert(eventId, tipoEvento, aggregateId, "saga-orchestrator");
            return true;
        } catch (DuplicateKeyException e) {
            log.debug("Evento {} ya procesado, insert ignorado", eventId);
            return false;
        }
    }
}
