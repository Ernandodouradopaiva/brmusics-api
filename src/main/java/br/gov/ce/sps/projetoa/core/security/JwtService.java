package br.gov.ce.sps.projetoa.core.security;

import br.gov.ce.sps.projetoa.core.security.exception.TokenJwtInvalidoException;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class JwtService {

    private static final String MENSAGEM_TOKEN_INVALIDO = "Token inválido ou expirado.";
    private final JwtDecoder decoder;
    private final JwtEncoder encoder;
    private final JwtProperties jwtProperties;

    public JwtService(JwtDecoder decoder, JwtEncoder encoder, JwtProperties jwtProperties) {
        this.decoder = decoder;
        this.encoder = encoder;
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();
        long expiration = jwtProperties.expirationSeconds() > 0
                ? jwtProperties.expirationSeconds()
                : 28800;
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .subject(usuario.getCodigo().toString())
                .claim("cpf", usuario.getCpf())
                .claim("nome", usuario.getNome());
        if (jwtProperties.issuer() != null && !jwtProperties.issuer().isBlank()) {
            builder.issuer(jwtProperties.issuer());
        }
        if (usuario.getEmail() != null) {
            builder.claim("email", usuario.getEmail());
        }
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                builder.build())).getTokenValue();
    }

    public long expirationSeconds() {
        return jwtProperties.expirationSeconds() > 0 ? jwtProperties.expirationSeconds() : 28800;
    }

    public Jwt validateAndDecode(String token) {
        if (token == null || token.isBlank()) {
            throw new TokenJwtInvalidoException(MENSAGEM_TOKEN_INVALIDO);
        }
        try {
            return decoder.decode(token);
        } catch (JwtException e) {
            log.warn("Falha ao validar JWT local: {}", rootMessage(e));
            throw new TokenJwtInvalidoException(MENSAGEM_TOKEN_INVALIDO, e);
        }
    }

    private static String rootMessage(Throwable ex) {
        Throwable cur = ex;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        String msg = cur.getMessage();
        return msg != null && !msg.isBlank() ? msg : cur.getClass().getSimpleName();
    }
}
