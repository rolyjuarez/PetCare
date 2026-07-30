package bo.capital.tec.pet.common.messaging.kafka;

import bo.capital.tec.pet.common.event.DomainEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("kafka")
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9093}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:petcare-consumer}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, DomainEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "bo.capital.tec.pet.*");
        config.put(JsonDeserializer.TYPE_MAPPINGS, getTypeMappings());
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, DomainEvent.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, DomainEvent>>
            kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DomainEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(kafkaErrorHandler());
        return factory;
    }

    @Bean
    public KafkaErrorHandler kafkaErrorHandler() {
        return new KafkaErrorHandler();
    }

    private String getTypeMappings() {
        return "ReservaCreadaEvent:bo.capital.tec.pet.modules.reserva.event.ReservaCreadaEvent,"
                + "ReservaConfirmadaEvent:bo.capital.tec.pet.modules.reserva.event.ReservaConfirmadaEvent,"
                + "ReservaCanceladaEvent:bo.capital.tec.pet.modules.reserva.event.ReservaCanceladaEvent,"
                + "ReservaCompletadaEvent:bo.capital.tec.pet.modules.reserva.event.ReservaCompletadaEvent,"
                + "PagoConfirmadoEvent:bo.capital.tec.pet.modules.pago.event.PagoConfirmadoEvent,"
                + "PagoReembolsadoEvent:bo.capital.tec.pet.modules.pago.event.PagoReembolsadoEvent,"
                + "UsuarioRegistradoEvent:bo.capital.tec.pet.modules.auth.event.UsuarioRegistradoEvent,"
                + "MascotaCreadaEvent:bo.capital.tec.pet.modules.mascota.event.MascotaCreadaEvent,"
                + "MascotaActualizadaEvent:bo.capital.tec.pet.modules.mascota.event.MascotaActualizadaEvent";
    }
}
