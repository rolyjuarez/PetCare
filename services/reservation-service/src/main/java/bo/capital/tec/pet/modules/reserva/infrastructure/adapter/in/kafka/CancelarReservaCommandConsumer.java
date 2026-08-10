package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.in.kafka;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.reserva.application.command.CancelarReservaCommand;
import bo.capital.tec.pet.modules.reserva.application.command.ReservaCommandUseCase;
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

    private final ReservaCommandUseCase reservaCommandUseCase;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${app.kafka.topic.comando-cancelar-reserva}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onCancelarReserva(@Payload CancelarReservaCommand comando, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(comando.getCommandId(), "CancelarReservaCommand",
                comando.getReservaId(), "reservation-service", comando)) {
            log.debug("Comando CancelarReserva duplicado, ignorado: {}", comando.getCommandId());
        } else {
            reservaCommandUseCase.compensarPagoFallido(comando);
        }
        ack.acknowledge();
    }
}
