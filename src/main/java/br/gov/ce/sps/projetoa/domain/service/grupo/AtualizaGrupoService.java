package br.gov.ce.sps.projetoa.domain.service.grupo;

import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.repository.GrupoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizaGrupoService {

    private final GrupoRepository grupoRepository;

    public Grupo atualiza(Grupo grupo) {
        if (grupo.getCodigo() == null || grupo.getId() == null) {
            throw new RuntimeException("Grupo não encontrado");
        }

        return grupoRepository.save(grupo);
    }
}