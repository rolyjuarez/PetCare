package bo.capital.tec.pet.common.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GatewayErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private final GatewayErrorWriter errorWriter;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = resolveStatus(ex);
        String message = status == HttpStatus.BAD_GATEWAY
                ? "Servicio de destino no disponible"
                : status == HttpStatus.GATEWAY_TIMEOUT
                ? "Tiempo de espera agotado en el servicio de destino"
                : ex.getMessage() != null ? ex.getMessage() : status.getReasonPhrase();
        log.warn("Error en gateway: {} {} -> {}", exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath(), status);
        return errorWriter.write(exchange, status, message);
    }

    private HttpStatus resolveStatus(Throwable ex) {
        Throwable t = ex;
        while (t != null) {
            if (t instanceof ConnectException) {
                return HttpStatus.BAD_GATEWAY;
            }
            if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
                return HttpStatus.GATEWAY_TIMEOUT;
            }
            if (t instanceof ResponseStatusException rse) {
                return HttpStatus.valueOf(rse.getStatusCode().value());
            }
            t = t.getCause();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
