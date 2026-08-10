package bo.capital.tec.pet;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.crypto.SecretKey;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "app.rate-limit.enabled=false",
        "jwt.secret=PETCareSecretKeyForJWTTokenGeneration2024VeryLongAndSecureKeyMustBe256Bits",
        "BACKEND_SERVICE_URL=http://localhost:8199",
        "RESERVATION_SERVICE_URL=http://localhost:8299",
        "PROVIDER_SERVICE_URL=http://localhost:8399",
        "PAYMENT_SERVICE_URL=http://localhost:8499"
})
class GatewayServiceApplicationTests {

    private static final String SECRET = "PETCareSecretKeyForJWTTokenGeneration2024VeryLongAndSecureKeyMustBe256Bits";

    @Autowired
    private WebTestClient webTestClient;

    private String validToken() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
        return Jwts.builder()
                .subject("gwtest01")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    @Test
    void healthIsUp() {
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void publicAuthPathDoesNotRequireToken() {
        webTestClient.get().uri("/api/v1/auth/login")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void invalidJwtRejected() {
        webTestClient.get().uri("/api/v1/clientes?size=1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void validJwtAcceptedAndRouted() {
        webTestClient.get().uri("/api/v1/reservas?size=1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void validJwtProviderRoute() {
        webTestClient.get().uri("/api/v1/proveedores?size=1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void validJwtPaymentRoute() {
        webTestClient.post().uri("/api/v1/pagos/1/procesar")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void unmatchedPathReturns404() {
        webTestClient.get().uri("/otra-cosa")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void corsPreflightAllowed() {
        webTestClient.options().uri("/api/v1/auth/login")
                .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200");
    }

    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @TestPropertySource(properties = {
            "app.rate-limit.enabled=true",
            "app.rate-limit.capacity=3",
            "app.rate-limit.refill-per-second=0.5"
    })
    static class RateLimitTest {

        @Autowired
        private WebTestClient webTestClient;

        @Test
        void burstOverCapacityReturns429() {
            for (int i = 0; i < 3; i++) {
                webTestClient.get().uri("/api/v1/auth/login")
                        .exchange()
                        .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY);
            }
            webTestClient.get().uri("/api/v1/auth/login")
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
