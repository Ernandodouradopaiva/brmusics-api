package br.gov.ce.sps.projetoa.core.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public final class UsuarioSecurity {

    private UsuarioSecurity() {}

    @Target(METHOD)
    @Retention(RUNTIME)
    @PreAuthorize("hasAuthority('USUARIO_LISTAR') or hasAuthority('USUARIO_GERENCIAR')")
    public @interface PodeListar {}

    @Target(METHOD)
    @Retention(RUNTIME)
    @PreAuthorize("hasAuthority('USUARIO_GERENCIAR')")
    public @interface PodeGerenciar {}
}
