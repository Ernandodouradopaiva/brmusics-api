package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletaEscalaServiceTest {

    @Mock
    private GetEscalaService getEscalaService;
    @Mock
    private EscalaMusicoRepository escalaMusicoRepository;

    private DeletaEscalaService service;

    @BeforeEach
    void setUp() {
        service = new DeletaEscalaService(getEscalaService, escalaMusicoRepository);
    }

    @Test
    void inativaParticipacoesSemApagarHistorico() {
        UUID codigo = UUID.randomUUID();
        Escala escala = new Escala();
        escala.setId(1L);
        escala.setCodigo(codigo);
        escala.setStatus(EscalaStatus.RASCUNHO);
        EscalaMusico participacao = new EscalaMusico();
        participacao.setAtivo(true);

        when(getEscalaService.findByCode(codigo)).thenReturn(escala);
        when(escalaMusicoRepository.findByEscala_Id(1L)).thenReturn(List.of(participacao));

        service.deletar(codigo);

        assertThat(participacao.getAtivo()).isFalse();
        verify(escalaMusicoRepository).save(participacao);
        verify(escalaMusicoRepository, never()).delete(participacao);
    }

    @Test
    void inativaParticipacoesDeEscalaPublicadaParaNovaVersao() {
        UUID codigo = UUID.randomUUID();
        Escala escala = new Escala();
        escala.setId(1L);
        escala.setCodigo(codigo);
        escala.setStatus(EscalaStatus.PUBLICADA);
        EscalaMusico participacao = new EscalaMusico();
        participacao.setAtivo(true);

        when(getEscalaService.findByCode(codigo)).thenReturn(escala);
        when(escalaMusicoRepository.findByEscala_Id(1L)).thenReturn(List.of(participacao));

        service.deletar(codigo);

        assertThat(participacao.getAtivo()).isFalse();
        verify(escalaMusicoRepository).save(participacao);
    }
}
