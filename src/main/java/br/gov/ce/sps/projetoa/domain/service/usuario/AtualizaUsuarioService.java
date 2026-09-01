package br.gov.ce.sps.projetoa.domain.service.usuario;

import br.gov.ce.sps.projetoa.api.input.UsuarioInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;
import br.gov.ce.sps.projetoa.domain.service.grupo.GetGrupoService;
import br.gov.ce.sps.projetoa.infrastructure.util.CpfUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizaUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GetGrupoService getGrupoService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario atualiza(Usuario usuario, UsuarioInput usuarioInput) {
        if (usuario.getCodigo() == null || usuario.getId() == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        String cpf = CpfUtils.normalizar(usuarioInput.getCpf());
        usuarioRepository.findByCpf(cpf)
                .filter(existente -> !existente.getId().equals(usuario.getId()))
                .ifPresent(existente -> {
                    throw new NegocioException("Já existe um usuário com este CPF.");
                });

        String email = usuarioInput.getEmail() == null ? null : usuarioInput.getEmail().trim().toLowerCase();
        if (email != null) {
            usuarioRepository.findByEmailIgnoreCase(email)
                    .filter(existente -> !existente.getId().equals(usuario.getId()))
                    .ifPresent(existente -> {
                        throw new NegocioException("Já existe um usuário com este e-mail.");
                    });
        }

        usuario.setNome(usuarioInput.getNome());
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        if (usuarioInput.getCargo() != null) {
            usuario.setCargo(usuarioInput.getCargo().isBlank() ? null : usuarioInput.getCargo().trim());
        }
        if (usuarioInput.getAtivo() != null) {
            usuario.setAtivo(usuarioInput.getAtivo());
        }
        if (usuarioInput.getSenha() != null && !usuarioInput.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(usuarioInput.getSenha()));
        }

        if (usuarioInput.getGrupos() != null) {
            if (usuarioInput.getGrupos().size() > 1) {
                throw new NegocioException("O usuário pode ter apenas um perfil (grupo) no Projeto A.");
            }
            usuario.setGrupos(getGrupoService.findAllByUUID(usuarioInput.getGrupos()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizarAtivo(Usuario usuario, boolean ativo) {
        usuario.setAtivo(ativo);
        return usuarioRepository.save(usuario);
    }
}
