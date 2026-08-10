package bo.capital.tec.pet.common.filter;

import bo.capital.tec.pet.common.error.GatewayErrorWriter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Duration;

@Slf4j
@Component
public class RateLimiterGlobalFilter implements GlobalFilter, Ordered {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private final long capacity;
    private final double refillPerSecond;
    private final boolean enabled;
    private final GatewayErrorWriter errorWriter;
    private final Cache<String, TokenBucket> buckets;

    public RateLimiterGlobalFilter(
            @Value("${app.rate-limit.capacity:60}") long capacity,
            @Value("${app.rate-limit.refill-per-second:1.0}") double refillPerSecond,
            @Value("${app.rate-limit.enabled:true}") boolean enabled,
            GatewayErrorWriter errorWriter) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.enabled = enabled;
        this.errorWriter = errorWriter;
        this.buckets = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(10))
                .maximumSize(100_000)
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!enabled) {
            return chain.filter(exchange);
        }
        String key = resolveKey(exchange);
        TokenBucket bucket = buckets.get(key, k -> new TokenBucket(capacity, refillPerSecond));
        if (!bucket.tryAcquire()) {
            log.warn("Rate limit excedido para {} en {}", key, exchange.getRequest().getURI().getPath());
            exchange.getResponse().getHeaders().set("X-RateLimit-Exceeded", "true");
            return errorWriter.write(exchange, HttpStatus.TOO_MANY_REQUESTS,
                    "Demasiadas peticiones, intente nuevamente en unos segundos");
        }
        return chain.filter(exchange);
    }

    private String resolveKey(ServerWebExchange exchange) {
        String forwarded = exchange.getRequest().getHeaders().getFirst(X_FORWARDED_FOR);
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        InetSocketAddress remote = exchange.getRequest().getRemoteAddress();
        return remote != null && remote.getAddress() != null ? remote.getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() {
        return -90;
    }

    static final class TokenBucket {
        private final double capacity;
        private final double refillPerSecond;
        private double tokens;
        private long lastRefillNanos;

        TokenBucket(double capacity, double refillPerSecond) {
            this.capacity = capacity;
            this.refillPerSecond = refillPerSecond;
            this.tokens = capacity;
            this.lastRefillNanos = System.nanoTime();
        }

        synchronized boolean tryAcquire() {
            long now = System.nanoTime();
            double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
            if (elapsedSeconds > 0) {
                tokens = Math.min(capacity, tokens + elapsedSeconds * refillPerSecond);
                lastRefillNanos = now;
            }
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }
    }
}
