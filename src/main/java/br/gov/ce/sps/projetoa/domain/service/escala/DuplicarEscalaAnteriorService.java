package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.dto.EscalaModel;
import br.gov.ce.sps.projetoa.api.input.DuplicarEscalaAnteriorInput;
import br.gov.ce.sps.projetoa.api.input.EscalaMusicoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.service.celebracao.GetCelebracaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DuplicarEscalaAnteriorService {

    private final GetCelebracaoService getCelebracaoService;
    private final EscalaRepository escalaRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final CadastroEscalaService cadastroEscalaService;

    @Transactional
    public EscalaModel duplicar(DuplicarEscalaAnteriorInput input) {
        Celebracao destino = getCelebracaoService.findByCode(input.getCelebracaoDestinoCodigo());
        List<Escala> anteriores = escalaRepository.findAnterioresComEquipe(
                destino.getData(), destino.getHoraInicio(), PageRequest.of(0, 1));
        if (anteriores.isEmpty()) {
            throw new NegocioException("Não há escala anterior com músicos para duplicar.");
        }
        Escala origem = anteriores.getFirst();
        List<EscalaMusico> ativas = escalaMusicoRepository
                .findAtivasComMusicoEInstrumentoByEscalaIdIn(List.of(origem.getId()));
        List<EscalaMusicoInput> equipe = cadastroEscalaService.toInputFromAtivas(ativas, true);
        if (equipe.isEmpty()) {
            throw new NegocioException("Não há escala anterior com músicos ativos para duplicar.");
        }
        Escala escalaDestino = cadastroEscalaService.obterOuCriarRascunho(destino);
        cadastroEscalaService.assertPodeAlterar(escalaDestino);
        List<String> alertas = cadastroEscalaService.sincronizarParticipacoes(escalaDestino, equipe, true);
        return cadastroEscalaService.toModel(escalaDestino, alertas);
    }
}
