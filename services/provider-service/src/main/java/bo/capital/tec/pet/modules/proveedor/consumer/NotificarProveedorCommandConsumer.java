package bo.capital.tec.pet.modules.proveedor.consumer;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.proveedor.command.NotificarProveedorCommand;
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
public class NotificarProveedorCommandConsumer {

    private final ProveedorService proveedorService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${app.kafka.topic.comando-notificar-proveedor}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onNotificarProveedor(@Payload NotificarProveedorCommand comando, Acknowledgment ack) {
        if (!idempotencyService.tryMarkProcessed(comando.getCommandId(), "NotificarProveedorCommand",
                comando.getReservaId(), "provider-service", comando)) {
            log.debug("Comando NotificarProveedor duplicado, ignorado: {}", comando.getCommandId());
        } else {
            proveedorService.procesarNotificarProveedor(comando);
        }
        ack.acknowledge();
    }
}
