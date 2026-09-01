package br.gov.ce.sps.projetoa.core.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;

@Component
public class SecurityUtil {

	private final ObjectProvider<UsuarioRepository> usuarioRepositoryProvider;

	public SecurityUtil(ObjectProvider<UsuarioRepository> usuarioRepositoryProvider) {
		this.usuarioRepositoryProvider = usuarioRepositoryProvider;
	}

	public Optional<Usuario> getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();
		UsuarioRepository repository = usuarioRepositoryProvider.getIfAvailable();
		if (repository == null) {
			return Optional.empty();
		}

		if (principal instanceof UserDetails userDetails) {
			return repository.findByCpf(userDetails.getUsername());
		}
		if (authentication instanceof JwtAuthenticationToken jwtAuth) {
			return buscarPorSubject(repository, jwtAuth.getToken().getSubject());
		}
		if (principal instanceof Jwt jwt) {
			return buscarPorSubject(repository, jwt.getSubject());
		}

		return Optional.empty();
	}

	private static Optional<Usuario> buscarPorSubject(UsuarioRepository repository, String subject) {
		try {
			return repository.findByCodigo(UUID.fromString(subject));
		} catch (IllegalArgumentException ex) {
			return repository.findByCpf(subject);
		}
	}
}
