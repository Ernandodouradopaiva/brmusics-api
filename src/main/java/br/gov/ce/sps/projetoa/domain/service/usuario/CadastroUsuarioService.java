package br.gov.ce.sps.projetoa.domain.service.usuario;

import br.gov.ce.sps.projetoa.api.input.GruposInput;
import br.gov.ce.sps.projetoa.api.input.UsuarioInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.model.GrupoSistemaConstants;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;
import br.gov.ce.sps.projetoa.domain.service.grupo.GetGrupoService;
import br.gov.ce.sps.projetoa.infrastructure.util.CpfUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CadastroUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GetGrupoService getGrupoService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario salvar(UsuarioInput input) {
        if (input.getSenha() == null || input.getSenha().isBlank()) {
            throw new NegocioException("Informe a senha do usuário.");
        }
        String cpf = CpfUtils.normalizar(input.getCpf());
        if (usuarioRepository.findByCpf(cpf).isPresent()) {
            throw new NegocioException("Já existe um usuário com este CPF.");
        }
        String email = input.getEmail() == null ? null : input.getEmail().trim().toLowerCase();
        if (email != null && usuarioRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new NegocioException("Já existe um usuário com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(input.getNome());
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setCargo(textoVazioComoNull(input.getCargo()));
        usuario.setAtivo(input.getAtivo() != null ? input.getAtivo() : Boolean.TRUE);
        usuario.setSenha(passwordEncoder.encode(input.getSenha()));
        if (input.getGrupos() != null && !input.getGrupos().isEmpty()) {
            if (input.getGrupos().size() > 1) {
                throw new NegocioException("O usuário pode ter apenas um perfil (grupo) no BRMusics.");
            }
            usuario.setGrupos(new ArrayList<>(getGrupoService.findAllByUUID(input.getGrupos())));
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atribuirGrupos(Long usuarioId, GruposInput input) {
        Usuario usuario = usuarioRepository.findByIdWithGrupos(usuarioId)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado."));
        List<UUID> gruposIds = input.gruposIds() != null ? input.gruposIds() : List.of();
        if (gruposIds.size() > 1) {
            throw new NegocioException("O usuário pode ter apenas um perfil (grupo) no BRMusics.");
        }
        List<Grupo> grupos = gruposIds.isEmpty() ? List.of() : getGrupoService.findAllByUUID(gruposIds);
        validarUltimoAnalista(usuario, grupos);
        usuario.setGrupos(new ArrayList<>(grupos));
        return usuarioRepository.save(usuario);
    }

    private void validarUltimoAnalista(Usuario usuario, List<Grupo> novosGrupos) {
        boolean eraAnalista = usuario.getGrupos().stream()
                .anyMatch(g -> GrupoSistemaConstants.GRUPO_ANALISTA_CODIGO.equals(g.getCodigo()));
        boolean continuaAnalista = novosGrupos.stream()
                .anyMatch(g -> GrupoSistemaConstants.GRUPO_ANALISTA_CODIGO.equals(g.getCodigo()));
        if (eraAnalista && !continuaAnalista && contarAnalistas() <= 1) {
            throw new NegocioException("Não é possível remover o último Administrador Geral do sistema.");
        }
    }

    private long contarAnalistas() {
        return usuarioRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getAtivo()))
                .filter(u -> u.getGrupos().stream()
                        .anyMatch(g -> GrupoSistemaConstants.GRUPO_ANALISTA_CODIGO.equals(g.getCodigo())))
                .count();
    }

    private static String textoVazioComoNull(String valor) {
        if (valor == null) {
            return null;
        }
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }
}
