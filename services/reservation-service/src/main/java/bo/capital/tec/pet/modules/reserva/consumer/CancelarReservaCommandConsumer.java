package bo.capital.tec.pet.modules.reserva.consumer;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.reserva.command.CancelarReservaCommand;
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
public class CancelarReservaCommandConsumer {

    private final ReservaService reservaService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${app.kafka.topic.comando-cancelar-reserva}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onCancelarReserva(@Payload CancelarReservaCommand comando, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(comando.getCommandId(), "CancelarReservaCommand",
                comando.getReservaId(), "reservation-service", comando)) {
            log.debug("Comando CancelarReserva duplicado, ignorado: {}", comando.getCommandId());
        } else {
            reservaService.compensarPorPagoFallido(comando);
        }
        ack.acknowledge();
    }
}
