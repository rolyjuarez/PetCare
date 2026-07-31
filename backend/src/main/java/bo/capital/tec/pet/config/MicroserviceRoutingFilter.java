package bo.capital.tec.pet.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MicroserviceRoutingFilter extends OncePerRequestFilter {

    private static final String CONTEXT_PATH = "/api/v1";
    private static final List<String> ROUTED_HEADERS =
            List.of("Authorization", "Content-Type", "Accept", "Accept-Language", "Origin", "X-Requested-With");

    private final String reservationBase;
    private final String providerBase;

    public MicroserviceRoutingFilter(
            @Value("${app.microservices.reservation:http://localhost:8081}") String reservationBase,
            @Value("${app.microservices.provider:http://localhost:8082}") String providerBase) {
        this.reservationBase = reservationBase;
        this.providerBase = providerBase;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String target = resolveTarget(uri);
        if (target == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
            target += "?" + query;
        }
        proxy(request, response, target);
    }

    private String resolveTarget(String uri) {
        if (uri.startsWith(CONTEXT_PATH + "/reservas")) {
            return reservationBase + uri;
        }
        if (uri.startsWith(CONTEXT_PATH + "/proveedor")) {
            return providerBase + uri;
        }
        return null;
    }

    private void proxy(HttpServletRequest request, HttpServletResponse response, String target) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(target).openConnection();
            conn.setRequestMethod(request.getMethod());
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(false);

            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (ROUTED_HEADERS.contains(name)) {
                    conn.setRequestProperty(name, request.getHeader(name));
                }
            }

            boolean hasBody = !"GET".equals(request.getMethod()) && !"DELETE".equals(request.getMethod());
            if (hasBody) {
                conn.setDoOutput(true);
                try (OutputStream out = conn.getOutputStream()) {
                    StreamUtils.copy(request.getInputStream(), out);
                }
            }

            conn.connect();
            int code = conn.getResponseCode();
            response.setStatus(code);

            String contentType = conn.getContentType();
            if (contentType != null) {
                response.setContentType(contentType);
            }

            InputStream body = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
            if (body != null) {
                try (InputStream in = body) {
                    StreamUtils.copy(in, response.getOutputStream());
                }
                response.flushBuffer();
            }
            log.debug("Ruteado {} {} -> {}", request.getMethod(), request.getRequestURI(), code);
        } catch (IOException e) {
            log.warn("Error enrutando {} {} hacia {}: {}", request.getMethod(), request.getRequestURI(), target, e.getMessage());
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Microservicio no disponible");
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
