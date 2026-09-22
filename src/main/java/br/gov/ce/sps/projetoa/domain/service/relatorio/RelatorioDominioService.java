package br.gov.ce.sps.projetoa.domain.service.relatorio;

import br.gov.ce.sps.projetoa.api.assembler.EscalaAssembler;
import br.gov.ce.sps.projetoa.api.assembler.RepertorioAssembler;
import br.gov.ce.sps.projetoa.domain.model.CategoriasLiturgicas;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.service.escala.CadastroEscalaService;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.MusicaRepository;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import br.gov.ce.sps.projetoa.infrastructure.report.RelatorioEscalasCompletoPdfGenerator;
import br.gov.ce.sps.projetoa.infrastructure.report.RelatorioExecutivoPdfContext;
import br.gov.ce.sps.projetoa.infrastructure.report.RelatorioListagemPdfGenerator;
import br.gov.ce.sps.projetoa.infrastructure.report.RelatorioPdfIcons;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioDominioService {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final Locale PT = Locale.forLanguageTag("pt-BR");

    private final MusicaRepository musicaRepository;
    private final MusicoRepository musicoRepository;
    private final EscalaRepository escalaRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final RepertorioRepository repertorioRepository;
    private final RepertorioItemRepository repertorioItemRepository;
    private final CelebracaoRepository celebracaoRepository;
    private final RelatorioListagemPdfGenerator pdfGenerator;
    private final RelatorioEscalasCompletoPdfGenerator escalasCompletoPdfGenerator;

    @Transactional(readOnly = true)
    public byte[] musicasPdf() {
        List<Musica> musicas = musicaRepository.findAll(Sort.by(Sort.Direction.ASC, "titulo"));
        long ativas = musicas.stream().filter(m -> Boolean.TRUE.equals(m.getAtivo())).count();
        List<String[]> linhas = musicas.stream()
                .map(m -> new String[]{
                        m.getTitulo(),
                        nvl(m.getAutor()),
                        nvl(m.getInterpreteReferencia()),
                        categoria(m.getCategoriaLiturgica()),
                        nvl(m.getTomPadrao()),
                        Boolean.TRUE.equals(m.getAtivo()) ? "Ativa" : "Inativa"
                })
                .toList();
        return pdfGenerator.gerar(new RelatorioListagemPdfGenerator.Pedido(
                "Relatório de Músicas",
                "Catálogo litúrgico — BRMusics",
                "Músicas",
                cards(musicas.size(), "TOTAL", ativas, "ATIVAS", musicas.size() - ativas, "INATIVAS"),
                new RelatorioListagemPdfGenerator.Coluna[]{
                        col("Título", 150f), col("Autor", 95f), col("Intérprete", 90f),
                        col("Categoria", 85f), col("Tom", 35f), col("Status", 50f)
                },
                linhas,
                "1. Listagem conforme cadastro de músicas.",
                "2. Identidade visual BRMusics (madeira/creme/dourado)."));
    }

    @Transactional(readOnly = true)
    public byte[] musicosPdf() {
        List<Musico> musicos = musicoRepository.findAllWithInstrumentos().stream()
                .sorted(Comparator.comparing(m -> m.getNome() == null ? "" : m.getNome(), String.CASE_INSENSITIVE_ORDER))
                .toList();
        long ativos = musicos.stream().filter(m -> Boolean.TRUE.equals(m.getAtivo())).count();
        List<String[]> linhas = musicos.stream()
                .map(m -> new String[]{
                        m.getNome(),
                        nvl(m.getNomeArtistico()),
                        nvl(m.getWhatsapp()),
                        instrumentos(m),
                        Boolean.TRUE.equals(m.getAtivo()) ? "Ativo" : "Inativo"
                })
                .toList();
        return pdfGenerator.gerar(new RelatorioListagemPdfGenerator.Pedido(
                "Relatório de Músicos",
                "Cadastro de músicos — BRMusics",
                "Músicos",
                cards(musicos.size(), "TOTAL", ativos, "ATIVOS", musicos.size() - ativos, "INATIVOS"),
                new RelatorioListagemPdfGenerator.Coluna[]{
                        col("Nome", 140f), col("Nome artístico", 100f), col("WhatsApp", 85f),
                        col("Funções/Instrumentos", 140f), col("Status", 50f)
                },
                linhas,
                "1. Funções e instrumentos conforme vínculo do músico.",
                "2. Identidade visual BRMusics (madeira/creme/dourado)."));
    }

    @Transactional(readOnly = true)
    public byte[] musicosPorEscalaPdf(Integer ano, Integer mes) {
        LocalDate[] periodo = periodo(ano, mes);
        String competencia = competenciaLabel(periodo[0], periodo[1], ano, mes);
        List<EscalaMusico> itens = escalaMusicoRepository.findAtivasNoPeriodo(periodo[0], periodo[1]);
        long musicosDistintos = itens.stream().map(em -> em.getMusico().getId()).distinct().count();
        long escalasDistintas = itens.stream().map(em -> em.getEscala().getId()).distinct().count();
        List<String[]> linhas = itens.stream()
                .map(em -> {
                    Celebracao c = em.getEscala().getCelebracao();
                    return new String[]{
                            DATA.format(c.getData()),
                            HORA.format(c.getHoraInicio()),
                            nvl(c.getTitulo()),
                            local(c),
                            em.getMusico().getNome(),
                            em.getInstrumento().getNome(),
                            em.getStatusConfirmacao() != null ? em.getStatusConfirmacao().name() : "—"
                    };
                })
                .toList();
        return pdfGenerator.gerar(new RelatorioListagemPdfGenerator.Pedido(
                "Relatório de Músicos por Escala",
                competencia + " — BRMusics",
                "Músicos × Escala",
                cards((long) itens.size(), "PARTICIPAÇÕES", musicosDistintos, "MÚSICOS", escalasDistintas, "ESCALAS"),
                new RelatorioListagemPdfGenerator.Coluna[]{
                        col("Data", 55f), col("Hora", 40f), col("Celebração", 110f), col("Local", 80f),
                        col("Músico", 100f), col("Função", 75f), col("Confirmação", 55f)
                },
                linhas,
                "1. Somente participações ativas no período.",
                "2. Identidade visual BRMusics (madeira/creme/dourado)."));
    }

    @Transactional(readOnly = true)
    public byte[] escalasPdf(Integer ano, Integer mes) {
        LocalDate[] periodo = periodo(ano, mes);
        String competencia = competenciaLabel(periodo[0], periodo[1], ano, mes);
        List<Escala> escalas = escalaRepository.findComCelebracaoNoPeriodo(periodo[0], periodo[1]);

        List<Long> escalaIds = escalas.stream().map(Escala::getId).toList();
        List<EscalaMusico> participacoes = escalaIds.isEmpty()
                ? List.of()
                : escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(escalaIds);
        Map<Long, List<EscalaMusico>> equipePorEscala = participacoes.stream()
                .collect(Collectors.groupingBy(em -> em.getEscala().getId()));

        List<Long> celebracaoIds = escalas.stream()
                .map(e -> e.getCelebracao() != null ? e.getCelebracao().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, Repertorio> repertorioPorCelebracao = celebracaoIds.isEmpty()
                ? Map.of()
                : repertorioRepository.findComCelebracaoByCelebracaoIdIn(celebracaoIds).stream()
                .collect(Collectors.toMap(r -> r.getCelebracao().getId(), r -> r, (a, b) -> a));
        List<Long> repertorioIds = repertorioPorCelebracao.values().stream().map(Repertorio::getId).toList();
        Map<Long, List<RepertorioItem>> itensPorRepertorio = repertorioIds.isEmpty()
                ? Map.of()
                : repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(repertorioIds).stream()
                .collect(Collectors.groupingBy(i -> i.getRepertorio().getId()));

        long totalMusicos = participacoes.stream()
                .map(em -> em.getMusico() != null ? em.getMusico().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        long totalItensRepertorio = itensPorRepertorio.values().stream().mapToLong(List::size).sum();

        List<RelatorioEscalasCompletoPdfGenerator.BlocoEscala> blocos = new ArrayList<>();
        for (Escala escala : escalas) {
            Celebracao c = escala.getCelebracao();
            if (c == null) {
                continue;
            }
            List<EscalaMusico> equipe = new ArrayList<>(equipePorEscala.getOrDefault(escala.getId(), List.of()));
            CadastroEscalaService.ordenar(equipe);
            List<String> linhasEquipe = equipe.stream()
                    .map(em -> {
                        String nome = EscalaAssembler.nomeExibicao(em.getMusico());
                        String funcao = em.getInstrumento() != null ? em.getInstrumento().getNome() : "—";
                        String conf = em.getStatusConfirmacao() != null ? em.getStatusConfirmacao().name() : "PENDENTE";
                        return nome + " — " + funcao + " (" + conf + ")";
                    })
                    .toList();

            Repertorio repertorio = repertorioPorCelebracao.get(c.getId());
            List<RepertorioItem> itens = repertorio == null
                    ? List.of()
                    : RepertorioAssembler.ordenar(itensPorRepertorio.getOrDefault(repertorio.getId(), List.of()));
            List<String> linhasRepertorio = itens.stream()
                    .map(item -> {
                        String momento = CategoriasLiturgicas.rotulo(item.getMomentoLiturgico());
                        String musica = item.getMusica() != null ? item.getMusica().getTitulo() : "—";
                        String tom = item.getTom() != null && !item.getTom().isBlank()
                                ? " — Tom " + item.getTom()
                                : "";
                        return momento + ": " + musica + tom;
                    })
                    .toList();

            String cabecalho = DATA.format(c.getData())
                    + " "
                    + HORA.format(c.getHoraInicio())
                    + " — "
                    + nvl(c.getTitulo())
                    + " ("
                    + local(c)
                    + ")";
            String status = escala.getStatus() != null ? escala.getStatus().name() : "—";
            blocos.add(new RelatorioEscalasCompletoPdfGenerator.BlocoEscala(
                    cabecalho, status, linhasEquipe, linhasRepertorio));
        }

        return escalasCompletoPdfGenerator.gerar(new RelatorioEscalasCompletoPdfGenerator.Pedido(
                "Relatório de Escalas",
                competencia + " — equipe e repertório por celebração",
                cards(escalas.size(), "ESCALAS", totalMusicos, "MÚSICOS", totalItensRepertorio, "MÚSICAS"),
                blocos,
                "1. Inclui escalas em rascunho e publicadas do período.",
                "2. Equipe: somente participações ativas. Repertório: itens ativos.",
                "3. Identidade visual BRMusics (madeira/creme/dourado)."));
    }

    @Transactional(readOnly = true)
    public byte[] repertoriosPdf(Integer ano, Integer mes) {
        LocalDate[] periodo = periodo(ano, mes);
        String competencia = competenciaLabel(periodo[0], periodo[1], ano, mes);
        List<Repertorio> repertorios = repertorioRepository.findComCelebracaoNoPeriodo(periodo[0], periodo[1]);
        long publicados = repertorios.stream()
                .filter(r -> r.getStatus() != null && r.getStatus().name().equals("PUBLICADA")).count();
        List<Long> ids = repertorios.stream().map(Repertorio::getId).toList();
        List<RepertorioItem> todosItens = ids.isEmpty()
                ? List.of()
                : repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(ids);
        var itensPorRepertorio = todosItens.stream()
                .collect(Collectors.groupingBy(i -> i.getRepertorio().getId()));
        List<String[]> linhas = new ArrayList<>();
        for (Repertorio r : repertorios) {
            Celebracao c = r.getCelebracao();
            List<RepertorioItem> itens = itensPorRepertorio.getOrDefault(r.getId(), List.of()).stream()
                    .sorted(Comparator.comparing(i -> i.getOrdem() == null ? 0 : i.getOrdem()))
                    .toList();
            if (itens.isEmpty()) {
                linhas.add(new String[]{
                        DATA.format(c.getData()), HORA.format(c.getHoraInicio()), nvl(c.getTitulo()),
                        local(c), r.getStatus() != null ? r.getStatus().name() : "—", "—", "—"
                });
                continue;
            }
            for (RepertorioItem item : itens) {
                linhas.add(new String[]{
                        DATA.format(c.getData()),
                        HORA.format(c.getHoraInicio()),
                        nvl(c.getTitulo()),
                        local(c),
                        r.getStatus() != null ? r.getStatus().name() : "—",
                        nvl(item.getMomentoLiturgico()),
                        item.getMusica() != null ? item.getMusica().getTitulo() : "—"
                });
            }
        }
        return pdfGenerator.gerar(new RelatorioListagemPdfGenerator.Pedido(
                "Relatório de Repertórios",
                competencia + " — BRMusics",
                "Repertórios",
                cards(repertorios.size(), "REPERTÓRIOS", publicados, "PUBLICADOS", (long) linhas.size(), "ITENS"),
                new RelatorioListagemPdfGenerator.Coluna[]{
                        col("Data", 50f), col("Hora", 35f), col("Celebração", 110f), col("Local", 75f),
                        col("Status", 55f), col("Momento", 70f), col("Música", 120f)
                },
                linhas,
                "1. Itens ativos do repertório por celebração no período.",
                "2. Identidade visual BRMusics (madeira/creme/dourado)."));
    }

    @Transactional(readOnly = true)
    public byte[] celebracoesPorMesPdf(Integer ano, Integer mes) {
        LocalDate[] periodo = periodo(ano, mes);
        String competencia = competenciaLabel(periodo[0], periodo[1], ano, mes);
        List<Celebracao> celebracoes = celebracaoRepository
                .findByDataBetweenOrderByDataAscHoraInicioAsc(periodo[0], periodo[1]);
        long publicadas = celebracoes.stream()
                .filter(c -> c.getStatus() != null && c.getStatus().name().equals("PUBLICADA")).count();
        List<String[]> linhas = celebracoes.stream()
                .map(c -> new String[]{
                        DATA.format(c.getData()),
                        HORA.format(c.getHoraInicio()),
                        c.getHoraFim() != null ? HORA.format(c.getHoraFim()) : "—",
                        nvl(c.getTitulo()),
                        local(c),
                        c.getStatus() != null ? c.getStatus().name() : "—"
                })
                .toList();
        return pdfGenerator.gerar(new RelatorioListagemPdfGenerator.Pedido(
                "Relatório de Celebrações por Mês",
                competencia + " — BRMusics",
                "Celebrações",
                cards(celebracoes.size(), "TOTAL", publicadas, "PUBLICADAS", celebracoes.size() - publicadas, "OUTRAS"),
                new RelatorioListagemPdfGenerator.Coluna[]{
                        col("Data", 55f), col("Início", 45f), col("Fim", 45f),
                        col("Título", 160f), col("Local", 120f), col("Status", 70f)
                },
                linhas,
                "1. Celebrações do período selecionado.",
                "2. Identidade visual BRMusics (madeira/creme/dourado)."));
    }

    @Transactional(readOnly = true)
    public byte[] musicosFuncoesInstrumentosPdf() {
        List<Musico> musicos = musicoRepository.findAllWithInstrumentos().stream()
                .sorted(Comparator.comparing(m -> m.getNome() == null ? "" : m.getNome(), String.CASE_INSENSITIVE_ORDER))
                .toList();
        List<String[]> linhas = new ArrayList<>();
        for (Musico m : musicos) {
            List<Instrumento> instrumentos = m.getInstrumentos() == null
                    ? List.of()
                    : m.getInstrumentos().stream()
                    .sorted(Comparator
                            .comparing((Instrumento i) -> i.getOrdem() == null ? 0 : i.getOrdem())
                            .thenComparing(i -> i.getNome() == null ? "" : i.getNome()))
                    .toList();
            if (instrumentos.isEmpty()) {
                linhas.add(new String[]{
                        m.getNome(), nvl(m.getNomeArtistico()), "—",
                        Boolean.TRUE.equals(m.getAtivo()) ? "Ativo" : "Inativo"
                });
            } else {
                for (Instrumento instrumento : instrumentos) {
                    linhas.add(new String[]{
                            m.getNome(),
                            nvl(m.getNomeArtistico()),
                            instrumento.getNome(),
                            Boolean.TRUE.equals(m.getAtivo()) ? "Ativo" : "Inativo"
                    });
                }
            }
        }
        long comFuncao = musicos.stream()
                .filter(m -> m.getInstrumentos() != null && !m.getInstrumentos().isEmpty())
                .count();
        return pdfGenerator.gerar(new RelatorioListagemPdfGenerator.Pedido(
                "Relatório de Músicos por Funções e Instrumentos",
                "Matriz músico × função — BRMusics",
                "Funções",
                cards(musicos.size(), "MÚSICOS", comFuncao, "COM FUNÇÃO", (long) linhas.size(), "VÍNCULOS"),
                new RelatorioListagemPdfGenerator.Coluna[]{
                        col("Músico", 170f), col("Nome artístico", 120f),
                        col("Função / Instrumento", 150f), col("Status", 55f)
                },
                linhas,
                "1. Uma linha por vínculo músico–função/instrumento.",
                "2. Identidade visual BRMusics (madeira/creme/dourado)."));
    }

    private static RelatorioExecutivoPdfContext.SummaryCardSpec[][] cards(
            long aVal, String aLabel, long bVal, String bLabel, long cVal, String cLabel) {
        String emissao = RelatorioExecutivoPdfContext.DATA_FMT.format(LocalDate.now());
        return new RelatorioExecutivoPdfContext.SummaryCardSpec[][]{
                {
                        new RelatorioExecutivoPdfContext.SummaryCardSpec(
                                RelatorioPdfIcons.Tipo.TOTAL_USUARIOS, aLabel,
                                RelatorioExecutivoPdfContext.formatNum(aVal), "registros"),
                        new RelatorioExecutivoPdfContext.SummaryCardSpec(
                                RelatorioPdfIcons.Tipo.STATUS, bLabel,
                                RelatorioExecutivoPdfContext.formatNum(bVal), "registros"),
                },
                {
                        new RelatorioExecutivoPdfContext.SummaryCardSpec(
                                RelatorioPdfIcons.Tipo.PESSOA, cLabel,
                                RelatorioExecutivoPdfContext.formatNum(cVal), "registros"),
                        new RelatorioExecutivoPdfContext.SummaryCardSpec(
                                RelatorioPdfIcons.Tipo.PRANCHETA, "EMISSÃO", emissao, "PDF BRMusics"),
                }
        };
    }

    private static RelatorioListagemPdfGenerator.Coluna col(String rotulo, float largura) {
        return new RelatorioListagemPdfGenerator.Coluna(rotulo, largura);
    }

    private static LocalDate[] periodo(Integer ano, Integer mes) {
        int anoRef = ano != null ? ano : LocalDate.now().getYear();
        if (mes == null) {
            return new LocalDate[]{LocalDate.of(anoRef, 1, 1), LocalDate.of(anoRef, 12, 31)};
        }
        YearMonth ym = YearMonth.of(anoRef, mes);
        return new LocalDate[]{ym.atDay(1), ym.atEndOfMonth()};
    }

    private static String competenciaLabel(LocalDate inicio, LocalDate fim, Integer ano, Integer mes) {
        if (mes != null) {
            return inicio.getMonth().getDisplayName(java.time.format.TextStyle.FULL, PT)
                    + "/" + inicio.getYear();
        }
        return "Ano " + (ano != null ? ano : inicio.getYear());
    }

    private static String categoria(String codigo) {
        if (codigo == null || codigo.isBlank()) return "—";
        return CategoriasLiturgicas.rotulo(codigo);
    }

    private static String instrumentos(Musico m) {
        if (m.getInstrumentos() == null || m.getInstrumentos().isEmpty()) return "—";
        return m.getInstrumentos().stream()
                .sorted(Comparator.comparing(i -> i.getNome() == null ? "" : i.getNome()))
                .map(Instrumento::getNome)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    private static String local(Celebracao c) {
        return c.getLocal() != null ? nvl(c.getLocal().getNome()) : "—";
    }

    private static String nvl(String s) {
        return s == null || s.isBlank() ? "—" : s;
    }
}
