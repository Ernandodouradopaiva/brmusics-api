package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.core.security.SecurityUtil;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MusicoAutenticadoService {

    static final String MSG_SEM_VINCULO = "Não há músico vinculado ao seu usuário.";

    private final SecurityUtil securityUtil;
    private final MusicoRepository musicoRepository;

    @Transactional(readOnly = true)
    public Musico exigirMusicoVinculado() {
        Usuario usuario = securityUtil.getAuthenticatedUser()
                .orElseThrow(() -> new AccessDeniedException("Usuário não autenticado."));
        return musicoRepository.findByUsuario_Id(usuario.getId())
                .filter(m -> Boolean.TRUE.equals(m.getAtivo()))
                .orElseThrow(() -> new NegocioException(MSG_SEM_VINCULO));
    }
}
