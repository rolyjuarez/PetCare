package bo.capital.tec.pet.modules.pago.consumer;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.pago.command.CrearPagoCommand;
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
public class CrearPagoCommandConsumer {

    private final PagoService pagoService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${app.kafka.topic.comando-crear-pago}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onCrearPago(@Payload CrearPagoCommand comando, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(comando.getCommandId(), "CrearPagoCommand",
                comando.getReservaId(), "payment-service", comando)) {
            log.debug("Comando CrearPago duplicado, ignorado: {}", comando.getCommandId());
        } else {
            pagoService.crearPago(comando);
        }
        ack.acknowledge();
    }
}
