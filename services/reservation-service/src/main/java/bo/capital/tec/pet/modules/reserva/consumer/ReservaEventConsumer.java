package bo.capital.tec.pet.modules.reserva.consumer;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.reserva.event.ReservaAceptadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaRechazadaEvent;
import bo.capital.tec.pet.modules.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaEventConsumer {

    private final ReservaService reservaService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${app.kafka.topic.reserva-aceptada}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onReservaAceptada(@Payload ReservaAceptadaEvent event, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(event.getEventId(), "ReservaAceptadaEvent",
                event.getReservaId(), "reservation-service", event)) {
            log.debug("Evento Aceptada duplicado, ignorado: {}", event.getEventId());
        } else {
            reservaService.aplicarAceptacion(event);
        }
        ack.acknowledge();
    }

    @KafkaListener(topics = "${app.kafka.topic.reserva-rechazada}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onReservaRechazada(@Payload ReservaRechazadaEvent event, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(event.getEventId(), "ReservaRechazadaEvent",
                event.getReservaId(), "reservation-service", event)) {
            log.debug("Evento Rechazada duplicado, ignorado: {}", event.getEventId());
        } else {
            reservaService.aplicarRechazo(event);
        }
        ack.acknowledge();
    }
}
