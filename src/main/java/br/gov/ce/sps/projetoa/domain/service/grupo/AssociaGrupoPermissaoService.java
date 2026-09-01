package br.gov.ce.sps.projetoa.domain.service.grupo;

import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.model.Permissao;
import br.gov.ce.sps.projetoa.domain.repository.GrupoRepository;
import br.gov.ce.sps.projetoa.domain.service.permissao.GetPermissaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssociaGrupoPermissaoService {

    private final GetGrupoService getGrupoService;
    private final GetPermissaoService getPermissaoService;

    private final GrupoRepository grupoRepository;

    public ResponseEntity<Void> associarGrupoPermissao(UUID grupoCodigo, UUID permissaoCodigo) {
        Grupo grupo = getGrupoService.findByCode(grupoCodigo);
        Permissao permissao = getPermissaoService.findByCode(permissaoCodigo);

        grupo.getPermissoes().add(permissao);
        grupoRepository.save(grupo);

        return ResponseEntity.noContent().build();
    }
}