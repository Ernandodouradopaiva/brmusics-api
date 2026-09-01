package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.dto.EscalaModel;
import br.gov.ce.sps.projetoa.api.input.CopiarEscalaInput;
import br.gov.ce.sps.projetoa.api.input.EscalaMusicoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.service.celebracao.GetCelebracaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CopiarEscalaService {

    private final GetCelebracaoService getCelebracaoService;
    private final EscalaRepository escalaRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final CadastroEscalaService cadastroEscalaService;

    @Transactional
    public EscalaModel copiar(CopiarEscalaInput input) {
        if (input.getOrigemCelebracaoCodigo().equals(input.getDestinoCelebracaoCodigo())) {
            throw new NegocioException("Selecione uma celebração de origem diferente da de destino.");
        }
        Celebracao origem = getCelebracaoService.findByCode(input.getOrigemCelebracaoCodigo());
        Celebracao destino = getCelebracaoService.findByCode(input.getDestinoCelebracaoCodigo());
        Escala escalaOrigem = escalaRepository.findByCelebracao_Id(origem.getId())
                .orElseThrow(() -> new NegocioException("A celebração de origem não possui escala."));
        List<EscalaMusico> ativas = escalaMusicoRepository
                .findAtivasComMusicoEInstrumentoByEscalaIdIn(List.of(escalaOrigem.getId()));
        List<EscalaMusicoInput> equipe = cadastroEscalaService.toInputFromAtivas(ativas, true);
        if (equipe.isEmpty()) {
            throw new NegocioException("A celebração de origem não possui músicos ativos na escala.");
        }
        Escala escalaDestino = cadastroEscalaService.obterOuCriarRascunho(destino);
        cadastroEscalaService.assertPodeAlterar(escalaDestino);
        List<String> alertas = cadastroEscalaService.sincronizarParticipacoes(escalaDestino, equipe, true);
        return cadastroEscalaService.toModel(escalaDestino, alertas);
    }
}
