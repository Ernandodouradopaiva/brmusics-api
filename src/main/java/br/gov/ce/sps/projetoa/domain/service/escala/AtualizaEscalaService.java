package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.dto.EscalaModel;
import br.gov.ce.sps.projetoa.api.input.EscalaInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtualizaEscalaService {

    private final GetEscalaService getEscalaService;
    private final CadastroEscalaService cadastroEscalaService;

    @Transactional
    public EscalaModel atualizar(UUID codigo, EscalaInput input) {
        Escala escala = getEscalaService.findByCode(codigo);
        cadastroEscalaService.assertPodeAlterar(escala);
        if (input.getCelebracaoCodigo() != null
                && escala.getCelebracao() != null
                && !input.getCelebracaoCodigo().equals(escala.getCelebracao().getCodigo())) {
            throw new NegocioException("Não é permitido transferir a escala para outra celebração.");
        }
        List<String> alertas = cadastroEscalaService.sincronizarParticipacoes(escala, input.getParticipacoes(), false);
        return cadastroEscalaService.toModel(escala, alertas);
    }
}
