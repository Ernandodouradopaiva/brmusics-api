package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.assembler.EscalaAssembler;
import br.gov.ce.sps.projetoa.api.dto.EscalaModel;
import br.gov.ce.sps.projetoa.api.input.EscalaInput;
import br.gov.ce.sps.projetoa.api.input.EscalaMusicoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaConfirmacaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import br.gov.ce.sps.projetoa.domain.service.celebracao.GetCelebracaoService;
import br.gov.ce.sps.projetoa.domain.service.instrumento.GetInstrumentoService;
import br.gov.ce.sps.projetoa.domain.service.musico.GetMusicoService;
import br.gov.ce.sps.projetoa.domain.service.musico.MusicoEscalaRegra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroEscalaServiceTest {

    @Mock
    private EscalaRepository escalaRepository;
    @Mock
    private EscalaMusicoRepository escalaMusicoRepository;
    @Mock
    private MusicoRepository musicoRepository;
    @Mock
    private GetCelebracaoService getCelebracaoService;
    @Mock
    private GetMusicoService getMusicoService;
    @Mock
    private GetInstrumentoService getInstrumentoService;

    private CadastroEscalaService service;

    @BeforeEach
    void setUp() {
        service = new CadastroEscalaService(
                escalaRepository,
                escalaMusicoRepository,
                musicoRepository,
                getCelebracaoService,
                getMusicoService,
                getInstrumentoService,
                new MusicoEscalaRegra(),
                new EscalaAssembler());
    }

    @Test
    void salvaEscalaComMusicoAtivo() {
        UUID celebracaoCodigo = UUID.randomUUID();
        UUID musicoCodigo = UUID.randomUUID();
        UUID instrumentoCodigo = UUID.randomUUID();
        Celebracao celebracao = celebracao(celebracaoCodigo);
        when(getCelebracaoService.findByCode(celebracaoCodigo)).thenReturn(celebracao);
        when(escalaRepository.existsByCelebracao_Id(10L)).thenReturn(false);
        when(escalaRepository.save(any(Escala.class))).thenAnswer(invocation -> {
            Escala e = invocation.getArgument(0);
            e.setId(1L);
            e.setCodigo(UUID.randomUUID());
            e.setCelebracao(celebracao);
            return e;
        });
        Musico musico = new Musico();
        musico.setId(5L);
        musico.setCodigo(musicoCodigo);
        musico.setNome("PEDRO");
        musico.setAtivo(true);
        Instrumento vocal = instrumento(instrumentoCodigo, "VOCAL");
        musico.setInstrumentos(Set.of(vocal));
        when(getMusicoService.findByCode(musicoCodigo)).thenReturn(musico);
        when(getInstrumentoService.findByCode(instrumentoCodigo)).thenReturn(vocal);
        when(escalaMusicoRepository.findByEscala_Id(1L)).thenReturn(List.of());
        when(escalaMusicoRepository.save(any(EscalaMusico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(musicoRepository.findWithInstrumentosByIdIn(anyCollection())).thenReturn(List.of(musico));
        lenient().when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(anyCollection()))
                .thenReturn(List.of());
        lenient().when(escalaMusicoRepository.findAtivasPorMusicosEDatas(anyCollection(), anyCollection()))
                .thenReturn(List.of());

        EscalaModel model = service.salvar(input(celebracaoCodigo, musicoCodigo, instrumentoCodigo));

        assertThat(model.getCelebracaoCodigo()).isEqualTo(celebracaoCodigo);
        verify(escalaMusicoRepository).save(any(EscalaMusico.class));
    }

    @Test
    void rejeitaMusicoInativo() {
        UUID celebracaoCodigo = UUID.randomUUID();
        UUID musicoCodigo = UUID.randomUUID();
        UUID instrumentoCodigo = UUID.randomUUID();

        Celebracao celebracao = celebracao(celebracaoCodigo);
        when(getCelebracaoService.findByCode(celebracaoCodigo)).thenReturn(celebracao);
        when(escalaRepository.existsByCelebracao_Id(10L)).thenReturn(false);
        when(escalaRepository.save(any(Escala.class))).thenAnswer(invocation -> {
            Escala e = invocation.getArgument(0);
            e.setId(1L);
            e.setCodigo(UUID.randomUUID());
            return e;
        });

        Musico musico = new Musico();
        musico.setId(5L);
        musico.setCodigo(musicoCodigo);
        musico.setNome("ANA");
        musico.setAtivo(false);
        when(getMusicoService.findByCode(musicoCodigo)).thenReturn(musico);

        Instrumento instrumento = instrumento(instrumentoCodigo, "VOCAL");
        when(getInstrumentoService.findByCode(instrumentoCodigo)).thenReturn(instrumento);
        when(escalaMusicoRepository.findByEscala_Id(1L)).thenReturn(List.of());

        EscalaInput input = input(celebracaoCodigo, musicoCodigo, instrumentoCodigo);

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Músico inativo não pode entrar em novas escalas.");
        verify(escalaMusicoRepository, never()).save(any());
    }

    @Test
    void rejeitaMesmoMusicoComMesmaFuncao() {
        UUID celebracaoCodigo = UUID.randomUUID();
        UUID musicoCodigo = UUID.randomUUID();
        UUID instrumentoCodigo = UUID.randomUUID();
        when(getCelebracaoService.findByCode(celebracaoCodigo)).thenReturn(celebracao(celebracaoCodigo));
        when(escalaRepository.existsByCelebracao_Id(10L)).thenReturn(false);
        when(escalaRepository.save(any(Escala.class))).thenAnswer(invocation -> {
            Escala e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        EscalaMusicoInput a = participacao(musicoCodigo, instrumentoCodigo);
        EscalaMusicoInput b = participacao(musicoCodigo, instrumentoCodigo);
        EscalaInput input = new EscalaInput();
        input.setCelebracaoCodigo(celebracaoCodigo);
        input.setParticipacoes(List.of(a, b));

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("O mesmo músico não pode ser escalado duas vezes com a mesma função na celebração.");
    }

    @Test
    void alertaQuandoInstrumentoNaoPertenceAoMusico() {
        UUID celebracaoCodigo = UUID.randomUUID();
        UUID musicoCodigo = UUID.randomUUID();
        UUID instrumentoCodigo = UUID.randomUUID();

        Celebracao celebracao = celebracao(celebracaoCodigo);
        when(getCelebracaoService.findByCode(celebracaoCodigo)).thenReturn(celebracao);
        when(escalaRepository.existsByCelebracao_Id(10L)).thenReturn(false);
        when(escalaRepository.save(any(Escala.class))).thenAnswer(invocation -> {
            Escala e = invocation.getArgument(0);
            e.setId(1L);
            e.setCodigo(UUID.randomUUID());
            e.setCelebracao(celebracao);
            return e;
        });
        when(escalaMusicoRepository.findByEscala_Id(1L)).thenReturn(List.of());
        when(escalaMusicoRepository.save(any(EscalaMusico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(anyCollection())).thenReturn(List.of());

        Musico musico = new Musico();
        musico.setId(5L);
        musico.setCodigo(musicoCodigo);
        musico.setNome("JOÃO");
        musico.setAtivo(true);
        musico.setInstrumentos(Set.of());
        when(getMusicoService.findByCode(musicoCodigo)).thenReturn(musico);

        Instrumento teclado = instrumento(instrumentoCodigo, "TECLADO");
        when(getInstrumentoService.findByCode(instrumentoCodigo)).thenReturn(teclado);

        EscalaModel model = service.salvar(input(celebracaoCodigo, musicoCodigo, instrumentoCodigo));

        assertThat(model.getAlertas())
                .anyMatch(msg -> msg.contains("TECLADO") && msg.contains("JOÃO"));
    }

    @Test
    void alertaConflitoDeHorario() {
        UUID celebracaoCodigo = UUID.randomUUID();
        UUID musicoCodigo = UUID.randomUUID();
        UUID instrumentoCodigo = UUID.randomUUID();

        Celebracao celebracao = celebracao(celebracaoCodigo);
        when(getCelebracaoService.findByCode(celebracaoCodigo)).thenReturn(celebracao);
        when(escalaRepository.existsByCelebracao_Id(10L)).thenReturn(false);
        when(escalaRepository.save(any(Escala.class))).thenAnswer(invocation -> {
            Escala e = invocation.getArgument(0);
            e.setId(1L);
            e.setCodigo(UUID.randomUUID());
            e.setCelebracao(celebracao);
            return e;
        });
        when(escalaMusicoRepository.findByEscala_Id(1L)).thenReturn(List.of());
        when(escalaMusicoRepository.save(any(EscalaMusico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(musicoRepository.findWithInstrumentosByIdIn(anyCollection())).thenReturn(List.of());

        Musico musico = new Musico();
        musico.setId(5L);
        musico.setCodigo(musicoCodigo);
        musico.setNome("PEDRO");
        musico.setAtivo(true);
        Instrumento vocal = instrumento(instrumentoCodigo, "VOCAL");
        musico.setInstrumentos(Set.of(vocal));
        when(getMusicoService.findByCode(musicoCodigo)).thenReturn(musico);
        when(getInstrumentoService.findByCode(instrumentoCodigo)).thenReturn(vocal);

        EscalaMusico atual = new EscalaMusico();
        atual.setMusico(musico);
        atual.setInstrumento(vocal);
        Escala escalaSalva = new Escala();
        escalaSalva.setId(1L);
        escalaSalva.setCelebracao(celebracao);
        atual.setEscala(escalaSalva);

        Celebracao outra = new Celebracao();
        outra.setId(99L);
        outra.setTitulo("Missa das 18h");
        outra.setData(LocalDate.of(2026, 9, 6));
        outra.setHoraInicio(LocalTime.of(18, 30));
        outra.setHoraFim(LocalTime.of(20, 0));
        Escala outraEscala = new Escala();
        outraEscala.setId(2L);
        outraEscala.setCelebracao(outra);
        EscalaMusico conflito = new EscalaMusico();
        conflito.setMusico(musico);
        conflito.setEscala(outraEscala);

        when(escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(anyCollection()))
                .thenReturn(List.of(atual));
        when(escalaMusicoRepository.findAtivasPorMusicosEDatas(anyCollection(), anyCollection()))
                .thenReturn(List.of(atual, conflito));

        EscalaModel model = service.salvar(input(celebracaoCodigo, musicoCodigo, instrumentoCodigo));

        assertThat(model.getAlertas()).anyMatch(msg -> msg.contains("Missa das 18h"));
    }

    @Test
    void permiteAlterarEscalaPublicada() {
        Escala escala = new Escala();
        escala.setId(1L);
        escala.setStatus(EscalaStatus.PUBLICADA);
        when(escalaMusicoRepository.findByEscala_Id(1L)).thenReturn(List.of());

        service.sincronizarParticipacoes(escala, List.of(), false);
    }

    @Test
    void rejeitaAlterarCelebracaoCancelada() {
        Celebracao celebracao = new Celebracao();
        celebracao.setStatus(br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus.CANCELADA);
        Escala escala = new Escala();
        escala.setCelebracao(celebracao);

        assertThatThrownBy(() -> service.sincronizarParticipacoes(escala, List.of(), false))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Não é possível alterar a escala de uma celebração cancelada.");
    }

    private static EscalaInput input(UUID celebracaoCodigo, UUID musicoCodigo, UUID instrumentoCodigo) {
        EscalaInput input = new EscalaInput();
        input.setCelebracaoCodigo(celebracaoCodigo);
        input.setParticipacoes(List.of(participacao(musicoCodigo, instrumentoCodigo)));
        return input;
    }

    private static EscalaMusicoInput participacao(UUID musicoCodigo, UUID instrumentoCodigo) {
        EscalaMusicoInput item = new EscalaMusicoInput();
        item.setMusicoCodigo(musicoCodigo);
        item.setInstrumentoCodigo(instrumentoCodigo);
        item.setStatusConfirmacao(EscalaConfirmacaoStatus.PENDENTE);
        return item;
    }

    private static Celebracao celebracao(UUID codigo) {
        Celebracao celebracao = new Celebracao();
        celebracao.setId(10L);
        celebracao.setCodigo(codigo);
        celebracao.setTitulo("Missa Dominical");
        celebracao.setData(LocalDate.of(2026, 9, 6));
        celebracao.setHoraInicio(LocalTime.of(19, 0));
        celebracao.setHoraFim(LocalTime.of(20, 30));
        return celebracao;
    }

    private static Instrumento instrumento(UUID codigo, String nome) {
        Instrumento instrumento = new Instrumento();
        instrumento.setId(7L);
        instrumento.setCodigo(codigo);
        instrumento.setNome(nome);
        instrumento.setAtivo(true);
        return instrumento;
    }
}
