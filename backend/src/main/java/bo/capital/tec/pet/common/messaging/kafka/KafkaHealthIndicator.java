package bo.capital.tec.pet.common.messaging.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaAdmin kafkaAdmin;

    @Override
    public Health health() {
        try {
            var clusterId = kafkaAdmin.clusterId();
            if (clusterId != null && !clusterId.isEmpty()) {
                return Health.up()
                        .withDetail("clusterId", clusterId)
                        .build();
            }
            return Health.down()
                    .withDetail("reason", "No cluster ID returned")
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("reason", e.getMessage())
                    .build();
        }
    }
}
