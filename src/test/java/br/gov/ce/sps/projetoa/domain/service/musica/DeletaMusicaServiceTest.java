package br.gov.ce.sps.projetoa.domain.service.musica;

import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.repository.MusicaAnexoRepository;
import br.gov.ce.sps.projetoa.domain.repository.MusicaRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.service.anexo.AnexoStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletaMusicaServiceTest {

    @Mock
    private MusicaRepository musicaRepository;
    @Mock
    private MusicaAnexoRepository musicaAnexoRepository;
    @Mock
    private RepertorioItemRepository repertorioItemRepository;
    @Mock
    private GetMusicaService getMusicaService;
    @Mock
    private AnexoStorageService anexoStorageService;

    private DeletaMusicaService service;

    @BeforeEach
    void setUp() {
        service = new DeletaMusicaService(
                musicaRepository,
                musicaAnexoRepository,
                repertorioItemRepository,
                getMusicaService,
                anexoStorageService);
    }

    @Test
    void excluiMusicaELimpaPastaDeAnexos() {
        UUID codigo = UUID.randomUUID();
        Musica musica = new Musica();
        musica.setId(3L);
        musica.setCodigo(codigo);
        when(getMusicaService.findByCode(codigo)).thenReturn(musica);

        service.deletar(codigo);

        verify(anexoStorageService).limparPasta(MusicaStoragePaths.pasta(codigo));
        verify(musicaAnexoRepository).deleteByMusica_Id(3L);
        verify(repertorioItemRepository).deleteByMusica_Id(3L);
        verify(musicaRepository).delete(musica);
        verify(musicaRepository, never()).save(musica);
    }

    @Test
    void excluiMesmoQuandoHaItemDeRepertorio() {
        UUID codigo = UUID.randomUUID();
        Musica musica = new Musica();
        musica.setId(8L);
        musica.setCodigo(codigo);
        musica.setAtivo(true);
        when(getMusicaService.findByCode(codigo)).thenReturn(musica);

        service.deletar(codigo);

        verify(musicaAnexoRepository).deleteByMusica_Id(8L);
        verify(repertorioItemRepository).deleteByMusica_Id(8L);
        verify(musicaRepository).delete(musica);
        verify(musicaRepository, never()).save(musica);
    }
}
