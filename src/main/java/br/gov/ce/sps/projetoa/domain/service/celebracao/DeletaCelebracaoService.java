package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletaCelebracaoService {

    private final CelebracaoRepository celebracaoRepository;
    private final EscalaRepository escalaRepository;
    private final RepertorioRepository repertorioRepository;
    private final GetCelebracaoService getCelebracaoService;

    @Transactional
    public void deletar(UUID codigo) {
        Celebracao celebracao = getCelebracaoService.findByCode(codigo);
        if (celebracao.getStatus() == CelebracaoStatus.REALIZADA) {
            throw new NegocioException("Não é possível excluir uma celebração já realizada.");
        }
        if (escalaRepository.existsByCelebracao_Id(celebracao.getId())) {
            throw new NegocioException("Celebração com escala não pode ser excluída para preservar o histórico.");
        }
        if (repertorioRepository.existsByCelebracao_Id(celebracao.getId())) {
            throw new NegocioException("Celebração com repertório não pode ser excluída para preservar o histórico.");
        }
        celebracaoRepository.delete(celebracao);
    }
}
