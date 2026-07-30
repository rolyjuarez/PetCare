package bo.capital.tec.pet.common.messaging.observability;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/events")
@RequiredArgsConstructor
public class EventMetricsController {

    private final MeterRegistry meterRegistry;

    @GetMapping("/metrics")
    public Map<String, Object> getEventMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        var published = meterRegistry.find("petcare.messages.published").counters();
        var consumed = meterRegistry.find("petcare.messages.consumed").counters();
        var errors = meterRegistry.find("petcare.messages.error").counters();

        Map<String, Long> publishedMap = new HashMap<>();
        published.forEach(c -> publishedMap.put(c.getId().getTag("topic"), (long) c.count()));

        Map<String, Long> consumedMap = new HashMap<>();
        consumed.forEach(c -> consumedMap.put(c.getId().getTag("topic"), (long) c.count()));

        Map<String, Long> errorMap = new HashMap<>();
        errors.forEach(c -> errorMap.put(c.getId().getTag("topic"), (long) c.count()));

        metrics.put("published", publishedMap);
        metrics.put("consumed", consumedMap);
        metrics.put("errors", errorMap);

        return metrics;
    }
}
