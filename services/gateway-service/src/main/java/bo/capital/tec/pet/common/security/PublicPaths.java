package bo.capital.tec.pet.common.security;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Component
public class PublicPaths {

    private final AntPathMatcher matcher = new AntPathMatcher();

    private final List<String> publicPatterns = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/refresh",
            "/uploads/**",
            "/api/v1/uploads/**",
            "/actuator/**",
            "/api/v1/actuator/**",
            "/api/v1/interna/**",
            "/api/v1/internal/**"
    );

    public boolean isPublic(String method, String path) {
        if (HttpMethod.OPTIONS.matches(method)) {
            return true;
        }
        return publicPatterns.stream().anyMatch(p -> matcher.match(p, path));
    }
}
