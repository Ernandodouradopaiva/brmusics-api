package br.gov.ce.sps.projetoa.core.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

    private final AuthCookieService authCookieService;
    private final ObjectProvider<JwtDecoder> jwtDecoderProvider;
    private final DefaultBearerTokenResolver fallback = new DefaultBearerTokenResolver();

    public CookieBearerTokenResolver(
            AuthCookieService authCookieService,
            ObjectProvider<JwtDecoder> jwtDecoderProvider) {
        this.authCookieService = authCookieService;
        this.jwtDecoderProvider = jwtDecoderProvider;
    }

    @Override
    public String resolve(HttpServletRequest request) {
        if (PublicApiRequestMatcher.matches(request)) {
            return null;
        }
        String fromCookie = authCookieService.readAccessToken(request);
        if (fromCookie != null && !fromCookie.isBlank()) {
            return jwtValido(fromCookie) ? fromCookie : null;
        }
        return fallback.resolve(request);
    }

    private boolean jwtValido(String token) {
        try {
            jwtDecoderProvider.getObject().decode(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }
}
