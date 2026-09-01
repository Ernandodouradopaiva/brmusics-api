package br.gov.ce.sps.projetoa.domain.service.inicio;

import br.gov.ce.sps.projetoa.api.dto.DashboardCoordenadorModel;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaConfirmacaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import br.gov.ce.sps.projetoa.domain.repository.WhatsAppEnvioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InicioDashboardServiceTest {

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
    private WhatsAppEnvioRepository whatsAppEnvioRepository;

    private InicioDashboardService service;

    @BeforeEach
    void setUp() {
        service = new InicioDashboardService(
                celebracaoRepository,
                escalaRepository,
                escalaMusicoRepository,
                repertorioRepository,
                repertorioItemRepository,
                whatsAppEnvioRepository);
        lenient().when(whatsAppEnvioRepository.countByStatus(WhatsAppEnvioStatus.PENDENTE)).thenReturn(0L);
        lenient().when(whatsAppEnvioRepository.countByStatus(WhatsAppEnvioStatus.ERRO)).thenReturn(0L);
        lenient().when(escalaRepository.findComCelebracaoByCelebracaoIdIn(anyCollection())).thenReturn(List.of());
        lenient().when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(anyCollection()))
                .thenReturn(List.of());
        lenient().when(repertorioRepository.findComCelebracaoByCelebracaoIdIn(anyCollection())).thenReturn(List.of());
        lenient().when(repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(anyCollection()))
                .thenReturn(List.of());
    }

    @Test
    void ignoraCanceladasRealizadasEJaOcorridas() {
        Celebracao futura = celebracao(1L, LocalDate.now().plusDays(2), "Missa Dominical", CelebracaoStatus.PUBLICADA);
        Celebracao cancelada = celebracao(2L, LocalDate.now().plusDays(3), "Cancelada", CelebracaoStatus.CANCELADA);
        Celebracao realizada = celebracao(3L, LocalDate.now().plusDays(1), "Já feita", CelebracaoStatus.REALIZADA);
        Celebracao passada = celebracao(4L, LocalDate.now().minusDays(1), "Ontem", CelebracaoStatus.PUBLICADA);

        when(celebracaoRepository.findByDataBetweenOrderByDataAscHoraInicioAsc(any(), any()))
                .thenReturn(List.of(futura, cancelada, realizada, passada));

        DashboardCoordenadorModel dash = service.montar();

        assertThat(dash.getProximaCelebracao()).isNotNull();
        assertThat(dash.getProximaCelebracao().getTitulo()).isEqualTo("Missa Dominical");
        assertThat(dash.getProximasCelebracoes()).isEmpty();
        assertThat(dash.getEscalasRascunho()).isEqualTo(1);
        assertThat(dash.getRepertoriosIncompletos()).isEqualTo(1);
    }

    @Test
    void contaRascunhosRepertorioIncompletoConfirmacoesEWhatsApp() {
        Celebracao proxima = celebracao(10L, LocalDate.now().plusDays(1), "Missa Dominical", CelebracaoStatus.PUBLICADA);
        Celebracao rascunho = celebracao(11L, LocalDate.now().plusDays(2), "Missa das 7", CelebracaoStatus.PUBLICADA);
        Celebracao semRepertorio = celebracao(12L, LocalDate.now().plusDays(3), "Missa das 19", CelebracaoStatus.PUBLICADA);

        Escala publicada = escala(10L, proxima, EscalaStatus.PUBLICADA);
        Escala escalaRascunho = escala(11L, rascunho, EscalaStatus.RASCUNHO);

        EscalaMusico pendente1 = participacao(1L, publicada, EscalaConfirmacaoStatus.PENDENTE);
        EscalaMusico pendente2 = participacao(2L, publicada, EscalaConfirmacaoStatus.PENDENTE);
        EscalaMusico confirmado = participacao(3L, publicada, EscalaConfirmacaoStatus.CONFIRMADO);
        EscalaMusico pendenteRascunho = participacao(4L, escalaRascunho, EscalaConfirmacaoStatus.PENDENTE);

        Repertorio repertorioProxima = repertorio(20L, proxima);
        RepertorioItem item = new RepertorioItem();
        item.setId(1L);
        item.setRepertorio(repertorioProxima);

        when(celebracaoRepository.findByDataBetweenOrderByDataAscHoraInicioAsc(any(), any()))
                .thenReturn(List.of(proxima, rascunho, semRepertorio));
        when(escalaRepository.findComCelebracaoByCelebracaoIdIn(anyCollection()))
                .thenReturn(List.of(publicada, escalaRascunho));
        when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(anyCollection()))
                .thenReturn(List.of(pendente1, pendente2, confirmado, pendenteRascunho));
        when(repertorioRepository.findComCelebracaoByCelebracaoIdIn(anyCollection()))
                .thenReturn(List.of(repertorioProxima));
        when(repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(anyCollection()))
                .thenReturn(List.of(item));
        when(whatsAppEnvioRepository.countByStatus(WhatsAppEnvioStatus.PENDENTE)).thenReturn(4L);
        when(whatsAppEnvioRepository.countByStatus(WhatsAppEnvioStatus.ERRO)).thenReturn(1L);

        DashboardCoordenadorModel dash = service.montar();

        assertThat(dash.getProximaCelebracao().getTitulo()).isEqualTo("Missa Dominical");
        assertThat(dash.getProximaCelebracao().isRepertorioCompleto()).isTrue();
        assertThat(dash.getProximaCelebracao().isEscalaPublicada()).isTrue();
        assertThat(dash.getMusicosEscaladosProxima()).isEqualTo(3);
        assertThat(dash.getEscalasRascunho()).isEqualTo(2);
        assertThat(dash.getRepertoriosIncompletos()).isEqualTo(2);
        assertThat(dash.getConfirmacoesPendentes()).isEqualTo(2);
        assertThat(dash.getWhatsappPendentes()).isEqualTo(4);
        assertThat(dash.getWhatsappErros()).isEqualTo(1);
        assertThat(dash.getAlertas()).containsExactly(
                "2 celebrações estão sem repertório.",
                "1 envio de WhatsApp apresentou erro.",
                "2 músicos ainda não confirmaram presença.");
        assertThat(dash.getProximasCelebracoes()).hasSize(2);
        assertThat(dash.getProximasCelebracoes().getFirst().getTitulo()).isEqualTo("Missa das 7");
        assertThat(dash.getProximasCelebracoes().getFirst().isEscalaPublicada()).isFalse();
    }

    @Test
    void periodoVazioDevolveTotaisZerados() {
        when(celebracaoRepository.findByDataBetweenOrderByDataAscHoraInicioAsc(any(), any())).thenReturn(List.of());

        DashboardCoordenadorModel dash = service.montar();

        assertThat(dash.getProximaCelebracao()).isNull();
        assertThat(dash.getProximasCelebracoes()).isEmpty();
        assertThat(dash.getMusicosEscaladosProxima()).isZero();
        assertThat(dash.getEscalasRascunho()).isZero();
        assertThat(dash.getRepertoriosIncompletos()).isZero();
        assertThat(dash.getAlertas()).isEmpty();
    }

    @Test
    void alertasUsamPluralDoPrompt() {
        assertThat(InicioDashboardService.montarAlertas(2, 1, 3)).containsExactly(
                "2 celebrações estão sem repertório.",
                "1 envio de WhatsApp apresentou erro.",
                "3 músicos ainda não confirmaram presença.");
        assertThat(InicioDashboardService.montarAlertas(1, 2, 1)).containsExactly(
                "1 celebração está sem repertório.",
                "2 envios de WhatsApp apresentaram erro.",
                "1 músico ainda não confirmou presença.");
        assertThat(InicioDashboardService.montarAlertas(0, 0, 0)).isEmpty();
    }

    private static Celebracao celebracao(Long id, LocalDate data, String titulo, CelebracaoStatus status) {
        Celebracao c = new Celebracao();
        c.setId(id);
        c.setCodigo(UUID.randomUUID());
        c.setTitulo(titulo);
        c.setData(data);
        c.setHoraInicio(LocalTime.of(19, 0));
        c.setStatus(status);
        return c;
    }

    private static Escala escala(Long id, Celebracao celebracao, EscalaStatus status) {
        Escala e = new Escala();
        e.setId(id);
        e.setCodigo(UUID.randomUUID());
        e.setStatus(status);
        e.setCelebracao(celebracao);
        return e;
    }

    private static EscalaMusico participacao(Long id, Escala escala, EscalaConfirmacaoStatus confirmacao) {
        Musico musico = new Musico();
        musico.setId(id);
        musico.setNome("Músico " + id);
        EscalaMusico em = new EscalaMusico();
        em.setId(id);
        em.setEscala(escala);
        em.setMusico(musico);
        em.setStatusConfirmacao(confirmacao);
        em.setAtivo(true);
        return em;
    }

    private static Repertorio repertorio(Long id, Celebracao celebracao) {
        Repertorio r = new Repertorio();
        r.setId(id);
        r.setCodigo(UUID.randomUUID());
        r.setCelebracao(celebracao);
        return r;
    }
}
