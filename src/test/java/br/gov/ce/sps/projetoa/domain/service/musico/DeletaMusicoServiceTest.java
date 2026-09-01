package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletaMusicoServiceTest {

    @Mock
    private MusicoRepository musicoRepository;
    @Mock
    private GetMusicoService getMusicoService;
    @Mock
    private MusicoHistoricoEscalaConsulta musicoHistoricoEscalaConsulta;

    private DeletaMusicoService service;

    @BeforeEach
    void setUp() {
        service = new DeletaMusicoService(musicoRepository, getMusicoService, musicoHistoricoEscalaConsulta);
    }

    @Test
    void excluiFisicamenteQuandoNaoHaHistoricoDeEscala() {
        UUID codigo = UUID.randomUUID();
        Musico musico = new Musico();
        musico.setId(1L);
        musico.setCodigo(codigo);
        musico.setAtivo(true);

        when(getMusicoService.findByCode(codigo)).thenReturn(musico);
        when(musicoHistoricoEscalaConsulta.possuiHistorico(1L)).thenReturn(false);

        service.deletar(codigo);

        verify(musicoRepository).delete(musico);
        verify(musicoRepository, never()).save(musico);
    }

    @Test
    void inativaQuandoHaHistoricoDeEscala() {
        UUID codigo = UUID.randomUUID();
        Musico musico = new Musico();
        musico.setId(2L);
        musico.setCodigo(codigo);
        musico.setAtivo(true);

        when(getMusicoService.findByCode(codigo)).thenReturn(musico);
        when(musicoHistoricoEscalaConsulta.possuiHistorico(2L)).thenReturn(true);

        service.deletar(codigo);

        assertThat(musico.getAtivo()).isFalse();
        verify(musicoRepository).save(musico);
        verify(musicoRepository, never()).delete(musico);
    }
}
