package br.gov.ce.sps.projetoa.domain.service.instrumento;

import br.gov.ce.sps.projetoa.api.input.InstrumentoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.repository.InstrumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CadastroInstrumentoService {

    private final InstrumentoRepository instrumentoRepository;

    @Transactional
    public Instrumento salvar(InstrumentoInput input) {
        Instrumento instrumento = new Instrumento();
        aplicarDados(instrumento, input, null);
        if (instrumento.getAtivo() == null) {
            instrumento.setAtivo(Boolean.TRUE);
        }
        if (input.getOrdem() == null) {
            instrumento.setOrdem(instrumentoRepository.maxOrdem() + 1);
        }
        return instrumentoRepository.save(instrumento);
    }

    void aplicarDados(Instrumento instrumento, InstrumentoInput input, Long instrumentoIdIgnorar) {
        if (!StringUtils.hasText(input.getNome())) {
            throw new NegocioException("Informe o nome do instrumento.");
        }
        String nome = input.getNome().trim();
        garantirNomeUnico(nome, instrumentoIdIgnorar);
        instrumento.setNome(nome);
        instrumento.setDescricao(blankToNull(input.getDescricao()));
        if (input.getAtivo() != null) {
            instrumento.setAtivo(input.getAtivo());
        }
        if (input.getOrdem() != null) {
            instrumento.setOrdem(input.getOrdem());
        }
    }

    private void garantirNomeUnico(String nome, Long instrumentoIdIgnorar) {
        boolean duplicado = instrumentoIdIgnorar == null
                ? instrumentoRepository.existsByNomeIgnoreCase(nome)
                : instrumentoRepository.existsByNomeIgnoreCaseAndIdNot(nome, instrumentoIdIgnorar);
        if (duplicado) {
            throw new NegocioException("Já existe um instrumento cadastrado com este nome.");
        }
    }

    private static String blankToNull(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        return valor.trim();
    }
}
