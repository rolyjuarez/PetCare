package bo.capital.tec.pet.common.messaging.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageMetrics {

    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> publishedCounters = new ConcurrentHashMap<>();
    private final Map<String, Counter> consumedCounters = new ConcurrentHashMap<>();
    private final Map<String, Counter> errorCounters = new ConcurrentHashMap<>();
    private final Map<String, Timer> latencyTimers = new ConcurrentHashMap<>();

    public MessageMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementPublished(String topic) {
        counter(publishedCounters, "petcare.messages.published", topic).increment();
    }

    public void incrementConsumed(String topic) {
        counter(consumedCounters, "petcare.messages.consumed", topic).increment();
    }

    public void incrementError(String topic) {
        counter(errorCounters, "petcare.messages.error", topic).increment();
    }

    public void recordLatency(String topic, Duration duration) {
        timer(latencyTimers, "petcare.messages.latency", topic).record(duration);
    }

    private Counter counter(Map<String, Counter> cache, String name, String topic) {
        return cache.computeIfAbsent(topic, t -> Counter.builder(name)
                .description("Message count for topic " + t)
                .tag("topic", t)
                .register(meterRegistry));
    }

    private Timer timer(Map<String, Timer> cache, String name, String topic) {
        return cache.computeIfAbsent(topic, t -> Timer.builder(name)
                .description("Message latency for topic " + t)
                .tag("topic", t)
                .register(meterRegistry));
    }
}
