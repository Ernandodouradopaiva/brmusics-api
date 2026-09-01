package br.gov.ce.sps.projetoa.domain.service.grupo;

import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.repository.GrupoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletaGrupoService {

    private final GrupoRepository grupoRepository;
    private final GetGrupoService getGrupoService;

    public void deletar(UUID codigo) {
        Grupo grupo = getGrupoService.findByCode(codigo);
        grupoRepository.delete(grupo);
    }
}