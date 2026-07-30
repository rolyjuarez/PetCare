package bo.capital.tec.pet.common.messaging.kafka;

import bo.capital.tec.pet.common.messaging.observability.MessageMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.DeserializationException;

@Slf4j
@RequiredArgsConstructor
public class KafkaErrorHandler implements CommonErrorHandler {

    private final MessageMetrics messageMetrics;

    public KafkaErrorHandler() {
        this.messageMetrics = null;
    }

    @Override
    public boolean handleOne(Exception thrownException, ConsumerRecord<?, ?> record,
                             Consumer<?, ?> consumer, MessageListenerContainer container) {
        log.error("Error processing message from topic={} partition={} offset={} key={}: {}",
                record.topic(), record.partition(), record.offset(), record.key(),
                thrownException.getMessage(), thrownException);

        if (messageMetrics != null) {
            messageMetrics.incrementError(record.topic());
        }

        if (thrownException.getCause() instanceof DeserializationException) {
            return true;
        }

        return false;
    }

    @Override
    public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer,
                                     MessageListenerContainer container, boolean batchListener) {
        log.error("Unexpected error in consumer: {}", thrownException.getMessage(), thrownException);
    }
}
