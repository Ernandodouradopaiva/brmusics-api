package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MusicoHistoricoEscalaConsulta {

    private final EscalaMusicoRepository escalaMusicoRepository;

    public boolean possuiHistorico(Long musicoId) {
        if (musicoId == null) {
            return false;
        }
        return escalaMusicoRepository.existsByMusico_Id(musicoId);
    }
}
