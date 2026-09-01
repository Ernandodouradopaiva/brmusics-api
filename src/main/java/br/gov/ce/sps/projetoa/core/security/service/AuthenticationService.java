package br.gov.ce.sps.projetoa.core.security.service;

import br.gov.ce.sps.projetoa.core.security.AuthenticationModel;
import br.gov.ce.sps.projetoa.core.security.JwtService;
import br.gov.ce.sps.projetoa.core.security.UsuarioPermissaoResolver;
import br.gov.ce.sps.projetoa.core.security.UsuarioSecurityMessages;
import br.gov.ce.sps.projetoa.core.security.exception.TokenJwtInvalidoException;
import br.gov.ce.sps.projetoa.core.ratelimit.InMemoryRateLimiter;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;
import br.gov.ce.sps.projetoa.infrastructure.util.CpfUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String MENSAGEM_CREDENCIAIS_INVALIDAS = "CPF ou senha inválidos.";
    private static final String MENSAGEM_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";
    private static final int LOGIN_MAX_TENTATIVAS = 5;
    private static final long LOGIN_JANELA_SEGUNDOS = 60;

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioPermissaoResolver usuarioPermissaoResolver;
    private final PasswordEncoder passwordEncoder;
    private final InMemoryRateLimiter rateLimiter;

    public LoginResult login(String cpf, String senha, String clientIp) {
        String rateKey = "login:" + (clientIp == null || clientIp.isBlank() ? "unknown" : clientIp);
        if (!rateLimiter.tryConsume(rateKey, LOGIN_MAX_TENTATIVAS, LOGIN_JANELA_SEGUNDOS)) {
            throw new NegocioException("Muitas tentativas de login. Tente novamente em instantes.");
        }
        String cpfNormalizado = CpfUtils.normalizar(cpf);
        Usuario usuario = usuarioRepository.findByCpfWithGrupos(cpfNormalizado).orElse(null);
        if (usuario == null
                || usuario.getSenha() == null
                || usuario.getSenha().isBlank()
                || !passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new NegocioException(MENSAGEM_CREDENCIAIS_INVALIDAS);
        }
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new NegocioException(UsuarioSecurityMessages.USUARIO_INATIVO);
        }
        String token = jwtService.generateToken(usuario);
        return new LoginResult(buildModel(usuario), token, jwtService.expirationSeconds());
    }

    public AuthenticationModel authenticateFromJwt(JwtAuthenticationToken jwtAuth) {
        Usuario usuario = buscarPorJwt(jwtAuth.getToken());
        return buildModel(usuario);
    }

    public ResponseEntity<AuthenticationModel> checkToken(String token) {
        Jwt decodedJwt = jwtService.validateAndDecode(token);
        Usuario usuario = buscarPorJwt(decodedJwt);
        return ResponseEntity.ok(buildModel(usuario));
    }

    private Usuario buscarPorJwt(Jwt jwt) {
        UUID codigo;
        try {
            codigo = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException ex) {
            throw new TokenJwtInvalidoException(MENSAGEM_USUARIO_NAO_ENCONTRADO);
        }
        Usuario usuario = usuarioRepository.findByCodigoWithGrupos(codigo)
                .orElseThrow(() -> new TokenJwtInvalidoException(MENSAGEM_USUARIO_NAO_ENCONTRADO));
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new TokenJwtInvalidoException(UsuarioSecurityMessages.USUARIO_INATIVO);
        }
        return usuario;
    }

    private AuthenticationModel buildModel(Usuario usuario) {
        List<String> permissoes = new ArrayList<>(usuarioPermissaoResolver.resolverChaves(usuario));

        AuthenticationModel response = new AuthenticationModel();
        response.setUsername(usuario.getUsername());
        response.setNome(usuario.getNome());
        response.setId(usuario.getId());
        response.setCodigo(usuario.getCodigo());
        response.setRoles(usuario.getGruposListString());
        response.setGruposCodigos(gruposCodigosLimpos(usuario));
        response.setGrupoNome(primeiroGrupoNome(usuario));
        response.setCargo(usuario.getCargo());
        response.setPermissoes(permissoes);
        response.setAuthorities(permissoes);
        return response;
    }

    private static List<String> gruposCodigosLimpos(Usuario usuario) {
        if (usuario.getGrupos() == null) {
            return List.of();
        }
        return usuario.getGrupos().stream()
                .filter(g -> g != null && g.getCodigo() != null)
                .map(g -> g.getCodigo().toString())
                .collect(Collectors.toList());
    }

    private static String primeiroGrupoNome(Usuario usuario) {
        if (usuario.getGrupos() == null) {
            return null;
        }
        return usuario.getGrupos().stream()
                .filter(g -> g != null && Boolean.TRUE.equals(g.getAtivo()))
                .map(g -> g.getNome())
                .filter(n -> n != null && !n.isBlank())
                .findFirst()
                .orElse(null);
    }

    public record LoginResult(AuthenticationModel model, String accessToken, long expiresIn) {
    }
}
