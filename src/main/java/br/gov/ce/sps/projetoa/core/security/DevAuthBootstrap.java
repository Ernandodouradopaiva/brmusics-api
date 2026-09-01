package br.gov.ce.sps.projetoa.core.security;

import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.model.GrupoSistemaConstants;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.GrupoRepository;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Em dev, garante um administrador local (CPF 00000000000 / senha Admin@123)
 * quando nenhum usuário possui senha cadastrada.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevAuthBootstrap implements ApplicationRunner {

    private static final String CPF_ADMIN = "00000000000";
    private static final String SENHA_ADMIN = "Admin@123";
    private static final String EMAIL_ADMIN = "admin@local.test";

    private final UsuarioRepository usuarioRepository;
    private final GrupoRepository grupoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean algumComSenha = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getSenha() != null && !u.getSenha().isBlank());
        if (algumComSenha) {
            return;
        }

        String hash = passwordEncoder.encode(SENHA_ADMIN);
        Usuario usuario = usuarioRepository.findByCpf(CPF_ADMIN).orElseGet(Usuario::new);
        usuario.setNome("ADMINISTRADOR TESTE");
        usuario.setCpf(CPF_ADMIN);
        usuario.setEmail(EMAIL_ADMIN);
        usuario.setSenha(hash);
        usuario.setAtivo(Boolean.TRUE);
        usuario.setCargo("ADMINISTRADOR");

        grupoRepository.findByCodigo(GrupoSistemaConstants.GRUPO_ANALISTA_CODIGO)
                .ifPresent(grupo -> garantirGrupo(usuario, grupo));

        usuarioRepository.save(usuario);
        log.warn(
                "Auth local (dev): usuário {} criado/atualizado com senha temporária Admin@123. Altere após o primeiro acesso.",
                CPF_ADMIN);
    }

    private static void garantirGrupo(Usuario usuario, Grupo grupo) {
        List<Grupo> grupos = usuario.getGrupos() == null ? new ArrayList<>() : new ArrayList<>(usuario.getGrupos());
        boolean jaTem = grupos.stream().anyMatch(g -> grupo.getCodigo().equals(g.getCodigo()));
        if (!jaTem) {
            grupos.add(grupo);
        }
        usuario.setGrupos(grupos);
    }
}
