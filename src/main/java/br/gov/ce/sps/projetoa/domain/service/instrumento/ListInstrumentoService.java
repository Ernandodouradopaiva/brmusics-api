package br.gov.ce.sps.projetoa.domain.service.instrumento;

import br.gov.ce.sps.projetoa.domain.filter.InstrumentoFilter;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.repository.InstrumentoRepository;
import br.gov.ce.sps.projetoa.domain.spec.InstrumentoSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListInstrumentoService {

    private final InstrumentoRepository instrumentoRepository;

    public Page<Instrumento> listar(InstrumentoFilter filtro, Pageable pageable) {
        return instrumentoRepository.findAll(InstrumentoSpec.usandoFiltro(filtro), pageable);
    }
}
