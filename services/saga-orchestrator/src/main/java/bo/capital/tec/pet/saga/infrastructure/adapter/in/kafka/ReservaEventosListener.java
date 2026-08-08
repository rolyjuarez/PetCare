package bo.capital.tec.pet.saga.infrastructure.adapter.in.kafka;

import bo.capital.tec.pet.saga.domain.model.ReservaAceptadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaCanceladaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaConfirmadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaCreadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaRechazadaEvent;
import bo.capital.tec.pet.saga.domain.port.in.SagaCoordinator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada: recibe los eventos de reserva y delega en el caso de
 * uso del orquestador. El ack se confirma tras procesar el evento.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaEventosListener {

    private final SagaCoordinator coordinador;

    @KafkaListener(topics = "${app.kafka.topic.reserva-creada}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onReservaCreada(@Payload ReservaCreadaEvent evento, Acknowledgment ack) {
        coordinador.onReservaCreada(evento);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${app.kafka.topic.reserva-aceptada}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onReservaAceptada(@Payload ReservaAceptadaEvent evento, Acknowledgment ack) {
        coordinador.onReservaAceptada(evento);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${app.kafka.topic.reserva-rechazada}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onReservaRechazada(@Payload ReservaRechazadaEvent evento, Acknowledgment ack) {
        coordinador.onReservaRechazada(evento);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${app.kafka.topic.reserva-confirmada}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onReservaConfirmada(@Payload ReservaConfirmadaEvent evento, Acknowledgment ack) {
        coordinador.onReservaConfirmada(evento);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${app.kafka.topic.reserva-cancelada}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onReservaCancelada(@Payload ReservaCanceladaEvent evento, Acknowledgment ack) {
        coordinador.onReservaCancelada(evento);
        ack.acknowledge();
    }
}
