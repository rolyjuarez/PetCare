package bo.capital.tec.pet.saga;

import bo.capital.tec.pet.saga.domain.model.CancelarReservaCommand;
import bo.capital.tec.pet.saga.domain.model.ConfirmarReservaCommand;
import bo.capital.tec.pet.saga.domain.model.CrearPagoCommand;
import bo.capital.tec.pet.saga.domain.model.DomainEvent;
import bo.capital.tec.pet.saga.domain.model.EstadoSaga;
import bo.capital.tec.pet.saga.domain.model.LiberarDescuentoCommand;
import bo.capital.tec.pet.saga.domain.model.NotificarProveedorCommand;
import bo.capital.tec.pet.saga.domain.model.PagoFallidoEvent;
import bo.capital.tec.pet.saga.domain.model.PagoProcesadoEvent;
import bo.capital.tec.pet.saga.domain.model.PagoReembolsadoEvent;
import bo.capital.tec.pet.saga.domain.model.RechazarReservaCommand;
import bo.capital.tec.pet.saga.domain.model.ReservaAceptadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaCanceladaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaConfirmadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaCreadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaRechazadaEvent;
import bo.capital.tec.pet.saga.domain.model.SagaCommand;
import bo.capital.tec.pet.saga.domain.model.SagaEstado;
import bo.capital.tec.pet.saga.domain.port.out.EventoProcesadoRepository;
import bo.capital.tec.pet.saga.domain.port.out.SagaEstadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "test.reserva.creada",
        "test.reserva.aceptada",
        "test.reserva.rechazada",
        "test.reserva.confirmada",
        "test.reserva.cancelada",
        "test.pago.completado",
        "test.pago.fallido",
        "test.pago.reembolsado",
        "test.saga.comando.notificar-proveedor",
        "test.saga.comando.confirmar-reserva",
        "test.saga.comando.crear-pago",
        "test.saga.comando.cancelar-reserva",
        "test.saga.comando.rechazar-reserva",
        "test.saga.comando.liberar-descuento"})
class SagaFlowIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private SagaEstadoRepository sagaRepository;

    @Autowired
    private EventoProcesadoRepository eventoProcesadoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestCommandCollector collector;

    @BeforeEach
    void limpiar() {
        jdbcTemplate.update("DELETE FROM evento_procesado");
        jdbcTemplate.update("DELETE FROM saga_estado");
        collector.clear();
    }

    @Test
    void flujoFelizCompletaLaSaga() throws Exception {
        send(reservaCreada("ev-creada"), "test.reserva.creada");
        assertThat(awaitEstado(1L, EstadoSaga.PROVEEDOR_NOTIFICADO).getPasoActual())
                .contains("notificar-proveedor");
        NotificarProveedorCommand notificar = await(NotificarProveedorCommand.class);
        assertThat(notificar.getReservaId()).isEqualTo(1L);
        assertThat(notificar.getClienteNombre()).isEqualTo("Ana");

        send(reservaAceptada("ev-aceptada"), "test.reserva.aceptada");
        awaitEstado(1L, EstadoSaga.RESERVA_ACEPTADA);
        ConfirmarReservaCommand confirmar = await(ConfirmarReservaCommand.class);
        assertThat(confirmar.getProveedorId()).isEqualTo(7L);

        send(reservaConfirmada("ev-confirmada"), "test.reserva.confirmada");
        awaitEstado(1L, EstadoSaga.PAGO_SOLICITADO);
        CrearPagoCommand crearPago = await(CrearPagoCommand.class);
        assertThat(crearPago.getPrecioTotal()).isEqualByComparingTo("80.00");

        send(pagoProcesado("ev-pago"), "test.pago.completado");
        SagaEstado completada = awaitEstado(1L, EstadoSaga.COMPLETADA);
        assertThat(completada.getPasoActual()).contains("referencia REF-1");
    }

    @Test
    void pagoFallidoEjecutaCompensacionCompleta() throws Exception {
        send(reservaCreada("ev-creada-c"), "test.reserva.creada");
        awaitEstado(1L, EstadoSaga.PROVEEDOR_NOTIFICADO);
        await(NotificarProveedorCommand.class);

        send(reservaAceptada("ev-aceptada-c"), "test.reserva.aceptada");
        awaitEstado(1L, EstadoSaga.RESERVA_ACEPTADA);
        await(ConfirmarReservaCommand.class);

        send(reservaConfirmada("ev-confirmada-c"), "test.reserva.confirmada");
        awaitEstado(1L, EstadoSaga.PAGO_SOLICITADO);
        await(CrearPagoCommand.class);

        send(pagoFallido("ev-fallido", "Tarjeta rechazada"), "test.pago.fallido");
        SagaEstado fallido = awaitEstado(1L, EstadoSaga.PAGO_FALLIDO);
        assertThat(fallido.getMotivo()).isEqualTo("Tarjeta rechazada");

        CancelarReservaCommand cancelar = await(CancelarReservaCommand.class);
        assertThat(cancelar.getMotivo()).isEqualTo("Tarjeta rechazada");
        LiberarDescuentoCommand liberar = await(LiberarDescuentoCommand.class);
        assertThat(liberar.getReservaId()).isEqualTo(1L);

        send(reservaCancelada("ev-cancelada"), "test.reserva.cancelada");
        awaitEstado(1L, EstadoSaga.CANCELADA);
    }

    @Test
    void proveedorRechazaLaReserva() throws Exception {
        send(reservaCreada("ev-creada-r"), "test.reserva.creada");
        awaitEstado(1L, EstadoSaga.PROVEEDOR_NOTIFICADO);
        await(NotificarProveedorCommand.class);

        send(reservaRechazada("ev-rechazada"), "test.reserva.rechazada");
        SagaEstado rechazada = awaitEstado(1L, EstadoSaga.RECHAZADA);
        assertThat(rechazada.getMotivo()).isEqualTo("No disponible");

        RechazarReservaCommand rechazar = await(RechazarReservaCommand.class);
        assertThat(rechazar.getMotivo()).isEqualTo("No disponible");
    }

    @Test
    void eventoDuplicadoNoRepiteTransiciones() throws Exception {
        send(reservaCreada("ev-dup"), "test.reserva.creada");
        send(reservaCreada("ev-dup"), "test.reserva.creada");

        awaitEstado(1L, EstadoSaga.PROVEEDOR_NOTIFICADO);

        Integer sagas = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM saga_estado WHERE reserva_id = ?", Integer.class, 1L);
        assertThat(sagas).isEqualTo(1);
        assertThat(eventoProcesadoRepository.existsByEventId("ev-dup")).isTrue();

        TimeUnit.MILLISECONDS.sleep(500);
        assertThat(collector.comandos.size()).isEqualTo(1);
    }

    private void send(DomainEvent evento, String topic) throws Exception {
        kafkaTemplate.send(topic, String.valueOf(evento.getAggregateId()), evento)
                .get(10, TimeUnit.SECONDS);
    }

    private SagaEstado awaitEstado(Long reservaId, EstadoSaga estado) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            SagaEstado saga = sagaRepository.findByReservaId(reservaId).orElse(null);
            if (saga != null && saga.getEstado() == estado) {
                return saga;
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }
        throw new AssertionError("Saga no alcanzó estado " + estado);
    }

    @SuppressWarnings("unchecked")
    private <T> T await(Class<T> tipo) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            SagaCommand comando = collector.poll();
            if (comando == null) {
                TimeUnit.MILLISECONDS.sleep(200);
                continue;
            }
            if (tipo.isInstance(comando)) {
                return (T) comando;
            }
        }
        throw new AssertionError("Comando " + tipo.getSimpleName() + " no recibido");
    }

    private ReservaCreadaEvent reservaCreada(String eventId) {
        ReservaCreadaEvent evento = new ReservaCreadaEvent(
                1L, "RES-100", 2L, "Ana", "ana@mail.com",
                7L, "VetCorp", 3L, "Consulta", 4L, "Rex",
                LocalDate.now().plusDays(2), LocalTime.of(10, 0),
                new BigDecimal("80.00"), 5L, "EN_ESTABLECIMIENTO");
        evento.setEventId(eventId);
        return evento;
    }

    private ReservaAceptadaEvent reservaAceptada(String eventId) {
        ReservaAceptadaEvent evento = new ReservaAceptadaEvent(
                1L, "RES-100", 7L, "Dr. Juan", "VetCorp",
                "Consulta", Instant.now(), "Aceptada");
        evento.setEventId(eventId);
        return evento;
    }

    private ReservaRechazadaEvent reservaRechazada(String eventId) {
        ReservaRechazadaEvent evento = new ReservaRechazadaEvent(
                1L, "RES-100", 7L, "Dr. Juan", "VetCorp",
                "Consulta", "No disponible", Instant.now());
        evento.setEventId(eventId);
        return evento;
    }

    private ReservaConfirmadaEvent reservaConfirmada(String eventId) {
        ReservaConfirmadaEvent evento = new ReservaConfirmadaEvent(
                1L, "RES-100", 2L, 7L, 3L, 4L,
                LocalDate.now().plusDays(2), LocalTime.of(10, 0),
                new BigDecimal("80.00"), "EN_ESTABLECIMIENTO", 5L);
        evento.setEventId(eventId);
        return evento;
    }

    private ReservaCanceladaEvent reservaCancelada(String eventId) {
        ReservaCanceladaEvent evento = new ReservaCanceladaEvent(
                1L, "RES-100", 2L, "Ana", "ana@mail.com",
                7L, "VetCorp", 3L, "Consulta", "Pago fallido");
        evento.setEventId(eventId);
        return evento;
    }

    private PagoProcesadoEvent pagoProcesado(String eventId) {
        PagoProcesadoEvent evento = new PagoProcesadoEvent(
                50L, 1L, new BigDecimal("80.00"), new BigDecimal("100.00"),
                new BigDecimal("20.00"), "TARJETA", "APROBADO", "REF-1");
        evento.setEventId(eventId);
        return evento;
    }

    private PagoFallidoEvent pagoFallido(String eventId, String motivo) {
        PagoFallidoEvent evento = new PagoFallidoEvent(
                50L, 1L, new BigDecimal("80.00"), "INT-1", motivo);
        evento.setEventId(eventId);
        return evento;
    }

    private PagoReembolsadoEvent pagoReembolsado(String eventId) {
        PagoReembolsadoEvent evento = new PagoReembolsadoEvent(
                50L, 1L, new BigDecimal("80.00"), "REF-REEMB");
        evento.setEventId(eventId);
        return evento;
    }

    @TestConfiguration
    static class TestCommandCollectorConfig {

        @Bean
        TestCommandCollector testCommandCollector() {
            return new TestCommandCollector();
        }
    }

    static class TestCommandCollector {

        private final BlockingQueue<SagaCommand> comandos = new LinkedBlockingQueue<>();

        @KafkaListener(topics = "test.saga.comando.notificar-proveedor",
                groupId = "saga-test-collector-notificar",
                containerFactory = "kafkaListenerContainerFactory")
        public void onNotificar(NotificarProveedorCommand comando,
                                org.springframework.kafka.support.Acknowledgment ack) {
            comandos.offer(comando);
            ack.acknowledge();
        }

        @KafkaListener(topics = "test.saga.comando.confirmar-reserva",
                groupId = "saga-test-collector-confirmar",
                containerFactory = "kafkaListenerContainerFactory")
        public void onConfirmar(ConfirmarReservaCommand comando,
                                org.springframework.kafka.support.Acknowledgment ack) {
            comandos.offer(comando);
            ack.acknowledge();
        }

        @KafkaListener(topics = "test.saga.comando.crear-pago",
                groupId = "saga-test-collector-crearpago",
                containerFactory = "kafkaListenerContainerFactory")
        public void onCrearPago(CrearPagoCommand comando,
                                org.springframework.kafka.support.Acknowledgment ack) {
            comandos.offer(comando);
            ack.acknowledge();
        }

        @KafkaListener(topics = "test.saga.comando.cancelar-reserva",
                groupId = "saga-test-collector-cancelar",
                containerFactory = "kafkaListenerContainerFactory")
        public void onCancelar(CancelarReservaCommand comando,
                               org.springframework.kafka.support.Acknowledgment ack) {
            comandos.offer(comando);
            ack.acknowledge();
        }

        @KafkaListener(topics = "test.saga.comando.rechazar-reserva",
                groupId = "saga-test-collector-rechazar",
                containerFactory = "kafkaListenerContainerFactory")
        public void onRechazar(RechazarReservaCommand comando,
                               org.springframework.kafka.support.Acknowledgment ack) {
            comandos.offer(comando);
            ack.acknowledge();
        }

        @KafkaListener(topics = "test.saga.comando.liberar-descuento",
                groupId = "saga-test-collector-liberar",
                containerFactory = "kafkaListenerContainerFactory")
        public void onLiberar(LiberarDescuentoCommand comando,
                              org.springframework.kafka.support.Acknowledgment ack) {
            comandos.offer(comando);
            ack.acknowledge();
        }

        SagaCommand poll() {
            return comandos.poll();
        }

        int size() {
            return comandos.size();
        }

        void clear() {
            comandos.clear();
        }
    }
}
