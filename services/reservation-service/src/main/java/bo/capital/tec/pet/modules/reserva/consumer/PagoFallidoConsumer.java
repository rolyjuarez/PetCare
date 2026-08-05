package bo.capital.tec.pet.modules.reserva.consumer;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.reserva.event.PagoFallidoEvent;
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
public class PagoFallidoConsumer {

    private final ReservaService reservaService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${app.kafka.topic.pago-fallido}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onPagoFallido(@Payload PagoFallidoEvent event, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(event.getEventId(), "PagoFallidoEvent",
                event.getReservaId(), "reservation-service", event)) {
            log.debug("Evento PagoFallido duplicado, ignorado: {}", event.getEventId());
        } else {
            reservaService.compensarPorPagoFallido(event);
        }
        ack.acknowledge();
    }
}
