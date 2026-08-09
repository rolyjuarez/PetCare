package bo.capital.tec.pet.modules.pago.consumer;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.pago.command.LiberarDescuentoCommand;
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
public class LiberarDescuentoCommandConsumer {

    private final PagoService pagoService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${app.kafka.topic.comando-liberar-descuento}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onLiberarDescuento(@Payload LiberarDescuentoCommand comando, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(comando.getCommandId(), "LiberarDescuentoCommand",
                comando.getReservaId(), "payment-service", comando)) {
            log.debug("Comando LiberarDescuento duplicado, ignorado: {}", comando.getCommandId());
        } else {
            pagoService.liberarDescuento(comando.getReservaId());
        }
        ack.acknowledge();
    }
}
