package br.gov.ce.sps.projetoa.core.security;

import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.model.Permissao;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.PermissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Resolve permissões funcionais ({@code recurso.acao}) do usuário a partir dos grupos ativos.
 * Ignora vínculos legados {@code ROLE_*} — apenas {@link Permissao#getChave()} entra no JWT.
 */
@Component
@RequiredArgsConstructor
public class UsuarioPermissaoResolver {

    private final PermissaoRepository permissaoRepository;

    public Set<String> resolverChaves(Usuario usuario) {
        if (usuario == null) {
            return Set.of();
        }

        Set<String> chaves = buscarChavesNoBanco(usuario);
        if (!chaves.isEmpty()) {
            return chaves;
        }

        return resolverChavesDosGruposCarregados(usuario);
    }

    public Set<String> resolverChaves(Collection<Grupo> grupos) {
        if (grupos == null) {
            return Set.of();
        }
        Usuario u = new Usuario();
        u.setGrupos(grupos.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        return resolverChavesDosGruposCarregados(u);
    }

    private Set<String> buscarChavesNoBanco(Usuario usuario) {
        List<String> chaves;
        if (usuario.getId() != null) {
            chaves = permissaoRepository.findChavesAtivasPorUsuarioId(usuario.getId());
        } else {
            return Set.of();
        }
        if (chaves == null || chaves.isEmpty()) {
            return Set.of();
        }
        return new LinkedHashSet<>(chaves);
    }

    private Set<String> resolverChavesDosGruposCarregados(Usuario usuario) {
        if (usuario.getGrupos() == null) {
            return Set.of();
        }
        Set<String> chaves = new LinkedHashSet<>();
        for (Grupo grupo : usuario.getGrupos()) {
            if (grupo == null || !Boolean.TRUE.equals(grupo.getAtivo()) || grupo.getPermissoes() == null) {
                continue;
            }
            for (Permissao p : grupo.getPermissoes()) {
                if (p == null || !Boolean.TRUE.equals(p.getAtivo())) {
                    continue;
                }
                if (p.getChave() == null || p.getChave().isBlank()) {
                    continue;
                }
                String chave = p.getChave().trim();
                if (chave.startsWith("ROLE_")) {
                    continue;
                }
                chaves.add(chave);
            }
        }
        return chaves;
    }
}
