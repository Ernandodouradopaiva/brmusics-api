package br.gov.ce.sps.projetoa.domain.service.repertorio;

import br.gov.ce.sps.projetoa.api.assembler.RepertorioAssembler;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import br.gov.ce.sps.projetoa.domain.service.escala.MinhaEscalaService;
import br.gov.ce.sps.projetoa.domain.service.musico.MusicoAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeuRepertorioServiceTest {

    @Mock
    private MusicoAutenticadoService musicoAutenticadoService;
    @Mock
    private MinhaEscalaService minhaEscalaService;
    @Mock
    private RepertorioRepository repertorioRepository;
    @Mock
    private RepertorioItemRepository repertorioItemRepository;

    private MeuRepertorioService service;
    private Musico musico;

    @BeforeEach
    void setUp() {
        service = new MeuRepertorioService(
                musicoAutenticadoService,
                minhaEscalaService,
                repertorioRepository,
                repertorioItemRepository,
                new RepertorioAssembler());
        musico = new Musico();
        musico.setId(1L);
        musico.setNome("João");
        musico.setAtivo(true);
        when(musicoAutenticadoService.exigirMusicoVinculado()).thenReturn(musico);
    }

    @Test
    void recusaRepertorioDeCelebracaoEmQueOMusicoNaoEstaEscalado() {
        UUID codigo = UUID.randomUUID();
        UUID celebracaoCodigo = UUID.randomUUID();
        Celebracao celebracao = new Celebracao();
        celebracao.setId(9L);
        celebracao.setCodigo(celebracaoCodigo);
        Repertorio repertorio = new Repertorio();
        repertorio.setId(4L);
        repertorio.setCodigo(codigo);
        repertorio.setCelebracao(celebracao);
        when(repertorioRepository.findComCelebracaoByCodigo(codigo)).thenReturn(Optional.of(repertorio));
        when(minhaEscalaService.musicoParticipaDaCelebracao(musico, celebracaoCodigo)).thenReturn(false);

        assertThatThrownBy(() -> service.buscar(codigo))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void listarUsaSomenteCelebracoesDoMusicoAutenticado() {
        when(minhaEscalaService.celebracoesPublicadasDoMusico(musico)).thenReturn(List.of());
        assertThat(service.listar()).isEmpty();
    }
}
