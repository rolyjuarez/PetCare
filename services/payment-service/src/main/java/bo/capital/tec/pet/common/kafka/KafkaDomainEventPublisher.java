package bo.capital.tec.pet.common.kafka;

import bo.capital.tec.pet.common.event.DomainEvent;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.reserva-creada:reserva.creada}")
    private String reservaCreadaTopic;

    @Value("${app.kafka.topic.reserva-aceptada:reserva.aceptada}")
    private String reservaAceptadaTopic;

    @Value("${app.kafka.topic.reserva-rechazada:reserva.rechazada}")
    private String reservaRechazadaTopic;

    @Value("${app.kafka.topic.reserva-confirmada:reserva.confirmada}")
    private String reservaConfirmadaTopic;

    @Value("${app.kafka.topic.pago-completado:pago.completado}")
    private String pagoCompletadoTopic;

    @Value("${app.kafka.topic.dlt:reserva.dlt.v1}")
    private String deadLetterTopic;

    public void publish(DomainEvent event) {
        String topic = resolveTopic(event);
        String key = String.valueOf(event.getAggregateId());
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Evento {} publicado en {} [offset={}]",
                        event.getEventId(), topic, result.getRecordMetadata().offset());
            } else {
                log.error("Fallo al publicar evento {} en {}", event.getEventId(), topic, ex);
            }
        });
    }

    private String resolveTopic(DomainEvent event) {
        return switch (event.getAggregateType()) {
            case "RESERVA" -> switch (event.getClass().getSimpleName()) {
                case "ReservaCreadaEvent" -> reservaCreadaTopic;
                case "ReservaAceptadaEvent" -> reservaAceptadaTopic;
                case "ReservaRechazadaEvent" -> reservaRechazadaTopic;
                case "ReservaConfirmadaEvent" -> reservaConfirmadaTopic;
                default -> deadLetterTopic;
            };
            case "PAGO" -> switch (event.getClass().getSimpleName()) {
                case "PagoProcesadoEvent" -> pagoCompletadoTopic;
                default -> deadLetterTopic;
            };
            default -> deadLetterTopic;
        };
    }
}
