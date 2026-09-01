package br.gov.ce.sps.projetoa.core.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, String issuer, long expirationSeconds) {
}
