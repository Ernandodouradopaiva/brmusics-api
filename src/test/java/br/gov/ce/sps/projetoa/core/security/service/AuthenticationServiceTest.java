package br.gov.ce.sps.projetoa.core.security.service;

import br.gov.ce.sps.projetoa.core.ratelimit.InMemoryRateLimiter;
import br.gov.ce.sps.projetoa.core.security.JwtService;
import br.gov.ce.sps.projetoa.core.security.UsuarioPermissaoResolver;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioPermissaoResolver usuarioPermissaoResolver;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private AuthenticationService service;

    @BeforeEach
    void setUp() {
        service = new AuthenticationService(
                jwtService,
                usuarioRepository,
                usuarioPermissaoResolver,
                passwordEncoder,
                new InMemoryRateLimiter());
    }

    @Test
    void loginComCredenciaisValidasEmiteToken() {
        Usuario usuario = usuarioAtivo("00000000000", passwordEncoder.encode("Admin@123"));
        when(usuarioRepository.findByCpfWithGrupos("00000000000")).thenReturn(Optional.of(usuario));
        when(usuarioPermissaoResolver.resolverChaves(usuario)).thenReturn(Set.of("usuario.listar"));
        when(jwtService.generateToken(usuario)).thenReturn("token-local");
        when(jwtService.expirationSeconds()).thenReturn(3600L);

        AuthenticationService.LoginResult result = service.login("000.000.000-00", "Admin@123", "127.0.0.1");

        assertThat(result.accessToken()).isEqualTo("token-local");
        assertThat(result.expiresIn()).isEqualTo(3600L);
        assertThat(result.model().getNome()).isEqualTo("ADMIN");
        assertThat(result.model().getPermissoes()).contains("usuario.listar");
        assertThat(result.model().getAccessToken()).isNull();
    }

    @Test
    void loginComSenhaInvalidaFalha() {
        Usuario usuario = usuarioAtivo("00000000000", passwordEncoder.encode("Admin@123"));
        when(usuarioRepository.findByCpfWithGrupos("00000000000")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.login("00000000000", "errada", "127.0.0.1"))
                .isInstanceOf(NegocioException.class)
                .hasMessage("CPF ou senha inválidos.");
    }

    @Test
    void loginComCpfInexistenteFalha() {
        when(usuarioRepository.findByCpfWithGrupos(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login("11111111111", "Admin@123", "127.0.0.1"))
                .isInstanceOf(NegocioException.class)
                .hasMessage("CPF ou senha inválidos.");
    }

    private static Usuario usuarioAtivo(String cpf, String senhaHash) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCodigo(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"));
        usuario.setNome("ADMIN");
        usuario.setCpf(cpf);
        usuario.setSenha(senhaHash);
        usuario.setAtivo(true);
        return usuario;
    }
}
