package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.assembler.EscalaPublicacaoAssembler;
import br.gov.ce.sps.projetoa.api.dto.EscalaPublicacaoModel;
import br.gov.ce.sps.projetoa.core.security.SecurityUtil;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaPublicacaoCelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaPublicacaoParticipacaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaPublicacaoRepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaPublicacaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicarEscalaMensalServiceTest {

    @Mock
    private CelebracaoRepository celebracaoRepository;
    @Mock
    private EscalaRepository escalaRepository;
    @Mock
    private EscalaMusicoRepository escalaMusicoRepository;
    @Mock
    private RepertorioRepository repertorioRepository;
    @Mock
    private RepertorioItemRepository repertorioItemRepository;
    @Mock
    private EscalaPublicacaoRepository escalaPublicacaoRepository;
    @Mock
    private EscalaPublicacaoCelebracaoRepository escalaPublicacaoCelebracaoRepository;
    @Mock
    private EscalaPublicacaoParticipacaoRepository escalaPublicacaoParticipacaoRepository;
    @Mock
    private EscalaPublicacaoRepertorioItemRepository escalaPublicacaoRepertorioItemRepository;
    @Mock
    private CadastroEscalaService cadastroEscalaService;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private PublicarEscalaMensalService service;

    @BeforeEach
    void setUp() {
        service = new PublicarEscalaMensalService(
                celebracaoRepository,
                escalaRepository,
                escalaMusicoRepository,
                repertorioRepository,
                repertorioItemRepository,
                escalaPublicacaoRepository,
                escalaPublicacaoCelebracaoRepository,
                escalaPublicacaoParticipacaoRepository,
                escalaPublicacaoRepertorioItemRepository,
                cadastroEscalaService,
                securityUtil,
                new EscalaPublicacaoAssembler(),
                applicationEventPublisher);
    }

    @Test
    void rejeitaMesSemCelebracao() {
        when(celebracaoRepository.findByDataBetweenOrderByDataAscHoraInicioAsc(any(), any())).thenReturn(List.of());

        assertThatThrownBy(() -> service.publicar(2026, 9))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Não há celebrações em Setembro/2026.");
        verify(escalaPublicacaoRepository, never()).save(any());
    }

    @Test
    void publicaVersao1ComSnapshotEMarcaEscala() {
        UUID celebracaoCodigo = UUID.randomUUID();
        Celebracao celebracao = celebracao(celebracaoCodigo);
        Escala escala = new Escala();
        escala.setId(1L);
        escala.setCodigo(UUID.randomUUID());
        escala.setCelebracao(celebracao);
        escala.setStatus(EscalaStatus.RASCUNHO);

        Musico musico = new Musico();
        musico.setId(5L);
        musico.setCodigo(UUID.randomUUID());
        musico.setNome("ANA");
        musico.setAtivo(true);
        Instrumento instrumento = new Instrumento();
        instrumento.setId(7L);
        instrumento.setCodigo(UUID.randomUUID());
        instrumento.setNome("VOCAL");
        EscalaMusico participacao = new EscalaMusico();
        participacao.setEscala(escala);
        participacao.setMusico(musico);
        participacao.setInstrumento(instrumento);
        participacao.setAtivo(true);

        Repertorio repertorio = new Repertorio();
        repertorio.setId(3L);
        repertorio.setCelebracao(celebracao);
        Musica musica = new Musica();
        musica.setCodigo(UUID.randomUUID());
        musica.setTitulo("SANTO");
        RepertorioItem item = new RepertorioItem();
        item.setRepertorio(repertorio);
        item.setMusica(musica);
        item.setMomentoLiturgico("SANTO");
        item.setOrdem(0);
        item.setTom("G");

        Usuario usuario = new Usuario();
        usuario.setCodigo(UUID.randomUUID());
        usuario.setNome("Coordenador");

        when(celebracaoRepository.findByDataBetweenOrderByDataAscHoraInicioAsc(any(), any()))
                .thenReturn(List.of(celebracao));
        when(escalaRepository.findComCelebracaoByCelebracaoIdIn(anyCollection())).thenReturn(List.of(escala));
        when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(anyCollection()))
                .thenReturn(List.of(participacao));
        when(repertorioRepository.findComCelebracaoByCelebracaoIdIn(anyCollection())).thenReturn(List.of(repertorio));
        when(repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(anyCollection())).thenReturn(List.of(item));
        when(cadastroEscalaService.alertasConflito(escala)).thenReturn(List.of());
        when(escalaPublicacaoRepository.findFirstByAnoAndMesOrderByVersaoDesc(2026, 9)).thenReturn(Optional.empty());
        when(securityUtil.getAuthenticatedUser()).thenReturn(Optional.of(usuario));
        when(escalaPublicacaoRepository.save(any())).thenAnswer(invocation -> {
            var pub = invocation.getArgument(0, br.gov.ce.sps.projetoa.domain.model.EscalaPublicacao.class);
            pub.setId(10L);
            pub.setCodigo(UUID.randomUUID());
            return pub;
        });
        when(escalaRepository.save(escala)).thenReturn(escala);

        EscalaPublicacaoModel model = service.publicar(2026, 9);

        assertThat(model.getVersao()).isEqualTo(1);
        assertThat(model.getQuantidadeCelebracoes()).isEqualTo(1);
        assertThat(model.getQuantidadeEscalas()).isEqualTo(1);
        assertThat(model.getQuantidadeMusicos()).isEqualTo(1);
        assertThat(model.getPublicadoPorNome()).isEqualTo("Coordenador");
        assertThat(model.getAlteracoes()).isEmpty();
        assertThat(escala.getStatus()).isEqualTo(EscalaStatus.PUBLICADA);
        verify(applicationEventPublisher).publishEvent(any(br.gov.ce.sps.projetoa.domain.event.EscalaPublicadaEvent.class));
    }

    private static Celebracao celebracao(UUID codigo) {
        Celebracao celebracao = new Celebracao();
        celebracao.setId(20L);
        celebracao.setCodigo(codigo);
        celebracao.setTitulo("Missa Dominical");
        celebracao.setData(LocalDate.of(2026, 9, 6));
        celebracao.setHoraInicio(LocalTime.of(19, 0));
        celebracao.setHoraFim(LocalTime.of(20, 30));
        return celebracao;
    }
}
