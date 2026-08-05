package bo.capital.tec.pet.modules.reserva;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.entity.Reserva;
import bo.capital.tec.pet.modules.reserva.event.PagoFallidoEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaAceptadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCanceladaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCreadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaRechazadaEvent;
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
        "test.reserva.cancelada",
        "test.pago.fallido"})
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
    void consumirReservaAceptadaActualizaReserva() throws Exception {
        ReservaResponseDTO creada = reservaService.create(buildRequest());

        ReservaAceptadaEvent event = new ReservaAceptadaEvent(
                creada.getId(), creada.getCodigo(), 1L,
                "Maria Lopez", "VetPet SRL", "Consulta general", Instant.now(), null);
        kafkaTemplate.send("test.reserva.aceptada", String.valueOf(creada.getId()), event).get(10, TimeUnit.SECONDS);

        Reserva actualizada = awaitReserva(creada.getId(), "CONFIRMADA");
        assertThat(actualizada.getProveedorId()).isEqualTo(1L);
        assertThat(actualizada.getRespuestaEn()).isNotNull();

        assertThat(idempotencyService.isProcessed(event.getEventId())).isTrue();
        Integer notificaciones = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notificacion WHERE usuario_id = ? AND tipo = 'EXITO'",
                Integer.class, creada.getClienteId());
        assertThat(notificaciones).isEqualTo(1);
    }

    @Test
    void consumirReservaRechazadaGuardaMotivo() throws Exception {
        ReservaResponseDTO creada = reservaService.create(buildRequest());

        ReservaRechazadaEvent event = new ReservaRechazadaEvent(
                creada.getId(), creada.getCodigo(), 1L,
                "Maria Lopez", "VetPet SRL", "Consulta general",
                "Horario no disponible", Instant.now());
        kafkaTemplate.send("test.reserva.rechazada", String.valueOf(creada.getId()), event).get(10, TimeUnit.SECONDS);

        Reserva actualizada = awaitReserva(creada.getId(), "RECHAZADA");
        assertThat(actualizada.getMotivoRechazo()).isEqualTo("Horario no disponible");
        assertThat(actualizada.getProveedorId()).isEqualTo(1L);
        Integer notificaciones = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notificacion WHERE usuario_id = ? AND tipo = 'ADVERTENCIA'",
                Integer.class, creada.getClienteId());
        assertThat(notificaciones).isEqualTo(1);
    }

    @Test
    void eventoDuplicadoSeIgnora() throws Exception {
        ReservaResponseDTO creada = reservaService.create(buildRequest());

        ReservaAceptadaEvent event = new ReservaAceptadaEvent(
                creada.getId(), creada.getCodigo(), 1L,
                "Maria Lopez", "VetPet SRL", "Consulta general", Instant.now(), null);
        kafkaTemplate.send("test.reserva.aceptada", String.valueOf(creada.getId()), event).get(10, TimeUnit.SECONDS);

        awaitReserva(creada.getId(), "CONFIRMADA");
        Reserva trasPrimero = reservaMapper.selectById(creada.getId());
        int versionTrasPrimero = trasPrimero.getVersion();

        kafkaTemplate.send("test.reserva.aceptada", String.valueOf(creada.getId()), event).get(10, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(2);

        Reserva trasSegundo = reservaMapper.selectById(creada.getId());
        assertThat(trasSegundo.getVersion()).isEqualTo(versionTrasPrimero);
    }

    @Test
    void pagoFallidoCancelaReservaYPublicaCompensacion() throws Exception {
        ReservaResponseDTO creada = reservaService.create(buildRequest());

        PagoFallidoEvent event = new PagoFallidoEvent(
                creada.getId(), creada.getId(), new BigDecimal("80.00"),
                "INT-FALLIDO000001", "La transacción fue rechazada por la entidad emisora");
        kafkaTemplate.send("test.pago.fallido", String.valueOf(creada.getId()), event).get(10, TimeUnit.SECONDS);

        Reserva cancelada = awaitReservaCancelada(creada.getId());
        assertThat(cancelada.getEstadoReservaId()).isEqualTo(4L);
        assertThat(idempotencyService.isProcessed(event.getEventId())).isTrue();

        Integer notificaciones = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notificacion WHERE usuario_id = ? AND tipo = 'ADVERTENCIA'",
                Integer.class, creada.getClienteId());
        assertThat(notificaciones).isEqualTo(1);

        ReservaCanceladaEvent compensacion = eventCollector.canceladas().poll(10, TimeUnit.SECONDS);
        assertThat(compensacion).isNotNull();
        assertThat(compensacion.getReservaId()).isEqualTo(creada.getId());
        assertThat(compensacion.getMotivoCancelacion()).startsWith("Pago fallido");
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
