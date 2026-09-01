package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.api.input.CelebracaoInput;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizaCelebracaoService {

    private final CelebracaoRepository celebracaoRepository;
    private final CadastroCelebracaoService cadastroCelebracaoService;

    @Transactional
    public Celebracao atualiza(Celebracao celebracao, CelebracaoInput input) {
        if (celebracao.getCodigo() == null || celebracao.getId() == null) {
            throw new RuntimeException("Celebração não encontrada");
        }
        cadastroCelebracaoService.aplicarDados(celebracao, input, false);
        return celebracaoRepository.save(celebracao);
    }
}
