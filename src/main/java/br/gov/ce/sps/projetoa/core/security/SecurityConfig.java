package br.gov.ce.sps.projetoa.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LocalJwtAuthenticationConverter localJwtAuthenticationConverter;
    private final CookieBearerTokenResolver cookieBearerTokenResolver;

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            Http401UnauthorizedEntryPoint unauthorizedEntryPoint) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/auth/**",
                                "/usuarios/recuperar-senha",
                                "/whatsapp/webhook",
                                "/actuator/health"
                        ))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/logout").permitAll()
                        .requestMatchers("/auth/session").authenticated()
                        .requestMatchers("/usuarios/recuperar-senha").permitAll()
                        .requestMatchers("/publico/*").permitAll()
                        .requestMatchers("/docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/estados", "/estados/**", "/municipios/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/whatsapp/webhook").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedEntryPoint))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(bearerTokenResolver())
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(localJwtAuthenticationConverter))
                        .authenticationEntryPoint(unauthorizedEntryPoint));
        return http.build();
    }

    @Bean
    BearerTokenResolver bearerTokenResolver() {
        return cookieBearerTokenResolver;
    }
}
