package bo.capital.tec.pet.modules.reserva.consumer;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.reserva.command.ConfirmarReservaCommand;
import bo.capital.tec.pet.modules.reserva.command.RechazarReservaCommand;
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
public class ConfirmarRechazarCommandConsumer {

    private final ReservaService reservaService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${app.kafka.topic.comando-confirmar-reserva}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onConfirmarReserva(@Payload ConfirmarReservaCommand comando, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(comando.getCommandId(), "ConfirmarReservaCommand",
                comando.getReservaId(), "reservation-service", comando)) {
            log.debug("Comando ConfirmarReserva duplicado, ignorado: {}", comando.getCommandId());
        } else {
            reservaService.aplicarAceptacion(comando);
        }
        ack.acknowledge();
    }

    @KafkaListener(topics = "${app.kafka.topic.comando-rechazar-reserva}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onRechazarReserva(@Payload RechazarReservaCommand comando, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(comando.getCommandId(), "RechazarReservaCommand",
                comando.getReservaId(), "reservation-service", comando)) {
            log.debug("Comando RechazarReserva duplicado, ignorado: {}", comando.getCommandId());
        } else {
            reservaService.aplicarRechazo(comando);
        }
        ack.acknowledge();
    }
}
