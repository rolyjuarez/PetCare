package bo.capital.tec.pet.saga.infrastructure.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Registra los topics de la saga: los de dominio (eventos) y los de comandos
 * que el orquestador publica hacia los microservicios.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.reserva-creada:reserva.creada}")
    private String reservaCreadaTopic;

    @Value("${app.kafka.topic.reserva-aceptada:reserva.aceptada}")
    private String reservaAceptadaTopic;

    @Value("${app.kafka.topic.reserva-rechazada:reserva.rechazada}")
    private String reservaRechazadaTopic;

    @Value("${app.kafka.topic.reserva-confirmada:reserva.confirmada}")
    private String reservaConfirmadaTopic;

    @Value("${app.kafka.topic.reserva-cancelada:reserva.cancelada}")
    private String reservaCanceladaTopic;

    @Value("${app.kafka.topic.pago-completado:pago.completado}")
    private String pagoCompletadoTopic;

    @Value("${app.kafka.topic.pago-fallido:pago.fallido}")
    private String pagoFallidoTopic;

    @Value("${app.kafka.topic.pago-reembolsado:pago.reembolsado}")
    private String pagoReembolsadoTopic;

    @Value("${app.kafka.topic.comando-notificar-proveedor:saga.comando.notificar-proveedor}")
    private String notificarProveedorTopic;

    @Value("${app.kafka.topic.comando-confirmar-reserva:saga.comando.confirmar-reserva}")
    private String confirmarReservaTopic;

    @Value("${app.kafka.topic.comando-crear-pago:saga.comando.crear-pago}")
    private String crearPagoTopic;

    @Value("${app.kafka.topic.comando-cancelar-reserva:saga.comando.cancelar-reserva}")
    private String cancelarReservaTopic;

    @Value("${app.kafka.topic.comando-rechazar-reserva:saga.comando.rechazar-reserva}")
    private String rechazarReservaTopic;

    @Value("${app.kafka.topic.comando-liberar-descuento:saga.comando.liberar-descuento}")
    private String liberarDescuentoTopic;

    @Value("${app.kafka.topic.dlt:saga.dlt.v1}")
    private String deadLetterTopic;

    @Value("${app.kafka.bootstrap-servers:localhost:9093}")
    private String bootstrapServers;

    @Value("${app.kafka.partitions:3}")
    private int partitions;

    @Value("${app.kafka.replication-factor:1}")
    private short replicationFactor;

    @Value("${app.kafka.retention-ms:604800000}")
    private long retentionMs;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic reservaCreadaTopic() {
        return buildTopic(reservaCreadaTopic);
    }

    @Bean
    public NewTopic reservaAceptadaTopic() {
        return buildTopic(reservaAceptadaTopic);
    }

    @Bean
    public NewTopic reservaRechazadaTopic() {
        return buildTopic(reservaRechazadaTopic);
    }

    @Bean
    public NewTopic reservaConfirmadaTopic() {
        return buildTopic(reservaConfirmadaTopic);
    }

    @Bean
    public NewTopic reservaCanceladaTopic() {
        return buildTopic(reservaCanceladaTopic);
    }

    @Bean
    public NewTopic pagoCompletadoTopic() {
        return buildTopic(pagoCompletadoTopic);
    }

    @Bean
    public NewTopic pagoFallidoTopic() {
        return buildTopic(pagoFallidoTopic);
    }

    @Bean
    public NewTopic pagoReembolsadoTopic() {
        return buildTopic(pagoReembolsadoTopic);
    }

    @Bean
    public NewTopic notificarProveedorTopic() {
        return buildTopic(notificarProveedorTopic);
    }

    @Bean
    public NewTopic confirmarReservaTopic() {
        return buildTopic(confirmarReservaTopic);
    }

    @Bean
    public NewTopic crearPagoTopic() {
        return buildTopic(crearPagoTopic);
    }

    @Bean
    public NewTopic cancelarReservaTopic() {
        return buildTopic(cancelarReservaTopic);
    }

    @Bean
    public NewTopic rechazarReservaTopic() {
        return buildTopic(rechazarReservaTopic);
    }

    @Bean
    public NewTopic liberarDescuentoTopic() {
        return buildTopic(liberarDescuentoTopic);
    }

    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name(deadLetterTopic)
                .partitions(1)
                .replicas(replicationFactor)
                .config("retention.ms", String.valueOf(retentionMs))
                .build();
    }

    private NewTopic buildTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", String.valueOf(retentionMs))
                .config("cleanup.policy", "delete")
                .build();
    }
}
