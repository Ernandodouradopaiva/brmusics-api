package br.gov.ce.sps.projetoa.domain.service.grupo;

import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.repository.GrupoRepository;
import br.gov.ce.sps.projetoa.domain.spec.GrupoSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ListGrupoService {

    private final GrupoRepository grupoRepository;

    public Set<Grupo> listarTodos() {
        List<Grupo> lista = grupoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
        return Set.copyOf(lista);
    }

    public Page<Grupo> listar(Pageable pageable) {
        return grupoRepository.findAll(pageable);
    }

    public Page<Grupo> listar(String busca, Pageable pageable) {
        return grupoRepository.findAll(GrupoSpec.comBusca(busca), pageable);
    }
}
