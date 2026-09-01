package br.gov.ce.sps.projetoa.core.security;

import br.gov.ce.sps.projetoa.api.exceptionhandler.Problem;
import br.gov.ce.sps.projetoa.api.exceptionhandler.ProblemType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Locale;

@Component
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public Http401UnauthorizedEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String userMessage = mensagemAmigavel(authException);

        Problem problem = Problem.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .timestamp(OffsetDateTime.now())
                .type(ProblemType.NAO_AUTENTICADO.getUri())
                .title(ProblemType.NAO_AUTENTICADO.getTitle())
                .detail(userMessage)
                .userMessage(userMessage)
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }

    private static String mensagemAmigavel(AuthenticationException authException) {
        String msg = authException.getMessage();
        if (msg == null || msg.isBlank()) {
            return "Sessão inválida ou expirada. Faça login novamente.";
        }

        String trimmed = msg.trim();
        String lower = trimmed.toLowerCase(Locale.ROOT);

        if (lower.contains("usuário não cadastrado") || lower.contains("usuario nao cadastrado")) {
            return trimmed;
        }
        if (lower.contains("usuário inativo") || lower.contains("usuario inativo")) {
            return trimmed;
        }
        if (authException instanceof BadCredentialsException) {
            if (lower.contains("bad credentials") || lower.contains("credenciais")) {
                return "CPF ou senha incorretos. Verifique seus dados e tente novamente.";
            }
            return trimmed;
        }
        if (lower.contains("token") && (lower.contains("expir") || lower.contains("invalid"))) {
            return "Sessão inválida ou expirada. Faça login novamente.";
        }
        if (lower.contains("bearer") || lower.contains("jwt")) {
            return "Token de acesso inválido ou ausente. Faça login novamente.";
        }

        return trimmed;
    }
}
