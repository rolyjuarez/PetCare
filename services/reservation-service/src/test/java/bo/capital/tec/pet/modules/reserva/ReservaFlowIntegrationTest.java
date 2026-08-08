package bo.capital.tec.pet.modules.reserva;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.reserva.command.CancelarReservaCommand;
import bo.capital.tec.pet.modules.reserva.command.ConfirmarReservaCommand;
import bo.capital.tec.pet.modules.reserva.command.RechazarReservaCommand;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.entity.Reserva;
import bo.capital.tec.pet.modules.reserva.event.ReservaCanceladaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCreadaEvent;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaMapper;
import bo.capital.tec.pet.modules.reserva.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
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
        "test.reserva.cancelada",
        "test.saga.comando.confirmar-reserva",
        "test.saga.comando.rechazar-reserva",
        "test.saga.comando.cancelar-reserva"})
class ReservaFlowIntegrationTest {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaMapper reservaMapper;

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private TestEventCollector eventCollector;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limpiarBase() {
        jdbcTemplate.update("DELETE FROM evento_procesado");
        jdbcTemplate.update("DELETE FROM reserva");
        jdbcTemplate.update("DELETE FROM notificacion");
        eventCollector.clear();
    }

    private ReservaRequestDTO buildRequest() {
        return ReservaRequestDTO.builder()
                .clienteId(1L)
                .servicioId(1L)
                .mascotaId(1L)
                .fechaReserva(LocalDate.now())
                .fechaInicio(LocalDate.now().plusDays(2))
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(11, 0))
                .precioTotal(new BigDecimal("80.00"))
                .build();
    }

    @Test
    void crearReservaPublicaReservaCreadaEvent() throws Exception {
        ReservaResponseDTO creada = reservaService.create(buildRequest());

        assertThat(creada.getId()).isNotNull();
        assertThat(creada.getCodigo()).startsWith("RES-");
        assertThat(creada.getEstadoReservaNombre()).isEqualTo("PENDIENTE");

        ReservaCreadaEvent event = eventCollector.creadas().poll(10, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
        assertThat(event.getReservaId()).isEqualTo(creada.getId());
        assertThat(event.getServicioId()).isEqualTo(1L);
        assertThat(event.getClienteNombre()).isEqualTo("Ana");
    }

    @Test
    void consumirComandoConfirmarReservaActualizaReserva() throws Exception {
        ReservaResponseDTO creada = reservaService.create(buildRequest());

        ConfirmarReservaCommand comando = new ConfirmarReservaCommand(
                creada.getId(), creada.getCodigo(), 1L,
                "Maria Lopez", "VetPet SRL", "Consulta general", null);
        kafkaTemplate.send("test.saga.comando.confirmar-reserva", String.valueOf(creada.getId()), comando)
                .get(10, TimeUnit.SECONDS);

        Reserva actualizada = awaitReserva(creada.getId(), "CONFIRMADA");
        assertThat(actualizada.getProveedorId()).isEqualTo(1L);
        assertThat(actualizada.getRespuestaEn()).isNotNull();

        assertThat(idempotencyService.isProcessed(comando.getCommandId())).isTrue();
        Integer notificaciones = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notificacion WHERE usuario_id = ? AND tipo = 'EXITO'",
                Integer.class, creada.getClienteId());
        assertThat(notificaciones).isEqualTo(1);
    }

    @Test
    void consumirComandoRechazarReservaGuardaMotivo() throws Exception {
        ReservaResponseDTO creada = reservaService.create(buildRequest());

        RechazarReservaCommand comando = new RechazarReservaCommand(
                creada.getId(), creada.getCodigo(), 1L,
                "Maria Lopez", "VetPet SRL", "Consulta general",
                "Horario no disponible");
        kafkaTemplate.send("test.saga.comando.rechazar-reserva", String.valueOf(creada.getId()), comando)
                .get(10, TimeUnit.SECONDS);

        Reserva actualizada = awaitReserva(creada.getId(), "RECHAZADA");
        assertThat(actualizada.getMotivoRechazo()).isEqualTo("Horario no disponible");
        assertThat(actualizada.getProveedorId()).isEqualTo(1L);
        Integer notificaciones = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notificacion WHERE usuario_id = ? AND tipo = 'ADVERTENCIA'",
                Integer.class, creada.getClienteId());
        assertThat(notificaciones).isEqualTo(1);
    }

    @Test
    void comandoDuplicadoSeIgnora() throws Exception {
        ReservaResponseDTO creada = reservaService.create(buildRequest());

        ConfirmarReservaCommand comando = new ConfirmarReservaCommand(
                creada.getId(), creada.getCodigo(), 1L,
                "Maria Lopez", "VetPet SRL", "Consulta general", null);
        kafkaTemplate.send("test.saga.comando.confirmar-reserva", String.valueOf(creada.getId()), comando)
                .get(10, TimeUnit.SECONDS);

        awaitReserva(creada.getId(), "CONFIRMADA");
        Reserva trasPrimero = reservaMapper.selectById(creada.getId());
        int versionTrasPrimero = trasPrimero.getVersion();

        kafkaTemplate.send("test.saga.comando.confirmar-reserva", String.valueOf(creada.getId()), comando)
                .get(10, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(2);

        Reserva trasSegundo = reservaMapper.selectById(creada.getId());
        assertThat(trasSegundo.getVersion()).isEqualTo(versionTrasPrimero);
    }

    @Test
    void comandoCancelarReservaEjecutaCompensacionYPublica() throws Exception {
        ReservaResponseDTO creada = reservaService.create(buildRequest());

        CancelarReservaCommand comando = new CancelarReservaCommand(
                creada.getId(), creada.getCodigo(),
                "La transacción fue rechazada por la entidad emisora");
        kafkaTemplate.send("test.saga.comando.cancelar-reserva", String.valueOf(creada.getId()), comando)
                .get(10, TimeUnit.SECONDS);

        Reserva cancelada = awaitReservaCancelada(creada.getId());
        assertThat(cancelada.getEstadoReservaId()).isEqualTo(4L);
        assertThat(idempotencyService.isProcessed(comando.getCommandId())).isTrue();

        Integer notificaciones = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notificacion WHERE usuario_id = ? AND tipo = 'ADVERTENCIA'",
                Integer.class, creada.getClienteId());
        assertThat(notificaciones).isEqualTo(1);

        ReservaCanceladaEvent compensacion = eventCollector.canceladas().poll(10, TimeUnit.SECONDS);
        assertThat(compensacion).isNotNull();
        assertThat(compensacion.getReservaId()).isEqualTo(creada.getId());
        assertThat(compensacion.getMotivo()).startsWith("Pago fallido");
    }

    private Reserva awaitReserva(Long id, String estadoNombre) throws InterruptedException {
        Reserva reserva = null;
        for (int i = 0; i < 50; i++) {
            Reserva actual = reservaMapper.selectById(id);
            String estado = actual.getEstadoReservaId() == 2 ? "CONFIRMADA"
                    : actual.getEstadoReservaId() == 3 ? "RECHAZADA" : "PENDIENTE";
            if (estadoNombre.equals(estado)) {
                reserva = actual;
                break;
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }
        assertThat(reserva).isNotNull();
        return reserva;
    }

    private Reserva awaitReservaCancelada(Long id) throws InterruptedException {
        Reserva reserva = null;
        for (int i = 0; i < 50; i++) {
            Reserva actual = reservaMapper.selectById(id);
            if (actual.getEstadoReservaId() != null && actual.getEstadoReservaId() == 4L) {
                reserva = actual;
                break;
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }
        assertThat(reserva).isNotNull();
        return reserva;
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class TestEventCollectorConfig {

        @org.springframework.context.annotation.Bean
        TestEventCollector testEventCollector() {
            return new TestEventCollector();
        }
    }

    static class TestEventCollector {

        private final BlockingQueue<ReservaCreadaEvent> creadas = new LinkedBlockingQueue<>();
        private final BlockingQueue<ReservaCanceladaEvent> canceladas = new LinkedBlockingQueue<>();

        @KafkaListener(topics = "test.reserva.creada",
                groupId = "reservation-service-test-collector",
                containerFactory = "kafkaListenerContainerFactory")
        public void onCreada(ReservaCreadaEvent event, org.springframework.kafka.support.Acknowledgment ack) {
            creadas.offer(event);
            ack.acknowledge();
        }

        @KafkaListener(topics = "test.reserva.cancelada",
                groupId = "reservation-service-test-collector",
                containerFactory = "kafkaListenerContainerFactory")
        public void onCancelada(ReservaCanceladaEvent event, org.springframework.kafka.support.Acknowledgment ack) {
            canceladas.offer(event);
            ack.acknowledge();
        }

        BlockingQueue<ReservaCreadaEvent> creadas() {
            return creadas;
        }

        BlockingQueue<ReservaCanceladaEvent> canceladas() {
            return canceladas;
        }

        void clear() {
            creadas.clear();
            canceladas.clear();
        }
    }
}
