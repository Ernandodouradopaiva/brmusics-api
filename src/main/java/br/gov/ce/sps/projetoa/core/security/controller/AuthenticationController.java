package br.gov.ce.sps.projetoa.core.security.controller;

import br.gov.ce.sps.projetoa.api.input.LoginInput;
import br.gov.ce.sps.projetoa.core.security.AuthenticationModel;
import br.gov.ce.sps.projetoa.core.security.AuthCookieService;
import br.gov.ce.sps.projetoa.core.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthCookieService authCookieService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationModel> login(
            @Valid @RequestBody LoginInput input,
            HttpServletRequest httpRequest) {
        AuthenticationService.LoginResult result =
                authenticationService.login(input.cpf(), input.senha(), clientIp(httpRequest));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        authCookieService.accessTokenCookie(result.accessToken(), result.expiresIn()).toString())
                .body(result.model());
    }

    @GetMapping("/session")
    public ResponseEntity<AuthenticationModel> session(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            AuthenticationModel model = authenticationService.authenticateFromJwt(jwtAuth);
            return ResponseEntity.ok(model);
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookieService.clearAccessTokenCookie().toString())
                .build();
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
