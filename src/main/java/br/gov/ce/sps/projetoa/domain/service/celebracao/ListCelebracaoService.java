package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.domain.filter.CelebracaoFilter;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.spec.CelebracaoSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListCelebracaoService {

    private final CelebracaoRepository celebracaoRepository;

    public Page<Celebracao> listar(CelebracaoFilter filtro, Pageable pageable) {
        return celebracaoRepository.findAll(CelebracaoSpec.usandoFiltro(filtro), pageable);
    }
}
