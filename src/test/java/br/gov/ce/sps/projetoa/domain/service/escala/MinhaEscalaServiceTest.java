package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.dto.MinhaEscalaAgendaModel;
import br.gov.ce.sps.projetoa.api.dto.MinhaEscalaItemModel;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import br.gov.ce.sps.projetoa.domain.service.musico.MusicoAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinhaEscalaServiceTest {

    @Mock
    private MusicoAutenticadoService musicoAutenticadoService;
    @Mock
    private EscalaMusicoRepository escalaMusicoRepository;
    @Mock
    private EscalaRepository escalaRepository;
    @Mock
    private RepertorioRepository repertorioRepository;

    private MinhaEscalaService service;
    private Musico joao;
    private Musico ana;

    @BeforeEach
    void setUp() {
        service = new MinhaEscalaService(
                musicoAutenticadoService,
                escalaMusicoRepository,
                escalaRepository,
                repertorioRepository);
        joao = musico(1L, "João Silva");
        ana = musico(2L, "Ana");
        when(musicoAutenticadoService.exigirMusicoVinculado()).thenReturn(joao);
        lenient().when(repertorioRepository.findComCelebracaoByCelebracaoIdIn(any())).thenReturn(List.of());
    }

    @Test
    void agendaIncluiSomenteEscalasPublicadasDoMusicoAutenticado() {
        Escala escalaJoao = escala(10L, UUID.randomUUID(), LocalDate.now().plusDays(3), "Missa Dominical");
        EscalaMusico participacaoJoao = participacao(escalaJoao, joao, "Violão", 1);
        EscalaMusico participacaoAna = participacao(escalaJoao, ana, "Vocal", 0);

        when(escalaMusicoRepository.findPublicadasDoMusico(1L)).thenReturn(List.of(participacaoJoao));
        when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(List.of(10L)))
                .thenReturn(List.of(participacaoAna, participacaoJoao));

        MinhaEscalaAgendaModel agenda = service.agenda();

        assertThat(agenda.getNomeMusico()).isEqualTo("João Silva");
        assertThat(agenda.getProxima()).isNotNull();
        assertThat(agenda.getProxima().getMinhaFuncao()).isEqualTo("Violão");
        assertThat(agenda.getProxima().getEquipe()).extracting("musicoNome")
                .containsExactly("Ana", "João Silva");
        assertThat(agenda.getProximas()).isEmpty();
    }

    @Test
    void buscarRecusaEscalaDeOutroMusico() {
        UUID codigo = UUID.randomUUID();
        Escala deOutro = escala(20L, codigo, LocalDate.now().plusDays(1), "Outra missa");
        when(escalaRepository.findComCelebracaoByCodigo(codigo)).thenReturn(Optional.of(deOutro));
        when(escalaMusicoRepository.existsByEscala_IdAndMusico_IdAndAtivoTrue(20L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> service.buscar(codigo))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void buscarRecusaRascunhoMesmoQueOMusicoEstejaNaEquipe() {
        UUID codigo = UUID.randomUUID();
        Escala rascunho = escala(21L, codigo, LocalDate.now().plusDays(1), "Rascunho");
        rascunho.setStatus(EscalaStatus.RASCUNHO);
        when(escalaRepository.findComCelebracaoByCodigo(codigo)).thenReturn(Optional.of(rascunho));

        assertThatThrownBy(() -> service.buscar(codigo))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void historicoSeparaEscalasJaOcorridas() {
        Escala passada = escala(11L, UUID.randomUUID(), LocalDate.now().minusDays(2), "Missa passada");
        Escala futura = escala(12L, UUID.randomUUID(), LocalDate.now().plusDays(5), "Missa futura");
        EscalaMusico p1 = participacao(passada, joao, "Teclado", 0);
        EscalaMusico p2 = participacao(futura, joao, "Violão", 0);
        when(escalaMusicoRepository.findPublicadasDoMusico(1L)).thenReturn(List.of(p1, p2));
        when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(List.of(11L, 12L)))
                .thenReturn(List.of(p1, p2));

        MinhaEscalaAgendaModel agenda = service.agenda();

        assertThat(agenda.getProxima().getTitulo()).isEqualTo("Missa futura");
        assertThat(agenda.getHistorico()).extracting(MinhaEscalaItemModel::getTitulo)
                .containsExactly("Missa passada");
    }

    private static Musico musico(Long id, String nome) {
        Musico m = new Musico();
        m.setId(id);
        m.setNome(nome);
        m.setAtivo(true);
        return m;
    }

    private static Escala escala(Long id, UUID codigo, LocalDate data, String titulo) {
        Celebracao c = new Celebracao();
        c.setId(id);
        c.setCodigo(UUID.randomUUID());
        c.setTitulo(titulo);
        c.setData(data);
        c.setHoraInicio(LocalTime.of(19, 0));
        c.setStatus(CelebracaoStatus.PUBLICADA);
        Escala e = new Escala();
        e.setId(id);
        e.setCodigo(codigo);
        e.setStatus(EscalaStatus.PUBLICADA);
        e.setCelebracao(c);
        return e;
    }

    private static EscalaMusico participacao(Escala escala, Musico musico, String funcao, int ordem) {
        Instrumento instrumento = new Instrumento();
        instrumento.setId(ordem + 100L);
        instrumento.setNome(funcao);
        EscalaMusico em = new EscalaMusico();
        em.setId(escala.getId() * 10 + musico.getId());
        em.setEscala(escala);
        em.setMusico(musico);
        em.setInstrumento(instrumento);
        em.setOrdem(ordem);
        em.setAtivo(true);
        return em;
    }
}
