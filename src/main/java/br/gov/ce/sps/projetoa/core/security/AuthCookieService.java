package br.gov.ce.sps.projetoa.core.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookieService {

    private final boolean secure;

    public AuthCookieService(@Value("${auth.cookie.secure:false}") boolean secure) {
        this.secure = secure;
    }

    public ResponseCookie accessTokenCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(AuthCookieNames.ACCESS_TOKEN, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .build();
    }

    public ResponseCookie clearAccessTokenCookie() {
        return ResponseCookie.from(AuthCookieNames.ACCESS_TOKEN, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }

    public String readAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (AuthCookieNames.ACCESS_TOKEN.equals(cookie.getName())
                    && cookie.getValue() != null
                    && !cookie.getValue().isBlank()) {
                return cookie.getValue().trim();
            }
        }
        return null;
    }
}
