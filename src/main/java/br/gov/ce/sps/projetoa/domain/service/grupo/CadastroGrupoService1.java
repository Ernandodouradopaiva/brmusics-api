package br.gov.ce.sps.projetoa.domain.service.grupo;

import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.repository.GrupoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CadastroGrupoService1 {

    private final GrupoRepository grupoRepository;

    public Grupo salvar(Grupo grupo) {
        return grupoRepository.save(grupo);
    }
}