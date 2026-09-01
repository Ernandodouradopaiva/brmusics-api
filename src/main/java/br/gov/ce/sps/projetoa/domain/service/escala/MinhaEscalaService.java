package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.assembler.EscalaAssembler;
import br.gov.ce.sps.projetoa.api.dto.MinhaEscalaAgendaModel;
import br.gov.ce.sps.projetoa.api.dto.MinhaEscalaEquipeItemModel;
import br.gov.ce.sps.projetoa.api.dto.MinhaEscalaItemModel;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import br.gov.ce.sps.projetoa.domain.service.musico.MusicoAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MinhaEscalaService {

    private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de escala com código %s";

    private final MusicoAutenticadoService musicoAutenticadoService;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final EscalaRepository escalaRepository;
    private final RepertorioRepository repertorioRepository;

    @Transactional(readOnly = true)
    public MinhaEscalaAgendaModel agenda() {
        Musico musico = musicoAutenticadoService.exigirMusicoVinculado();
        List<MinhaEscalaItemModel> itens = montarItens(musico);
        LocalDateTime agora = LocalDateTime.now();
        List<MinhaEscalaItemModel> futuras = itens.stream()
                .filter(item -> !jaOcorreu(item, agora))
                .toList();
        List<MinhaEscalaItemModel> historico = itens.stream()
                .filter(item -> jaOcorreu(item, agora))
                .sorted(Comparator
                        .comparing(MinhaEscalaItemModel::getData, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(MinhaEscalaItemModel::getHoraInicio, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        MinhaEscalaAgendaModel agenda = new MinhaEscalaAgendaModel();
        agenda.setNomeMusico(EscalaAssembler.nomeExibicao(musico));
        if (!futuras.isEmpty()) {
            agenda.setProxima(futuras.getFirst());
            agenda.setProximas(futuras.size() > 1 ? futuras.subList(1, futuras.size()) : List.of());
        } else {
            agenda.setProximas(List.of());
        }
        agenda.setHistorico(historico);
        return agenda;
    }

    @Transactional(readOnly = true)
    public MinhaEscalaItemModel buscar(UUID escalaCodigo) {
        Musico musico = musicoAutenticadoService.exigirMusicoVinculado();
        Escala escala = escalaRepository.findComCelebracaoByCodigo(escalaCodigo)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, escalaCodigo)));
        if (escala.getStatus() != EscalaStatus.PUBLICADA
                || !escalaMusicoRepository.existsByEscala_IdAndMusico_IdAndAtivoTrue(escala.getId(), musico.getId())) {
            throw new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, escalaCodigo));
        }
        return montarItens(musico).stream()
                .filter(item -> escalaCodigo.equals(item.getEscalaCodigo()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, escalaCodigo)));
    }

    public boolean musicoParticipaDaCelebracao(Musico musico, UUID celebracaoCodigo) {
        if (musico == null || celebracaoCodigo == null) {
            return false;
        }
        return escalaRepository.findByCelebracao_Codigo(celebracaoCodigo)
                .filter(e -> e.getStatus() == EscalaStatus.PUBLICADA)
                .filter(e -> escalaMusicoRepository.existsByEscala_IdAndMusico_IdAndAtivoTrue(e.getId(), musico.getId()))
                .isPresent();
    }

    public List<Celebracao> celebracoesPublicadasDoMusico(Musico musico) {
        if (musico == null || musico.getId() == null) {
            return List.of();
        }
        Map<Long, Celebracao> porId = new LinkedHashMap<>();
        for (EscalaMusico em : escalaMusicoRepository.findPublicadasDoMusico(musico.getId())) {
            Celebracao celebracao = em.getEscala() != null ? em.getEscala().getCelebracao() : null;
            if (celebracao != null && celebracao.getId() != null) {
                porId.putIfAbsent(celebracao.getId(), celebracao);
            }
        }
        return List.copyOf(porId.values());
    }

    private List<MinhaEscalaItemModel> montarItens(Musico musico) {
        List<EscalaMusico> minhas = escalaMusicoRepository.findPublicadasDoMusico(musico.getId());
        if (minhas.isEmpty()) {
            return List.of();
        }
        Map<Long, List<EscalaMusico>> minhasPorEscala = minhas.stream()
                .collect(Collectors.groupingBy(em -> em.getEscala().getId(), LinkedHashMap::new, Collectors.toList()));
        List<Long> escalaIds = List.copyOf(minhasPorEscala.keySet());
        Map<Long, List<EscalaMusico>> equipePorEscala = escalaMusicoRepository
                .findAtivasComMusicoEInstrumentoByEscalaIdIn(escalaIds)
                .stream()
                .collect(Collectors.groupingBy(em -> em.getEscala().getId()));
        List<Long> celebracaoIds = minhas.stream()
                .map(em -> em.getEscala().getCelebracao())
                .filter(Objects::nonNull)
                .map(Celebracao::getId)
                .distinct()
                .toList();
        Map<Long, UUID> repertorioPorCelebracao = celebracaoIds.isEmpty()
                ? Map.of()
                : repertorioRepository.findComCelebracaoByCelebracaoIdIn(celebracaoIds).stream()
                .collect(Collectors.toMap(r -> r.getCelebracao().getId(), Repertorio::getCodigo, (a, b) -> a));

        List<MinhaEscalaItemModel> itens = new ArrayList<>();
        for (Map.Entry<Long, List<EscalaMusico>> entry : minhasPorEscala.entrySet()) {
            EscalaMusico amostra = entry.getValue().getFirst();
            Escala escala = amostra.getEscala();
            Celebracao celebracao = escala.getCelebracao();
            if (celebracao == null) {
                continue;
            }
            MinhaEscalaItemModel item = new MinhaEscalaItemModel();
            item.setEscalaCodigo(escala.getCodigo());
            item.setCelebracaoCodigo(celebracao.getCodigo());
            item.setTitulo(celebracao.getTitulo());
            item.setData(celebracao.getData());
            item.setHoraInicio(celebracao.getHoraInicio());
            item.setHoraFim(celebracao.getHoraFim());
            item.setDiaSemana(EscalaAssembler.diaSemana(celebracao));
            item.setLocalNome(celebracao.getLocal() != null ? celebracao.getLocal().getNome() : null);
            item.setMinhaFuncao(funcoes(entry.getValue()));
            item.setRepertorioCodigo(repertorioPorCelebracao.get(celebracao.getId()));
            List<EscalaMusico> equipe = new ArrayList<>(equipePorEscala.getOrDefault(escala.getId(), List.of()));
            CadastroEscalaService.ordenar(equipe);
            item.setEquipe(equipe.stream().map(this::toEquipe).toList());
            itens.add(item);
        }
        itens.sort(Comparator
                .comparing(MinhaEscalaItemModel::getData, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(MinhaEscalaItemModel::getHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())));
        return itens;
    }

    private MinhaEscalaEquipeItemModel toEquipe(EscalaMusico em) {
        MinhaEscalaEquipeItemModel item = new MinhaEscalaEquipeItemModel();
        item.setMusicoNome(EscalaAssembler.nomeExibicao(em.getMusico()));
        item.setInstrumentoNome(em.getInstrumento() != null ? em.getInstrumento().getNome() : null);
        return item;
    }

    private static String funcoes(List<EscalaMusico> participacoes) {
        return participacoes.stream()
                .map(em -> em.getInstrumento() != null ? em.getInstrumento().getNome() : null)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private static boolean jaOcorreu(MinhaEscalaItemModel item, LocalDateTime agora) {
        if (item.getData() == null) {
            return false;
        }
        LocalTime hora = item.getHoraInicio() != null ? item.getHoraInicio() : LocalTime.MIN;
        return LocalDateTime.of(item.getData(), hora).isBefore(agora);
    }
}
