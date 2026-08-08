package bo.capital.tec.pet.modules.proveedor;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.proveedor.command.NotificarProveedorCommand;
import bo.capital.tec.pet.modules.proveedor.dto.ResponderSolicitudRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.SolicitudReservaResponseDTO;
import bo.capital.tec.pet.modules.proveedor.entity.SolicitudReserva;
import bo.capital.tec.pet.modules.proveedor.event.ReservaAceptadaEvent;
import bo.capital.tec.pet.modules.proveedor.event.ReservaRechazadaEvent;
import bo.capital.tec.pet.modules.proveedor.mapper.SolicitudReservaMapper;
import bo.capital.tec.pet.modules.proveedor.service.ProveedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "test.saga.comando.notificar-proveedor",
        "test.reserva.aceptada",
        "test.reserva.rechazada"})
class ProveedorFlowIntegrationTest {

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private SolicitudReservaMapper solicitudReservaMapper;

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
        jdbcTemplate.update("DELETE FROM solicitud_reserva");
        eventCollector.clear();
    }

    private NotificarProveedorCommand buildComando(Long proveedorId) {
        return new NotificarProveedorCommand(1L, "RES-20260731-001", 1L, "Ana Gomez",
                "ana@petcare.bo", proveedorId, "VetPet SRL",
                1L, "Consulta general", 1L, "Rex",
                LocalDate.now().plusDays(2), LocalTime.of(10, 0), new BigDecimal("80.00"),
                null, "EN_ESTABLECIMIENTO");
    }

    @Test
    void alRecibirComandoNotificarProveedorSeCreanSolicitudes() throws Exception {
        NotificarProveedorCommand comando = buildComando(null);

        kafkaTemplate.send("test.saga.comando.notificar-proveedor",
                String.valueOf(comando.getReservaId()), comando).get(10, TimeUnit.SECONDS);

        List<SolicitudReserva> solicitudes = awaitSolicitudes(1L, 2);
        assertThat(solicitudes).hasSize(2);
        assertThat(solicitudes).extracting(SolicitudReserva::getProveedorId)
                .containsExactlyInAnyOrder(1L, 2L);
        assertThat(solicitudes).allMatch(s ->
                "PENDIENTE".equals(s.getEstado()) && "RES-20260731-001".equals(s.getCodigo()));
    }

    @Test
    void aceptarSolicitudPublicaReservaAceptadaEvent() throws Exception {
        proveedorService.procesarNotificarProveedor(buildComando(1L));
        SolicitudReserva solicitud = solicitudReservaMapper.selectByReservaId(1L).get(0);

        SolicitudReservaResponseDTO resultado =
                proveedorService.aceptar(1L, solicitud.getId(),
                        new ResponderSolicitudRequestDTO(null, "Mascota en buen estado"));

        assertThat(resultado.getEstado()).isEqualTo("ACEPTADA");
        assertThat(resultado.getRespondidaEn()).isNotNull();

        ReservaAceptadaEvent event = eventCollector.aceptadas().poll(10, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
        assertThat(event.getReservaId()).isEqualTo(1L);
        assertThat(event.getProveedorId()).isEqualTo(1L);
        assertThat(event.getProveedorEmpresa()).isEqualTo("VetPet SRL");
        assertThat(event.getComentarioProveedor()).isEqualTo("Mascota en buen estado");

        SolicitudReserva actual = solicitudReservaMapper.selectById(solicitud.getId());
        assertThat(actual.getEstado()).isEqualTo("ACEPTADA");
        assertThat(actual.getRespondidaEn()).isNotNull();
    }

    @Test
    void rechazarSolicitudPublicaReservaRechazadaEvent() throws Exception {
        proveedorService.procesarNotificarProveedor(buildComando(1L));
        SolicitudReserva solicitud = solicitudReservaMapper.selectByReservaId(1L).get(0);

        SolicitudReservaResponseDTO resultado =
                proveedorService.rechazar(1L, solicitud.getId(),
                        new ResponderSolicitudRequestDTO("Horario no disponible", null));        assertThat(resultado.getEstado()).isEqualTo("RECHAZADA");
        assertThat(resultado.getMotivoRechazo()).isEqualTo("Horario no disponible");

        ReservaRechazadaEvent event = eventCollector.rechazadas().poll(10, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
        assertThat(event.getReservaId()).isEqualTo(1L);
        assertThat(event.getProveedorId()).isEqualTo(1L);
        assertThat(event.getMotivoRechazo()).isEqualTo("Horario no disponible");

        SolicitudReserva actual = solicitudReservaMapper.selectById(solicitud.getId());
        assertThat(actual.getEstado()).isEqualTo("RECHAZADA");
        assertThat(actual.getMotivoRechazo()).isEqualTo("Horario no disponible");
    }

    @Test
    void comandoDuplicadoSeIgnora() throws Exception {
        NotificarProveedorCommand comando = buildComando(null);

        kafkaTemplate.send("test.saga.comando.notificar-proveedor",
                String.valueOf(comando.getReservaId()), comando).get(10, TimeUnit.SECONDS);
        awaitSolicitudes(1L, 2);

        kafkaTemplate.send("test.saga.comando.notificar-proveedor",
                String.valueOf(comando.getReservaId()), comando).get(10, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(2);

        assertThat(solicitudReservaMapper.selectByReservaId(1L)).hasSize(2);
    }

    @Test
    void listarSolicitudesPorProveedor() {
        proveedorService.procesarNotificarProveedor(buildComando(1L));

        PagedResponse<SolicitudReservaResponseDTO> pendientes =
                proveedorService.listarSolicitudes(1L, "PENDIENTE", 0, 20);
        assertThat(pendientes.getTotalElements()).isEqualTo(1);
        assertThat(pendientes.getContent().get(0).getClienteNombre()).isEqualTo("Ana Gomez");

        PagedResponse<SolicitudReservaResponseDTO> aceptadas =
                proveedorService.listarSolicitudes(1L, "ACEPTADA", 0, 20);
        assertThat(aceptadas.getTotalElements()).isZero();
    }

    private List<SolicitudReserva> awaitSolicitudes(Long reservaId, int expected)
            throws InterruptedException {
        List<SolicitudReserva> solicitudes = List.of();
        for (int i = 0; i < 50; i++) {
            solicitudes = solicitudReservaMapper.selectByReservaId(reservaId);
            if (solicitudes.size() >= expected) {
                return solicitudes;
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }
        throw new AssertionError("No se crearon las solicitudes esperadas para la reserva " + reservaId);
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class TestEventCollectorConfig {

        @org.springframework.context.annotation.Bean
        TestEventCollector testEventCollector() {
            return new TestEventCollector();
        }
    }

    static class TestEventCollector {

        private final BlockingQueue<ReservaAceptadaEvent> aceptadas = new LinkedBlockingQueue<>();
        private final BlockingQueue<ReservaRechazadaEvent> rechazadas = new LinkedBlockingQueue<>();

        @KafkaListener(topics = "test.reserva.aceptada",
                groupId = "provider-service-test-collector",
                containerFactory = "kafkaListenerContainerFactory")
        public void onAceptada(@Payload ReservaAceptadaEvent event, Acknowledgment ack) {
            aceptadas.offer(event);
            ack.acknowledge();
        }

        @KafkaListener(topics = "test.reserva.rechazada",
                groupId = "provider-service-test-collector",
                containerFactory = "kafkaListenerContainerFactory")
        public void onRechazada(@Payload ReservaRechazadaEvent event, Acknowledgment ack) {
            rechazadas.offer(event);
            ack.acknowledge();
        }

        BlockingQueue<ReservaAceptadaEvent> aceptadas() {
            return aceptadas;
        }

        BlockingQueue<ReservaRechazadaEvent> rechazadas() {
            return rechazadas;
        }

        void clear() {
            aceptadas.clear();
            rechazadas.clear();
        }
    }
}
