package br.gov.ce.sps.projetoa.domain.service.usuario;

import br.gov.ce.sps.projetoa.api.dto.SenhaTemporariaModel;
import br.gov.ce.sps.projetoa.api.input.RecuperarSenhaInput;
import br.gov.ce.sps.projetoa.api.input.SenhaInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;
import br.gov.ce.sps.projetoa.infrastructure.util.CpfUtils;
import br.gov.ce.sps.projetoa.infrastructure.util.GerarSenhaRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SenhaUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GetUsuarioService getUsuarioService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SenhaTemporariaModel recuperarSenha(RecuperarSenhaInput input) {
        String cpf = CpfUtils.normalizar(input.getCpf());
        String email = input.getEmail() == null ? "" : input.getEmail().trim();
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                .orElseThrow(() -> new NegocioException("CPF ou e-mail não conferem."));
        String senhaTemporaria = GerarSenhaRandom.gerarSenhaAleatoria();
        usuario.setSenha(passwordEncoder.encode(senhaTemporaria));
        usuarioRepository.save(usuario);
        return new SenhaTemporariaModel(senhaTemporaria);
    }

    @Transactional
    public void alterarSenha(UUID codigo, SenhaInput senhaInput) {
        validarProprioUsuario(codigo);
        Usuario usuario = getUsuarioService.findByCode(codigo);
        if (usuario.getSenha() == null || !passwordEncoder.matches(senhaInput.getSenhaAtual(), usuario.getSenha())) {
            throw new NegocioException("Senha atual inválida.");
        }
        if (senhaInput.getNovaSenha() == null || senhaInput.getNovaSenha().length() < 8) {
            throw new NegocioException("A nova senha deve ter no mínimo 8 caracteres.");
        }
        usuario.setSenha(passwordEncoder.encode(senhaInput.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    private void validarProprioUsuario(UUID codigo) {
        UUID codigoAutenticado = codigoAutenticado();
        if (codigoAutenticado == null || !codigoAutenticado.equals(codigo)) {
            throw new NegocioException("Sem permissão para alterar senha deste usuário");
        }
    }

    private static UUID codigoAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            try {
                return UUID.fromString(jwtAuth.getToken().getSubject());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            try {
                return UUID.fromString(jwt.getSubject());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }
}
