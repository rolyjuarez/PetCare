package bo.capital.tec.pet.modules.pago;

import bo.capital.tec.pet.common.kafka.IdempotencyService;
import bo.capital.tec.pet.modules.pago.command.CrearPagoCommand;
import bo.capital.tec.pet.modules.pago.dto.DatosTarjetaDTO;
import bo.capital.tec.pet.modules.pago.dto.PagoResponseDTO;
import bo.capital.tec.pet.modules.pago.dto.ProcesarPagoRequestDTO;
import bo.capital.tec.pet.modules.pago.entity.Pago;
import bo.capital.tec.pet.modules.pago.event.PagoFallidoEvent;
import bo.capital.tec.pet.modules.pago.event.PagoProcesadoEvent;
import bo.capital.tec.pet.modules.pago.event.PagoReembolsadoEvent;
import bo.capital.tec.pet.modules.pago.mapper.PagoMapper;
import bo.capital.tec.pet.modules.pago.service.PagoService;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "test.saga.comando.crear-pago",
        "test.pago.completado",
        "test.pago.fallido",
        "test.pago.reembolsado"})
class PagoFlowIntegrationTest {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoMapper pagoMapper;

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
        jdbcTemplate.update("DELETE FROM pago");
        eventCollector.clear();
    }

    private CrearPagoCommand buildComando(Long reservaId, String modalidad) {
        return new CrearPagoCommand(reservaId, "RES-00000001", 1L, 1L, 1L, 1L,
                LocalDate.now().plusDays(2), LocalTime.of(10, 0),
                new BigDecimal("100.00"), modalidad, null);
    }

    private ProcesarPagoRequestDTO buildRequest(String numero) {
        return ProcesarPagoRequestDTO.builder()
                .metodoPago("TARJETA_CREDITO")
                .tarjeta(DatosTarjetaDTO.builder()
                        .numero(numero)
                        .titular("Ana Gomez")
                        .expira("12/28")
                        .cvv("123")
                        .build())
                .build();
    }

    @Test
    void reservaConfirmadaCreaPagoPendiente() {
        PagoResponseDTO pago = pagoService.crearPago(buildComando(1L, "DOMICILIO"));

        assertThat(pago.getId()).isNotNull();
        assertThat(pago.getReservaId()).isEqualTo(1L);
        assertThat(pago.getEstadoSync()).isEqualTo("PENDIENTE");
        assertThat(pago.getModalidadPago()).isEqualTo("EN_LINEA");
        assertThat(pago.getMonto()).isEqualByComparingTo("100.00");
    }

    @Test
    void procesarPagoOnlineAplicaPromocionYRecargo() {
        PagoResponseDTO creado = pagoService.crearPago(buildComando(1L, "DOMICILIO"));

        PagoResponseDTO procesado = pagoService.procesar(creado.getId(), buildRequest("4242424242424242"));

        assertThat(procesado.getEstadoSync()).isEqualTo("COMPLETADO");
        assertThat(procesado.getEstadoPago()).isEqualTo("COMPLETADO");
        assertThat(procesado.getMonto()).isEqualByComparingTo("110.00");
        assertThat(procesado.getMontoOriginal()).isEqualByComparingTo("100.00");
        assertThat(procesado.getDescuentoTotal()).isEqualByComparingTo("10.00");
        assertThat(procesado.getReferenciaTransaccion()).isNotBlank();
        assertThat(procesado.getIntencionId()).startsWith("INT-");
        assertThat(procesado.getFechaPago()).isNotNull();
    }

    @Test
    void tarjetaRechazadaDejaPagoFallido() {
        PagoResponseDTO creado = pagoService.crearPago(buildComando(1L, "DOMICILIO"));

        PagoResponseDTO procesado = pagoService.procesar(creado.getId(), buildRequest("4000000000000000"));

        assertThat(procesado.getEstadoSync()).isEqualTo("FALLIDO");
        assertThat(procesado.getEstadoPago()).isEqualTo("FALLIDO");
        assertThat(procesado.getIntencionId()).startsWith("INT-");
    }

    @Test
    void pagoEnEstablecimientoSeCompletaSinPasarela() {
        PagoResponseDTO creado = pagoService.crearPago(buildComando(2L, "EN_ESTABLECIMIENTO"));

        PagoResponseDTO procesado = pagoService.procesar(creado.getId(),
                ProcesarPagoRequestDTO.builder().metodoPago("EFECTIVO").build());

        assertThat(procesado.getEstadoSync()).isEqualTo("COMPLETADO");
        assertThat(procesado.getMetodoPago()).isEqualTo("EFECTIVO");
        assertThat(procesado.getReferenciaTransaccion()).isEqualTo("EFECTIVO-EN-ESTABLECIMIENTO");
    }

    @Test
    void reembolsarPagoCompletadoCambiaEstado() {
        PagoResponseDTO creado = pagoService.crearPago(buildComando(2L, "EN_ESTABLECIMIENTO"));
        pagoService.procesar(creado.getId(), ProcesarPagoRequestDTO.builder().metodoPago("EFECTIVO").build());

        PagoResponseDTO reembolsado = pagoService.reembolsar(creado.getId());

        assertThat(reembolsado.getEstadoSync()).isEqualTo("REEMBOLSADO");
    }

    @Test
    void pagoFallidoPublicaPagoFallidoEvent() throws Exception {
        PagoResponseDTO creado = pagoService.crearPago(buildComando(1L, "DOMICILIO"));

        PagoResponseDTO procesado = pagoService.procesar(creado.getId(), buildRequest("4000000000000000"));

        assertThat(procesado.getEstadoSync()).isEqualTo("FALLIDO");
        PagoFallidoEvent event = eventCollector.fallidos().poll(10, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
        assertThat(event.getReservaId()).isEqualTo(1L);
        assertThat(event.getPagoId()).isEqualTo(creado.getId());
        assertThat(event.getIntencionId()).startsWith("INT-");
        assertThat(event.getMotivo()).isNotBlank();
    }

    @Test
    void reembolsoPublicaPagoReembolsadoEvent() throws Exception {
        PagoResponseDTO creado = pagoService.crearPago(buildComando(2L, "EN_ESTABLECIMIENTO"));
        pagoService.procesar(creado.getId(), ProcesarPagoRequestDTO.builder().metodoPago("EFECTIVO").build());

        PagoResponseDTO reembolsado = pagoService.reembolsar(creado.getId());

        assertThat(reembolsado.getEstadoSync()).isEqualTo("REEMBOLSADO");
        PagoReembolsadoEvent event = eventCollector.reembolsados().poll(10, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
        assertThat(event.getReservaId()).isEqualTo(2L);
        assertThat(event.getPagoId()).isEqualTo(creado.getId());
    }

    @Test
    void consumirComandoCrearPagoPorKafkaCreaPago() throws Exception {
        CrearPagoCommand comando = buildComando(2L, "EN_ESTABLECIMIENTO");
        kafkaTemplate.send("test.saga.comando.crear-pago", String.valueOf(comando.getReservaId()), comando)
                .get(10, TimeUnit.SECONDS);

        Pago pago = awaitPago(2L);
        assertThat(pago).isNotNull();
        assertThat(pago.getModalidadPago()).isEqualTo("EN_ESTABLECIMIENTO");
        assertThat(idempotencyService.isProcessed(comando.getCommandId())).isTrue();
    }

    @Test
    void comandoDuplicadoSeIgnora() throws Exception {
        CrearPagoCommand comando = buildComando(2L, "EN_ESTABLECIMIENTO");
        kafkaTemplate.send("test.saga.comando.crear-pago", String.valueOf(comando.getReservaId()), comando)
                .get(10, TimeUnit.SECONDS);
        awaitPago(2L);

        kafkaTemplate.send("test.saga.comando.crear-pago", String.valueOf(comando.getReservaId()), comando)
                .get(10, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(2);

        Pago pago = pagoMapper.selectByReservaId(2L);
        Integer cantidad = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pago WHERE reserva_id = ?", Integer.class, 2L);
        assertThat(cantidad).isEqualTo(1);
        assertThat(pago.getVersion()).isEqualTo(1);
    }

    private Pago awaitPago(Long reservaId) throws InterruptedException {
        Pago pago = null;
        for (int i = 0; i < 50; i++) {
            pago = pagoMapper.selectByReservaId(reservaId);
            if (pago != null) {
                return pago;
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }
        return null;
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class TestEventCollectorConfig {

        @org.springframework.context.annotation.Bean
        TestEventCollector testEventCollector() {
            return new TestEventCollector();
        }
    }

    static class TestEventCollector {

        private final BlockingQueue<PagoProcesadoEvent> procesados = new LinkedBlockingQueue<>();
        private final BlockingQueue<PagoFallidoEvent> fallidos = new LinkedBlockingQueue<>();
        private final BlockingQueue<PagoReembolsadoEvent> reembolsados = new LinkedBlockingQueue<>();

        @KafkaListener(topics = "test.pago.completado",
                groupId = "payment-service-test-collector",
                containerFactory = "kafkaListenerContainerFactory")
        public void onProcesado(@Payload PagoProcesadoEvent event, Acknowledgment ack) {
            procesados.offer(event);
            ack.acknowledge();
        }

        @KafkaListener(topics = "test.pago.fallido",
                groupId = "payment-service-test-collector",
                containerFactory = "kafkaListenerContainerFactory")
        public void onFallido(@Payload PagoFallidoEvent event, Acknowledgment ack) {
            fallidos.offer(event);
            ack.acknowledge();
        }

        @KafkaListener(topics = "test.pago.reembolsado",
                groupId = "payment-service-test-collector",
                containerFactory = "kafkaListenerContainerFactory")
        public void onReembolsado(@Payload PagoReembolsadoEvent event, Acknowledgment ack) {
            reembolsados.offer(event);
            ack.acknowledge();
        }

        BlockingQueue<PagoProcesadoEvent> procesados() {
            return procesados;
        }

        BlockingQueue<PagoFallidoEvent> fallidos() {
            return fallidos;
        }

        BlockingQueue<PagoReembolsadoEvent> reembolsados() {
            return reembolsados;
        }

        void clear() {
            procesados.clear();
            fallidos.clear();
            reembolsados.clear();
        }
    }
}
