package bo.capital.tec.pet.common.filter;

import bo.capital.tec.pet.common.error.GatewayErrorWriter;
import bo.capital.tec.pet.common.security.JwtTokenProvider;
import bo.capital.tec.pet.common.security.PublicPaths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String X_AUTHENTICATED_USER = "X-Authenticated-User";

    private final JwtTokenProvider jwtTokenProvider;
    private final PublicPaths publicPaths;
    private final GatewayErrorWriter errorWriter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethod() != null ? request.getMethod().name() : "";
        String path = request.getURI().getPath();

        if (publicPaths.isPublic(method, path)) {
            return chain.filter(exchange);
        }

        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = header.substring(BEARER_PREFIX.length());
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("JWT invalido o expirado: {} {}", method, path);
            return errorWriter.write(exchange, HttpStatus.UNAUTHORIZED, "Token JWT invalido o expirado");
        }

        ServerHttpRequest mutated = request.mutate()
                .header(X_AUTHENTICATED_USER, jwtTokenProvider.getUsername(token))
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    @Override
    public int getOrder() {
        return -80;
    }
}
