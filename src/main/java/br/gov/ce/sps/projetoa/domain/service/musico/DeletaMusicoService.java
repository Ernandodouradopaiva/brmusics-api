package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletaMusicoService {

    private final MusicoRepository musicoRepository;
    private final GetMusicoService getMusicoService;
    private final MusicoHistoricoEscalaConsulta musicoHistoricoEscalaConsulta;

    @Transactional
    public void deletar(UUID codigo) {
        Musico musico = getMusicoService.findByCode(codigo);
        if (musicoHistoricoEscalaConsulta.possuiHistorico(musico.getId())) {
            musico.setAtivo(Boolean.FALSE);
            musicoRepository.save(musico);
            return;
        }
        musicoRepository.delete(musico);
    }
}
