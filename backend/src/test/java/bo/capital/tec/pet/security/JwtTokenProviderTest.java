package bo.capital.tec.pet.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "PETCareSecretKeyForJWTTokenGeneration2024VeryLongAndSecureKeyMustBe256Bits",
                3600000L, 604800000L);
    }

    @Test
    void generateAccessToken_ShouldReturnToken() {
        String token = jwtTokenProvider.generateAccessToken("admin");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUsernameFromToken_ShouldReturnUsername() {
        String token = jwtTokenProvider.generateAccessToken("admin");
        String username = jwtTokenProvider.getUsernameFromToken(token);
        assertEquals("admin", username);
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtTokenProvider.generateAccessToken("admin");
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void generateRefreshToken_ShouldReturnToken() {
        String token = jwtTokenProvider.generateRefreshToken("admin");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getExpirationMs_ShouldReturnValue() {
        assertEquals(3600000L, jwtTokenProvider.getExpirationMs());
    }
}
