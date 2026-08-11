package bo.capital.tec.pet.common.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

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

    @Value("${app.kafka.topic.pago-completado:pago.completado}")
    private String pagoCompletadoTopic;

    @Value("${app.kafka.topic.pago-fallido:pago.fallido}")
    private String pagoFallidoTopic;

    @Value("${app.kafka.topic.pago-reembolsado:pago.reembolsado}")
    private String pagoReembolsadoTopic;

    @Value("${app.kafka.topic.dlt:reserva.dlt.v1}")
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
        return TopicBuilder.name(reservaCreadaTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", String.valueOf(retentionMs))
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic reservaAceptadaTopic() {
        return TopicBuilder.name(reservaAceptadaTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", String.valueOf(retentionMs))
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic reservaRechazadaTopic() {
        return TopicBuilder.name(reservaRechazadaTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", String.valueOf(retentionMs))
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic reservaConfirmadaTopic() {
        return TopicBuilder.name(reservaConfirmadaTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", String.valueOf(retentionMs))
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic pagoCompletadoTopic() {
        return TopicBuilder.name(pagoCompletadoTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", String.valueOf(retentionMs))
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic pagoFallidoTopic() {
        return TopicBuilder.name(pagoFallidoTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", String.valueOf(retentionMs))
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic pagoReembolsadoTopic() {
        return TopicBuilder.name(pagoReembolsadoTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", String.valueOf(retentionMs))
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name(deadLetterTopic)
                .partitions(1)
                .replicas(replicationFactor)
                .config("retention.ms", String.valueOf(retentionMs))
                .build();
    }
}
