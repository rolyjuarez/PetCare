package bo.capital.tec.pet.modules.pago.consumer;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.pago.event.ReservaConfirmadaEvent;
import bo.capital.tec.pet.modules.pago.service.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaConfirmadaConsumer {

    private final PagoService pagoService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${app.kafka.topic.reserva-confirmada}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onReservaConfirmada(@Payload ReservaConfirmadaEvent event, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(event.getEventId(), "ReservaConfirmadaEvent",
                event.getReservaId(), "payment-service", event)) {
            log.debug("Evento ReservaConfirmada duplicado, ignorado: {}", event.getEventId());
        } else {
            pagoService.crearDesdeReservaConfirmada(event);
        }
        ack.acknowledge();
    }
}
