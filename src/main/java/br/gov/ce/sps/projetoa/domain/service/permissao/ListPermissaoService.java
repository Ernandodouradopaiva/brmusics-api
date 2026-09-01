package br.gov.ce.sps.projetoa.domain.service.permissao;

import br.gov.ce.sps.projetoa.domain.model.Permissao;
import br.gov.ce.sps.projetoa.domain.repository.PermissaoRepository;
import br.gov.ce.sps.projetoa.domain.spec.PermissaoSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ListPermissaoService {

    private final PermissaoRepository permissaoRepository;

    public Set<Permissao> listar() {
        return Set.copyOf(permissaoRepository.findAll());
    }

    public Page<Permissao> listar(String busca, Pageable pageable) {
        return permissaoRepository.findAll(PermissaoSpec.comBusca(busca), pageable);
    }
}
