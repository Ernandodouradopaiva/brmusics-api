package br.gov.ce.sps.projetoa.domain.service.local;

import br.gov.ce.sps.projetoa.domain.filter.LocalFilter;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.repository.LocalRepository;
import br.gov.ce.sps.projetoa.domain.spec.LocalSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListLocalService {

    private final LocalRepository localRepository;

    public Page<Local> listar(LocalFilter filtro, Pageable pageable) {
        return localRepository.findAll(LocalSpec.usandoFiltro(filtro), pageable);
    }
}
