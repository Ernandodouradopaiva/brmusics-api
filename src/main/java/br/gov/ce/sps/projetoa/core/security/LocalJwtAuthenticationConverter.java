package br.gov.ce.sps.projetoa.core.security;

import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocalJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioPermissaoResolver usuarioPermissaoResolver;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UUID codigo;
        try {
            codigo = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException ex) {
            throw new BadCredentialsException("Token inválido.");
        }
        Usuario usuario = usuarioRepository.findByCodigoWithGrupos(codigo)
                .orElseThrow(() -> new BadCredentialsException("Usuário não cadastrado."));
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new BadCredentialsException(UsuarioSecurityMessages.USUARIO_INATIVO);
        }
        List<GrantedAuthority> authorities = usuarioPermissaoResolver.resolverChaves(usuario).stream()
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .map(a -> (GrantedAuthority) a)
                .toList();
        return new JwtAuthenticationToken(jwt, authorities, usuario.getCpf());
    }
}
