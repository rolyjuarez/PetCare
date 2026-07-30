package bo.capital.tec.pet.common.messaging.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.common.config.TopicConfig.*;

@Configuration
@Profile("kafka")
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.reserva-event:petcare.reserva.event.v1}")
    private String reservaTopic;

    @Value("${app.kafka.topic.pago-event:petcare.pago.event.v1}")
    private String pagoTopic;

    @Value("${app.kafka.topic.usuario-event:petcare.usuario.event.v1}")
    private String usuarioTopic;

    @Value("${app.kafka.topic.mascota-event:petcare.mascota.event.v1}")
    private String mascotaTopic;

    @Value("${app.kafka.topic.dlt:petcare.internal.dlt.v1}")
    private String deadLetterTopic;

    private Map<String, String> eventTopicConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(RETENTION_MS_CONFIG, "259200000");
        config.put(RETENTION_BYTES_CONFIG, "-1");
        config.put(CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_DELETE);
        config.put(MIN_IN_SYNC_REPLICAS_CONFIG, "1");
        return config;
    }

    private Map<String, String> dltTopicConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(RETENTION_MS_CONFIG, "604800000");
        config.put(CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_DELETE);
        return config;
    }

    @Bean
    public NewTopic reservaEventTopic() {
        return new NewTopic(reservaTopic, 4, (short) 1)
                .configs(eventTopicConfig());
    }

    @Bean
    public NewTopic pagoEventTopic() {
        return new NewTopic(pagoTopic, 4, (short) 1)
                .configs(eventTopicConfig());
    }

    @Bean
    public NewTopic usuarioEventTopic() {
        return new NewTopic(usuarioTopic, 2, (short) 1)
                .configs(eventTopicConfig());
    }

    @Bean
    public NewTopic mascotaEventTopic() {
        return new NewTopic(mascotaTopic, 2, (short) 1)
                .configs(eventTopicConfig());
    }

    @Bean
    public NewTopic deadLetterTopic() {
        return new NewTopic(deadLetterTopic, 1, (short) 1)
                .configs(dltTopicConfig());
    }
}
