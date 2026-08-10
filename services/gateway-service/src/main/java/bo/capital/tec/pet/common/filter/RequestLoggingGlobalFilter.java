package bo.capital.tec.pet.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestLoggingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        return chain.filter(exchange).doFinally(signal -> {
            long duration = System.currentTimeMillis() - start;
            String method = exchange.getRequest().getMethod() != null ? exchange.getRequest().getMethod().name() : "?";
            String path = exchange.getRequest().getURI().getPath();
            int status = exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode().value() : 0;
            log.info("{} {} -> {} ({} ms)", method, path, status, duration);
        });
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
