package br.gov.ce.sps.projetoa.domain.service.repertorio;

import br.gov.ce.sps.projetoa.api.assembler.RepertorioAssembler;
import br.gov.ce.sps.projetoa.api.dto.RepertorioModel;
import br.gov.ce.sps.projetoa.api.input.RepertorioInput;
import br.gov.ce.sps.projetoa.api.input.RepertorioItemInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.CategoriasLiturgicas;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import br.gov.ce.sps.projetoa.domain.service.celebracao.GetCelebracaoService;
import br.gov.ce.sps.projetoa.domain.service.musica.GetMusicaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroRepertorioServiceTest {

    @Mock
    private RepertorioRepository repertorioRepository;
    @Mock
    private RepertorioItemRepository repertorioItemRepository;
    @Mock
    private GetCelebracaoService getCelebracaoService;
    @Mock
    private GetMusicaService getMusicaService;

    private CadastroRepertorioService service;
    private final List<RepertorioItem> persistidos = new ArrayList<>();
    private final AtomicLong ids = new AtomicLong(10);

    @BeforeEach
    void setUp() {
        persistidos.clear();
        service = new CadastroRepertorioService(
                repertorioRepository,
                repertorioItemRepository,
                getCelebracaoService,
                getMusicaService,
                new RepertorioAssembler());
    }

    @Test
    void tomDoItemNaoAlteraTomPadraoDaMusica() {
        UUID celebracaoCodigo = UUID.randomUUID();
        UUID musicaCodigo = UUID.randomUUID();
        Celebracao celebracao = celebracao(celebracaoCodigo);
        Musica musica = musica(musicaCodigo, "CANTO DE ENTRADA", "G");

        mockCriacao(celebracao);
        when(getMusicaService.findByCode(musicaCodigo)).thenReturn(musica);

        RepertorioInput input = input(celebracaoCodigo, item(musicaCodigo, CategoriasLiturgicas.ENTRADA, "A"));
        RepertorioModel model = service.salvar(input);

        assertThat(musica.getTomPadrao()).isEqualTo("G");
        assertThat(persistidos).hasSize(1);
        assertThat(persistidos.getFirst().getTom()).isEqualTo("A");
        assertThat(model.getItens()).hasSize(1);
        assertThat(model.getItens().getFirst().getTom()).isEqualTo("A");
        assertThat(model.getItens().getFirst().getTomPadraoMusica()).isEqualTo("G");
    }

    @Test
    void permiteVariasMusicasNoMesmoMomento() {
        UUID celebracaoCodigo = UUID.randomUUID();
        UUID primeira = UUID.randomUUID();
        UUID segunda = UUID.randomUUID();
        mockCriacao(celebracao(celebracaoCodigo));
        when(getMusicaService.findByCode(primeira)).thenReturn(musica(primeira, "SANTO I", "C"));
        when(getMusicaService.findByCode(segunda)).thenReturn(musica(segunda, "SANTO II", "D"));

        RepertorioInput input = input(
                celebracaoCodigo,
                item(primeira, CategoriasLiturgicas.SANTO, "C"),
                item(segunda, CategoriasLiturgicas.SANTO, "G"));

        RepertorioModel model = service.salvar(input);

        assertThat(persistidos).hasSize(2);
        assertThat(model.getItens()).extracting("musicaTitulo").containsExactly("SANTO I", "SANTO II");
        assertThat(persistidos).allMatch(item -> CategoriasLiturgicas.SANTO.equals(item.getMomentoLiturgico()));
    }

    @Test
    void rejeitaMusicaInativaEmItemNovo() {
        UUID celebracaoCodigo = UUID.randomUUID();
        UUID musicaCodigo = UUID.randomUUID();
        mockCriacao(celebracao(celebracaoCodigo));
        Musica musica = musica(musicaCodigo, "GLORIA", "E");
        musica.setAtivo(false);
        when(getMusicaService.findByCode(musicaCodigo)).thenReturn(musica);

        RepertorioInput input = input(celebracaoCodigo, item(musicaCodigo, CategoriasLiturgicas.GLORIA, "E"));

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Música inativa não pode ser incluída no repertório.");
        verify(repertorioItemRepository, never()).save(any());
    }

    @Test
    void rejeitaCelebracaoQueJaPossuiRepertorio() {
        UUID celebracaoCodigo = UUID.randomUUID();
        when(getCelebracaoService.findByCode(celebracaoCodigo)).thenReturn(celebracao(celebracaoCodigo));
        when(repertorioRepository.existsByCelebracao_Id(20L)).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(input(celebracaoCodigo)))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Esta celebração já possui repertório. Use a edição para alterar as músicas.");
    }

    private void mockCriacao(Celebracao celebracao) {
        when(getCelebracaoService.findByCode(celebracao.getCodigo())).thenReturn(celebracao);
        when(repertorioRepository.existsByCelebracao_Id(celebracao.getId())).thenReturn(false);
        when(repertorioRepository.save(any(Repertorio.class))).thenAnswer(invocation -> {
            Repertorio repertorio = invocation.getArgument(0);
            repertorio.setId(1L);
            repertorio.setCodigo(UUID.randomUUID());
            repertorio.setCelebracao(celebracao);
            return repertorio;
        });
        when(repertorioItemRepository.findByRepertorio_Id(any())).thenReturn(List.of());
        lenient().when(repertorioItemRepository.save(any(RepertorioItem.class))).thenAnswer(invocation -> {
            RepertorioItem item = invocation.getArgument(0);
            if (item.getId() == null) {
                item.setId(ids.incrementAndGet());
            }
            if (item.getCodigo() == null) {
                item.setCodigo(UUID.randomUUID());
            }
            persistidos.add(item);
            return item;
        });
        lenient().when(repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(anyCollection()))
                .thenAnswer(invocation -> persistidos.stream().filter(i -> Boolean.TRUE.equals(i.getAtivo())).toList());
    }

    private static RepertorioInput input(UUID celebracaoCodigo, RepertorioItemInput... itens) {
        RepertorioInput input = new RepertorioInput();
        input.setCelebracaoCodigo(celebracaoCodigo);
        input.setItens(List.of(itens));
        return input;
    }

    private static RepertorioItemInput item(UUID musicaCodigo, String momento, String tom) {
        RepertorioItemInput item = new RepertorioItemInput();
        item.setMusicaCodigo(musicaCodigo);
        item.setMomentoLiturgico(momento);
        item.setTom(tom);
        return item;
    }

    private static Celebracao celebracao(UUID codigo) {
        Celebracao celebracao = new Celebracao();
        celebracao.setId(20L);
        celebracao.setCodigo(codigo);
        celebracao.setTitulo("Missa");
        celebracao.setData(LocalDate.of(2026, 8, 31));
        celebracao.setHoraInicio(LocalTime.of(19, 0));
        return celebracao;
    }

    private static Musica musica(UUID codigo, String titulo, String tomPadrao) {
        Musica musica = new Musica();
        musica.setId(7L);
        musica.setCodigo(codigo);
        musica.setTitulo(titulo);
        musica.setTomPadrao(tomPadrao);
        musica.setAtivo(true);
        return musica;
    }
}
