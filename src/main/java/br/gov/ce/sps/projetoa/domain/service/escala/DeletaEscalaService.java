package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletaEscalaService {

    private final GetEscalaService getEscalaService;
    private final EscalaMusicoRepository escalaMusicoRepository;

    @Transactional
    public void deletar(UUID codigo) {
        Escala escala = getEscalaService.findByCode(codigo);
        Celebracao celebracao = escala.getCelebracao();
        if (celebracao != null && celebracao.getStatus() == CelebracaoStatus.CANCELADA) {
            throw new NegocioException("Não é possível alterar a escala de uma celebração cancelada.");
        }
        for (EscalaMusico participacao : escalaMusicoRepository.findByEscala_Id(escala.getId())) {
            if (Boolean.TRUE.equals(participacao.getAtivo())) {
                participacao.setAtivo(Boolean.FALSE);
                escalaMusicoRepository.save(participacao);
            }
        }
    }
}
