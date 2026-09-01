package br.gov.ce.sps.projetoa.domain.service.instrumento;

import br.gov.ce.sps.projetoa.api.input.InstrumentoInput;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.repository.InstrumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizaInstrumentoService {

    private final InstrumentoRepository instrumentoRepository;
    private final CadastroInstrumentoService cadastroInstrumentoService;

    @Transactional
    public Instrumento atualiza(Instrumento instrumento, InstrumentoInput input) {
        if (instrumento.getCodigo() == null || instrumento.getId() == null) {
            throw new RuntimeException("Instrumento não encontrado");
        }
        cadastroInstrumentoService.aplicarDados(instrumento, input, instrumento.getId());
        return instrumentoRepository.save(instrumento);
    }

    @Transactional
    public Instrumento atualizarAtivo(Instrumento instrumento, boolean ativo) {
        instrumento.setAtivo(ativo);
        return instrumentoRepository.save(instrumento);
    }
}
