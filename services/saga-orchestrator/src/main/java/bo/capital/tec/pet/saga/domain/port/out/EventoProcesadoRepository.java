package bo.capital.tec.pet.saga.domain.port.out;

/**
 * Puerto de salida: registro de idempotencia para evitar procesar dos veces un
 * mismo evento.
 */
public interface EventoProcesadoRepository {

    boolean existsByEventId(String eventId);

    void insert(String eventId, String eventType, Long aggregateId, String service);
}
