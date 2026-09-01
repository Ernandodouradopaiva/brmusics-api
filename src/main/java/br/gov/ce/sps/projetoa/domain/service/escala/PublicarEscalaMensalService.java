package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.assembler.EscalaAssembler;
import br.gov.ce.sps.projetoa.api.assembler.EscalaPublicacaoAssembler;
import br.gov.ce.sps.projetoa.api.dto.EscalaPublicacaoAlteracaoModel;
import br.gov.ce.sps.projetoa.api.dto.EscalaPublicacaoModel;
import br.gov.ce.sps.projetoa.api.dto.EscalaPublicacaoPreviaModel;
import br.gov.ce.sps.projetoa.core.security.SecurityUtil;
import br.gov.ce.sps.projetoa.domain.event.EscalaPublicadaEvent;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacao;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoAlteracao;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoCelebracao;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoParticipacao;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoRepertorioItem;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicarEscalaMensalService {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CelebracaoRepository celebracaoRepository;
    private final EscalaRepository escalaRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final RepertorioRepository repertorioRepository;
    private final RepertorioItemRepository repertorioItemRepository;
    private final EscalaPublicacaoRepository escalaPublicacaoRepository;
    private final EscalaPublicacaoCelebracaoRepository escalaPublicacaoCelebracaoRepository;
    private final EscalaPublicacaoParticipacaoRepository escalaPublicacaoParticipacaoRepository;
    private final EscalaPublicacaoRepertorioItemRepository escalaPublicacaoRepertorioItemRepository;
    private final CadastroEscalaService cadastroEscalaService;
    private final SecurityUtil securityUtil;
    private final EscalaPublicacaoAssembler escalaPublicacaoAssembler;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public EscalaPublicacaoPreviaModel previa(int ano, int mes) {
        ContextoMes contexto = carregarMes(ano, mes);
        return toPrevia(contexto);
    }

    @Transactional
    public EscalaPublicacaoModel publicar(int ano, int mes) {
        ContextoMes contexto = carregarMes(ano, mes);
        if (!contexto.impedimentos().isEmpty()) {
            throw new NegocioException(contexto.impedimentos().getFirst());
        }
        Usuario responsavel = securityUtil.getAuthenticatedUser()
                .orElseThrow(() -> new NegocioException("Não foi possível identificar o usuário responsável pela publicação."));

        EscalaPublicacao publicacao = new EscalaPublicacao();
        publicacao.setAno(ano);
        publicacao.setMes(mes);
        publicacao.setVersao(contexto.proximaVersao());
        publicacao.setPublicadoEm(OffsetDateTime.now());
        publicacao.setPublicadoPor(responsavel);
        publicacao.setPublicadoPorNome(nomeResponsavel(responsavel));
        publicacao.setQuantidadeCelebracoes(contexto.quantidadeCelebracoes());
        publicacao.setQuantidadeMusicos(contexto.quantidadeMusicos());
        publicacao.setQuantidadeEscalas(contexto.quantidadeEscalas());

        for (EscalaPublicacaoSnapshot snap : contexto.snapshots()) {
            EscalaPublicacaoCelebracao cel = new EscalaPublicacaoCelebracao();
            cel.setPublicacao(publicacao);
            cel.setCelebracaoCodigo(snap.celebracaoCodigo());
            cel.setCelebracaoTitulo(snap.titulo());
            cel.setData(snap.data());
            cel.setHoraInicio(snap.horaInicio());
            cel.setHoraFim(snap.horaFim());
            cel.setLocalNome(snap.localNome());
            int ordemPart = 0;
            for (EscalaPublicacaoSnapshotParticipacao p : snap.participacoes()) {
                EscalaPublicacaoParticipacao part = new EscalaPublicacaoParticipacao();
                part.setCelebracaoSnapshot(cel);
                part.setMusicoCodigo(p.musicoCodigo());
                part.setMusicoNome(p.musicoNome());
                part.setInstrumentoCodigo(p.instrumentoCodigo());
                part.setInstrumentoNome(p.instrumentoNome());
                part.setOrdem(ordemPart++);
                cel.getParticipacoes().add(part);
            }
            int ordem = 0;
            for (EscalaPublicacaoSnapshotRepertorio r : snap.repertorio()) {
                EscalaPublicacaoRepertorioItem item = new EscalaPublicacaoRepertorioItem();
                item.setCelebracaoSnapshot(cel);
                item.setMusicaCodigo(r.musicaCodigo());
                item.setMusicaTitulo(r.musicaTitulo());
                item.setMomentoLiturgico(r.momentoLiturgico());
                item.setOrdem(r.ordem() == null ? ordem : r.ordem());
                item.setTom(r.tom());
                cel.getRepertorioItens().add(item);
                ordem++;
            }
            publicacao.getCelebracoes().add(cel);
        }
        for (EscalaPublicacaoDiffItem diff : contexto.diffs()) {
            EscalaPublicacaoAlteracao alteracao = new EscalaPublicacaoAlteracao();
            alteracao.setPublicacao(publicacao);
            alteracao.setCelebracaoCodigo(diff.celebracaoCodigo());
            alteracao.setCelebracaoTitulo(diff.celebracaoTitulo());
            alteracao.setTipo(diff.tipo());
            alteracao.setDescricao(diff.descricao());
            publicacao.getAlteracoes().add(alteracao);
        }

        escalaPublicacaoRepository.save(publicacao);

        for (Escala escala : contexto.escalasPublicaveis()) {
            escala.setStatus(EscalaStatus.PUBLICADA);
            escalaRepository.save(escala);
        }
        applicationEventPublisher.publishEvent(new EscalaPublicadaEvent(publicacao.getCodigo()));
        return escalaPublicacaoAssembler.toModel(publicacao);
    }

    private ContextoMes carregarMes(int ano, int mes) {
        if (mes < 1 || mes > 12) {
            throw new NegocioException("Informe um mês válido (1 a 12).");
        }
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
        List<Celebracao> todas = celebracaoRepository.findByDataBetweenOrderByDataAscHoraInicioAsc(inicio, fim);
        List<Celebracao> publicaveis = todas.stream()
                .filter(c -> c.getStatus() != CelebracaoStatus.CANCELADA)
                .toList();

        Map<Long, Escala> escalaPorCelebracao = publicaveis.isEmpty()
                ? Map.of()
                : escalaRepository.findComCelebracaoByCelebracaoIdIn(publicaveis.stream().map(Celebracao::getId).toList())
                .stream()
                .collect(Collectors.toMap(e -> e.getCelebracao().getId(), e -> e, (a, b) -> a));

        List<Long> escalaIds = escalaPorCelebracao.values().stream().map(Escala::getId).toList();
        Map<Long, List<EscalaMusico>> ativasPorEscala = escalaIds.isEmpty()
                ? Map.of()
                : escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(escalaIds).stream()
                .collect(Collectors.groupingBy(em -> em.getEscala().getId()));
        ativasPorEscala.values().forEach(CadastroEscalaService::ordenar);

        Map<Long, Repertorio> repertorioPorCelebracao = publicaveis.isEmpty()
                ? Map.of()
                : repertorioRepository.findComCelebracaoByCelebracaoIdIn(publicaveis.stream().map(Celebracao::getId).toList())
                .stream()
                .collect(Collectors.toMap(r -> r.getCelebracao().getId(), r -> r, (a, b) -> a));
        List<Long> repertorioIds = repertorioPorCelebracao.values().stream().map(Repertorio::getId).toList();
        Map<Long, List<RepertorioItem>> itensPorRepertorio = repertorioIds.isEmpty()
                ? Map.of()
                : repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(repertorioIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRepertorio().getId()));

        List<String> impedimentos = new ArrayList<>();
        String competencia = EscalaPublicacaoAssembler.competencia(ano, mes);
        if (publicaveis.isEmpty()) {
            impedimentos.add("Não há celebrações em " + competencia + ".");
        }

        List<EscalaPublicacaoSnapshot> snapshots = new ArrayList<>();
        List<Escala> escalasPublicaveis = new ArrayList<>();
        for (Celebracao celebracao : publicaveis) {
            Escala escala = escalaPorCelebracao.get(celebracao.getId());
            List<EscalaMusico> ativas = escala == null
                    ? List.of()
                    : ativasPorEscala.getOrDefault(escala.getId(), List.of());
            Repertorio repertorio = repertorioPorCelebracao.get(celebracao.getId());
            List<RepertorioItem> repertorioItens = repertorio == null
                    ? List.of()
                    : itensPorRepertorio.getOrDefault(repertorio.getId(), List.of());

            if (escala == null || ativas.isEmpty()) {
                impedimentos.add("A celebração "
                        + celebracao.getTitulo()
                        + " ("
                        + DATA.format(celebracao.getData())
                        + ") não possui escala com músicos.");
            } else {
                for (EscalaMusico em : ativas) {
                    Musico musico = em.getMusico();
                    if (musico == null) {
                        impedimentos.add("A celebração "
                                + celebracao.getTitulo()
                                + " possui participação sem músico.");
                    } else if (!Boolean.TRUE.equals(musico.getAtivo())) {
                        impedimentos.add(EscalaAssembler.nomeExibicao(musico)
                                + " está inativo e não pode constar na escala de "
                                + celebracao.getTitulo()
                                + ".");
                    }
                }
                impedimentos.addAll(cadastroEscalaService.alertasConflito(escala));
                escalasPublicaveis.add(escala);
            }
            if (repertorio == null || repertorioItens.isEmpty()) {
                impedimentos.add("A celebração "
                        + celebracao.getTitulo()
                        + " ("
                        + DATA.format(celebracao.getData())
                        + ") não possui repertório.");
            }
            snapshots.add(toSnapshot(celebracao, ativas, repertorioItens));
        }

        Optional<EscalaPublicacao> anterior = escalaPublicacaoRepository.findFirstByAnoAndMesOrderByVersaoDesc(ano, mes);
        int proximaVersao = anterior.map(p -> p.getVersao() + 1).orElse(1);
        List<EscalaPublicacaoSnapshot> snapshotsAnteriores = anterior
                .map(this::carregarSnapshots)
                .orElse(List.of());
        List<EscalaPublicacaoDiffItem> diffs = snapshotsAnteriores.isEmpty()
                ? List.of()
                : EscalaPublicacaoComparador.comparar(
                        snapshotsAnteriores,
                        snapshots,
                        identificarCanceladas(snapshotsAnteriores, snapshots));

        int quantidadeMusicos = (int) snapshots.stream()
                .flatMap(s -> s.participacoes().stream())
                .map(EscalaPublicacaoSnapshotParticipacao::musicoCodigo)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        int quantidadeEscalas = (int) snapshots.stream().filter(s -> !s.participacoes().isEmpty()).count();

        return new ContextoMes(
                ano,
                mes,
                competencia,
                publicaveis.size(),
                quantidadeMusicos,
                quantidadeEscalas,
                proximaVersao,
                anterior.orElse(null),
                impedimentos,
                snapshots,
                diffs,
                escalasPublicaveis);
    }

    private EscalaPublicacaoPreviaModel toPrevia(ContextoMes contexto) {
        EscalaPublicacaoPreviaModel model = new EscalaPublicacaoPreviaModel();
        model.setAno(contexto.ano());
        model.setMes(contexto.mes());
        model.setCompetencia(contexto.competencia());
        model.setProximaVersao(contexto.proximaVersao());
        model.setQuantidadeCelebracoes(contexto.quantidadeCelebracoes());
        model.setQuantidadeMusicos(contexto.quantidadeMusicos());
        model.setQuantidadeEscalas(contexto.quantidadeEscalas());
        model.setImpedimentos(List.copyOf(contexto.impedimentos()));
        model.setPodePublicar(contexto.impedimentos().isEmpty());
        model.setAlteracoes(contexto.diffs().stream().map(this::toAlteracaoModel).toList());
        if (contexto.anterior() != null) {
            model.setVersaoAtual(contexto.anterior().getVersao());
            model.setPublicadoEm(contexto.anterior().getPublicadoEm());
            model.setPublicadoPorNome(contexto.anterior().getPublicadoPorNome());
        }
        return model;
    }

    private List<EscalaPublicacaoSnapshot> carregarSnapshots(EscalaPublicacao publicacao) {
        List<EscalaPublicacaoCelebracao> celebracoes =
                escalaPublicacaoCelebracaoRepository.findByPublicacaoId(publicacao.getId());
        if (celebracoes.isEmpty()) {
            return List.of();
        }
        List<Long> ids = celebracoes.stream().map(EscalaPublicacaoCelebracao::getId).toList();
        Map<Long, List<EscalaPublicacaoParticipacao>> partes =
                escalaPublicacaoParticipacaoRepository.findByCelebracaoSnapshot_IdIn(ids).stream()
                        .collect(Collectors.groupingBy(p -> p.getCelebracaoSnapshot().getId()));
        Map<Long, List<EscalaPublicacaoRepertorioItem>> reps =
                escalaPublicacaoRepertorioItemRepository.findByCelebracaoSnapshot_IdIn(ids).stream()
                        .collect(Collectors.groupingBy(r -> r.getCelebracaoSnapshot().getId()));

        List<EscalaPublicacaoSnapshot> snapshots = new ArrayList<>();
        for (EscalaPublicacaoCelebracao cel : celebracoes) {
            List<EscalaPublicacaoSnapshotParticipacao> participacoes = partes
                    .getOrDefault(cel.getId(), List.of())
                    .stream()
                    .map(p -> new EscalaPublicacaoSnapshotParticipacao(
                            p.getMusicoCodigo(),
                            p.getMusicoNome(),
                            p.getInstrumentoCodigo(),
                            p.getInstrumentoNome()))
                    .toList();
            List<EscalaPublicacaoSnapshotRepertorio> repertorio = reps
                    .getOrDefault(cel.getId(), List.of())
                    .stream()
                    .map(r -> new EscalaPublicacaoSnapshotRepertorio(
                            r.getMusicaCodigo(),
                            r.getMusicaTitulo(),
                            r.getMomentoLiturgico(),
                            r.getOrdem(),
                            r.getTom()))
                    .toList();
            snapshots.add(new EscalaPublicacaoSnapshot(
                    cel.getCelebracaoCodigo(),
                    cel.getCelebracaoTitulo(),
                    cel.getData(),
                    cel.getHoraInicio(),
                    cel.getHoraFim(),
                    cel.getLocalNome(),
                    participacoes,
                    repertorio));
        }
        return snapshots;
    }

    private static EscalaPublicacaoSnapshot toSnapshot(
            Celebracao celebracao,
            List<EscalaMusico> ativas,
            List<RepertorioItem> repertorioItens) {
        List<EscalaPublicacaoSnapshotParticipacao> participacoes = ativas.stream()
                .filter(em -> em.getMusico() != null && em.getInstrumento() != null)
                .map(em -> new EscalaPublicacaoSnapshotParticipacao(
                        em.getMusico().getCodigo(),
                        EscalaAssembler.nomeExibicao(em.getMusico()),
                        em.getInstrumento().getCodigo(),
                        em.getInstrumento().getNome()))
                .toList();
        List<EscalaPublicacaoSnapshotRepertorio> repertorio = repertorioItens.stream()
                .filter(item -> item.getMusica() != null)
                .map(item -> {
                    Musica musica = item.getMusica();
                    return new EscalaPublicacaoSnapshotRepertorio(
                            musica.getCodigo(),
                            musica.getTitulo(),
                            item.getMomentoLiturgico(),
                            item.getOrdem(),
                            item.getTom());
                })
                .toList();
        return new EscalaPublicacaoSnapshot(
                celebracao.getCodigo(),
                celebracao.getTitulo(),
                celebracao.getData(),
                celebracao.getHoraInicio(),
                celebracao.getHoraFim(),
                celebracao.getLocal() != null ? celebracao.getLocal().getNome() : null,
                participacoes,
                repertorio);
    }

    private Set<UUID> identificarCanceladas(
            List<EscalaPublicacaoSnapshot> anteriores,
            List<EscalaPublicacaoSnapshot> atuais) {
        Set<UUID> atuaisCodigos = atuais.stream()
                .map(EscalaPublicacaoSnapshot::celebracaoCodigo)
                .collect(Collectors.toSet());
        List<UUID> ausentes = anteriores.stream()
                .map(EscalaPublicacaoSnapshot::celebracaoCodigo)
                .filter(codigo -> !atuaisCodigos.contains(codigo))
                .distinct()
                .toList();
        if (ausentes.isEmpty()) {
            return Set.of();
        }
        return celebracaoRepository.findByCodigoIn(ausentes).stream()
                .filter(c -> c.getStatus() == CelebracaoStatus.CANCELADA)
                .map(Celebracao::getCodigo)
                .collect(Collectors.toSet());
    }

    private EscalaPublicacaoAlteracaoModel toAlteracaoModel(EscalaPublicacaoDiffItem item) {
        EscalaPublicacaoAlteracaoModel model = new EscalaPublicacaoAlteracaoModel();
        model.setCelebracaoCodigo(item.celebracaoCodigo());
        model.setCelebracaoTitulo(item.celebracaoTitulo());
        model.setTipo(item.tipo());
        model.setDescricao(item.descricao());
        return model;
    }

    private static String nomeResponsavel(Usuario usuario) {
        if (usuario == null || !StringUtils.hasText(usuario.getNome())) {
            return "Usuário";
        }
        return usuario.getNome().trim();
    }

    private record ContextoMes(
            int ano,
            int mes,
            String competencia,
            int quantidadeCelebracoes,
            int quantidadeMusicos,
            int quantidadeEscalas,
            int proximaVersao,
            EscalaPublicacao anterior,
            List<String> impedimentos,
            List<EscalaPublicacaoSnapshot> snapshots,
            List<EscalaPublicacaoDiffItem> diffs,
            List<Escala> escalasPublicaveis) {
    }
}
