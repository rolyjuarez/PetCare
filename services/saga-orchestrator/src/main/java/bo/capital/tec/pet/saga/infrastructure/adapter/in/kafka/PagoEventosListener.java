package bo.capital.tec.pet.saga.infrastructure.adapter.in.kafka;

import bo.capital.tec.pet.saga.domain.model.PagoFallidoEvent;
import bo.capital.tec.pet.saga.domain.model.PagoProcesadoEvent;
import bo.capital.tec.pet.saga.domain.model.PagoReembolsadoEvent;
import bo.capital.tec.pet.saga.domain.port.in.SagaCoordinator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada: recibe los eventos de pago y delega en el caso de uso
 * del orquestador.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PagoEventosListener {

    private final SagaCoordinator coordinador;

    @KafkaListener(topics = "${app.kafka.topic.pago-completado}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onPagoProcesado(@Payload PagoProcesadoEvent evento, Acknowledgment ack) {
        coordinador.onPagoProcesado(evento);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${app.kafka.topic.pago-fallido}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onPagoFallido(@Payload PagoFallidoEvent evento, Acknowledgment ack) {
        coordinador.onPagoFallido(evento);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${app.kafka.topic.pago-reembolsado}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onPagoReembolsado(@Payload PagoReembolsadoEvent evento, Acknowledgment ack) {
        coordinador.onPagoReembolsado(evento);
        ack.acknowledge();
    }
}
