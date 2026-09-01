package br.gov.ce.sps.projetoa.domain.service.instrumento;

import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.InstrumentoRepository;
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
class DeletaInstrumentoServiceTest {

    @Mock
    private InstrumentoRepository instrumentoRepository;
    @Mock
    private MusicoRepository musicoRepository;
    @Mock
    private EscalaMusicoRepository escalaMusicoRepository;
    @Mock
    private GetInstrumentoService getInstrumentoService;

    private DeletaInstrumentoService service;

    @BeforeEach
    void setUp() {
        service = new DeletaInstrumentoService(
                instrumentoRepository, musicoRepository, escalaMusicoRepository, getInstrumentoService);
    }

    @Test
    void excluiFisicamenteQuandoNaoHaVinculoComMusico() {
        UUID codigo = UUID.randomUUID();
        Instrumento instrumento = new Instrumento();
        instrumento.setId(1L);
        instrumento.setCodigo(codigo);
        instrumento.setAtivo(true);

        when(getInstrumentoService.findByCode(codigo)).thenReturn(instrumento);
        when(musicoRepository.existsByInstrumentos_Id(1L)).thenReturn(false);
        when(escalaMusicoRepository.existsByInstrumento_Id(1L)).thenReturn(false);

        service.deletar(codigo);

        verify(instrumentoRepository).delete(instrumento);
        verify(instrumentoRepository, never()).save(instrumento);
    }

    @Test
    void inativaQuandoHaVinculoComMusico() {
        UUID codigo = UUID.randomUUID();
        Instrumento instrumento = new Instrumento();
        instrumento.setId(2L);
        instrumento.setCodigo(codigo);
        instrumento.setAtivo(true);

        when(getInstrumentoService.findByCode(codigo)).thenReturn(instrumento);
        when(musicoRepository.existsByInstrumentos_Id(2L)).thenReturn(true);

        service.deletar(codigo);

        assertThat(instrumento.getAtivo()).isFalse();
        verify(instrumentoRepository).save(instrumento);
        verify(instrumentoRepository, never()).delete(instrumento);
    }
}
