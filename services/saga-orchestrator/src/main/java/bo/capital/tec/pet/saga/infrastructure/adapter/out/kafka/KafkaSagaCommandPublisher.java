package bo.capital.tec.pet.saga.infrastructure.adapter.out.kafka;

import bo.capital.tec.pet.saga.domain.model.SagaCommand;
import bo.capital.tec.pet.saga.domain.port.out.SagaCommandPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida: publica los comandos del orquestador en el topic de
 * comandos correspondiente según el tipo de comando.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSagaCommandPublisher implements SagaCommandPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.comando-notificar-proveedor:saga.comando.notificar-proveedor}")
    private String notificarProveedorTopic;

    @Value("${app.kafka.topic.comando-confirmar-reserva:saga.comando.confirmar-reserva}")
    private String confirmarReservaTopic;

    @Value("${app.kafka.topic.comando-crear-pago:saga.comando.crear-pago}")
    private String crearPagoTopic;

    @Value("${app.kafka.topic.comando-cancelar-reserva:saga.comando.cancelar-reserva}")
    private String cancelarReservaTopic;

    @Value("${app.kafka.topic.comando-rechazar-reserva:saga.comando.rechazar-reserva}")
    private String rechazarReservaTopic;

    @Value("${app.kafka.topic.comando-liberar-descuento:saga.comando.liberar-descuento}")
    private String liberarDescuentoTopic;

    @Value("${app.kafka.topic.dlt:saga.dlt.v1}")
    private String deadLetterTopic;

    @Override
    public void publicar(SagaCommand comando) {
        String topic = resolveTopic(comando);
        kafkaTemplate.send(topic, String.valueOf(comando.getAggregateId()), comando);
        log.info("Comando {} publicado en {}", comando.getClass().getSimpleName(), topic);
    }

    private String resolveTopic(SagaCommand comando) {
        return switch (comando.getClass().getSimpleName()) {
            case "NotificarProveedorCommand" -> notificarProveedorTopic;
            case "ConfirmarReservaCommand" -> confirmarReservaTopic;
            case "CrearPagoCommand" -> crearPagoTopic;
            case "CancelarReservaCommand" -> cancelarReservaTopic;
            case "RechazarReservaCommand" -> rechazarReservaTopic;
            case "LiberarDescuentoCommand" -> liberarDescuentoTopic;
            default -> deadLetterTopic;
        };
    }
}
