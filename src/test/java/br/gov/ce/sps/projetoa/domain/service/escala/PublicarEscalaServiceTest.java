package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.dto.EscalaModel;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicarEscalaServiceTest {

    @Mock
    private GetEscalaService getEscalaService;
    @Mock
    private EscalaRepository escalaRepository;
    @Mock
    private EscalaMusicoRepository escalaMusicoRepository;
    @Mock
    private RepertorioRepository repertorioRepository;
    @Mock
    private RepertorioItemRepository repertorioItemRepository;
    @Mock
    private CadastroEscalaService cadastroEscalaService;

    private PublicarEscalaService service;

    @BeforeEach
    void setUp() {
        service = new PublicarEscalaService(
                getEscalaService,
                escalaRepository,
                escalaMusicoRepository,
                repertorioRepository,
                repertorioItemRepository,
                cadastroEscalaService);
    }

    @Test
    void publicaEscalaEmRascunho() {
        UUID codigo = UUID.randomUUID();
        Escala escala = escala(codigo, EscalaStatus.RASCUNHO);
        EscalaMusico participacao = participacaoAtiva();
        Repertorio repertorio = new Repertorio();
        repertorio.setId(9L);
        EscalaModel model = new EscalaModel();
        model.setCodigo(codigo);
        model.setStatus(EscalaStatus.PUBLICADA);

        when(getEscalaService.findByCode(codigo)).thenReturn(escala);
        when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(List.of(1L)))
                .thenReturn(List.of(participacao));
        when(cadastroEscalaService.alertasConflito(escala)).thenReturn(List.of());
        when(repertorioRepository.findByCelebracao_Id(10L)).thenReturn(Optional.of(repertorio));
        when(repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(List.of(9L)))
                .thenReturn(List.of(new RepertorioItem()));
        when(escalaRepository.save(escala)).thenReturn(escala);
        when(cadastroEscalaService.toModel(escala, List.of())).thenReturn(model);

        EscalaModel resultado = service.publicar(codigo);

        assertThat(escala.getStatus()).isEqualTo(EscalaStatus.PUBLICADA);
        assertThat(resultado.getStatus()).isEqualTo(EscalaStatus.PUBLICADA);
        verify(escalaRepository).save(escala);
    }

    @Test
    void rejeitaQuandoJaPublicada() {
        UUID codigo = UUID.randomUUID();
        when(getEscalaService.findByCode(codigo)).thenReturn(escala(codigo, EscalaStatus.PUBLICADA));

        assertThatThrownBy(() -> service.publicar(codigo))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("já está publicada");
    }

    @Test
    void rejeitaSemMusicos() {
        UUID codigo = UUID.randomUUID();
        Escala escala = escala(codigo, EscalaStatus.RASCUNHO);
        when(getEscalaService.findByCode(codigo)).thenReturn(escala);
        when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(anyList()))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.publicar(codigo))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("não possui escala com músicos");
    }

    @Test
    void rejeitaSemRepertorio() {
        UUID codigo = UUID.randomUUID();
        Escala escala = escala(codigo, EscalaStatus.RASCUNHO);
        when(getEscalaService.findByCode(codigo)).thenReturn(escala);
        when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(anyList()))
                .thenReturn(List.of(participacaoAtiva()));
        when(cadastroEscalaService.alertasConflito(any())).thenReturn(List.of());
        when(repertorioRepository.findByCelebracao_Id(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.publicar(codigo))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("não possui repertório");
    }

    private static Escala escala(UUID codigo, EscalaStatus status) {
        Celebracao celebracao = new Celebracao();
        celebracao.setId(10L);
        celebracao.setTitulo("Missa Dominical");
        celebracao.setData(LocalDate.of(2026, 9, 12));
        celebracao.setStatus(CelebracaoStatus.PUBLICADA);

        Escala escala = new Escala();
        escala.setId(1L);
        escala.setCodigo(codigo);
        escala.setStatus(status);
        escala.setCelebracao(celebracao);
        return escala;
    }

    private static EscalaMusico participacaoAtiva() {
        Musico musico = new Musico();
        musico.setId(3L);
        musico.setNome("ERNANDO");
        musico.setAtivo(true);

        EscalaMusico em = new EscalaMusico();
        em.setAtivo(true);
        em.setMusico(musico);
        return em;
    }
}
