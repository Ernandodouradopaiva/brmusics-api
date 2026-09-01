package br.gov.ce.sps.projetoa.core.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

/**
 * Rotas públicas da API em que o OAuth2 Resource Server não deve tentar autenticar
 * (evita 401 com cookie JWT expirado em endpoints permitAll).
 */
public final class PublicApiRequestMatcher {

    private static final List<RequestMatcher> MATCHERS = List.of(
            new AntPathRequestMatcher("/auth/login"),
            new AntPathRequestMatcher("/auth/logout"),
            new AntPathRequestMatcher("/usuarios/recuperar-senha"),
            new AntPathRequestMatcher("/publico/*"),
            new AntPathRequestMatcher("/actuator/health")
    );

    private PublicApiRequestMatcher() {
    }

    public static boolean matches(HttpServletRequest request) {
        for (RequestMatcher matcher : MATCHERS) {
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
}
