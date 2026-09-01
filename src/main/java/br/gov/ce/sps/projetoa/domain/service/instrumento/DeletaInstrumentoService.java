package br.gov.ce.sps.projetoa.domain.service.instrumento;

import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.InstrumentoRepository;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletaInstrumentoService {

    private final InstrumentoRepository instrumentoRepository;
    private final MusicoRepository musicoRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final GetInstrumentoService getInstrumentoService;

    @Transactional
    public void deletar(UUID codigo) {
        Instrumento instrumento = getInstrumentoService.findByCode(codigo);
        if (musicoRepository.existsByInstrumentos_Id(instrumento.getId())
                || escalaMusicoRepository.existsByInstrumento_Id(instrumento.getId())) {
            instrumento.setAtivo(Boolean.FALSE);
            instrumentoRepository.save(instrumento);
            return;
        }
        instrumentoRepository.delete(instrumento);
    }
}
