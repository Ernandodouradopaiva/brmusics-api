package br.gov.ce.sps.projetoa.domain.service.musica;

import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.repository.MusicaAnexoRepository;
import br.gov.ce.sps.projetoa.domain.repository.MusicaRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.service.anexo.AnexoStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletaMusicaService {

    private final MusicaRepository musicaRepository;
    private final MusicaAnexoRepository musicaAnexoRepository;
    private final RepertorioItemRepository repertorioItemRepository;
    private final GetMusicaService getMusicaService;
    private final AnexoStorageService anexoStorageService;

    @Transactional
    public void deletar(UUID codigo) {
        Musica musica = getMusicaService.findByCode(codigo);
        try {
            anexoStorageService.limparPasta(MusicaStoragePaths.pasta(musica.getCodigo()));
        } catch (Exception e) {
            log.warn("Falha ao limpar pasta de anexos da música {}: {}", musica.getCodigo(), e.getMessage());
        }
        musicaAnexoRepository.deleteByMusica_Id(musica.getId());
        repertorioItemRepository.deleteByMusica_Id(musica.getId());
        musicaRepository.delete(musica);
    }
}
