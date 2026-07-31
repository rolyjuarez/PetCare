package bo.capital.tec.pet.modules.proveedor.consumer;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.proveedor.event.ReservaCreadaEvent;
import bo.capital.tec.pet.modules.proveedor.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProveedorReservaConsumer {

    private final ProveedorService proveedorService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${app.kafka.topic.reserva-creada}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onReservaCreada(@Payload ReservaCreadaEvent event, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(event.getEventId(), "ReservaCreadaEvent",
                event.getReservaId(), "provider-service", event)) {
            log.debug("Evento ReservaCreada duplicado, ignorado: {}", event.getEventId());
        } else {
            proveedorService.procesarReservaCreada(event);
        }
        ack.acknowledge();
    }
}
