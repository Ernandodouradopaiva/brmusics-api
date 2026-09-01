package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.assembler.EscalaAssembler;
import br.gov.ce.sps.projetoa.api.dto.EscalaMensalItemModel;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListEscalaMensalService {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CelebracaoRepository celebracaoRepository;
    private final EscalaRepository escalaRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final CadastroEscalaService cadastroEscalaService;
    private final EscalaAssembler escalaAssembler;

    @Transactional(readOnly = true)
    public List<EscalaMensalItemModel> listar(int ano, int mes) {
        if (mes < 1 || mes > 12) {
            throw new NegocioException("Informe um mês válido (1 a 12).");
        }
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
        List<Celebracao> celebracoes = celebracaoRepository.findByDataBetweenOrderByDataAscHoraInicioAsc(inicio, fim);
        if (celebracoes.isEmpty()) {
            return List.of();
        }

        List<Long> celebracaoIds = celebracoes.stream().map(Celebracao::getId).toList();
        Map<Long, Escala> escalaPorCelebracao = escalaRepository.findComCelebracaoByCelebracaoIdIn(celebracaoIds)
                .stream()
                .collect(Collectors.toMap(e -> e.getCelebracao().getId(), e -> e, (a, b) -> a));

        List<Long> escalaIds = escalaPorCelebracao.values().stream().map(Escala::getId).toList();
        Map<Long, List<EscalaMusico>> ativasPorEscala = escalaIds.isEmpty()
                ? Map.of()
                : escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(escalaIds).stream()
                .collect(Collectors.groupingBy(em -> em.getEscala().getId()));

        List<EscalaMusico> todasAtivas = ativasPorEscala.values().stream().flatMap(List::stream).toList();
        cadastroEscalaService.hidratarInstrumentosDoMusico(todasAtivas);
        ativasPorEscala.values().forEach(CadastroEscalaService::ordenar);

        List<EscalaMusico> conflitos = conflitosDoMes(todasAtivas, celebracoes);

        List<EscalaMensalItemModel> itens = new ArrayList<>();
        for (Celebracao celebracao : celebracoes) {
            Escala escala = escalaPorCelebracao.get(celebracao.getId());
            List<EscalaMusico> ativas = escala == null
                    ? List.of()
                    : ativasPorEscala.getOrDefault(escala.getId(), List.of());
            List<String> alertas = montarAlertas(celebracao, escala, ativas, conflitos);
            itens.add(escalaAssembler.toMensalItem(celebracao, escala, ativas, alertas));
        }
        return itens;
    }

    private List<EscalaMusico> conflitosDoMes(List<EscalaMusico> ativas, List<Celebracao> celebracoes) {
        List<Long> musicoIds = ativas.stream()
                .map(em -> em.getMusico() != null ? em.getMusico().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (musicoIds.isEmpty()) {
            return List.of();
        }
        List<LocalDate> datas = celebracoes.stream().map(Celebracao::getData).distinct().toList();
        return escalaMusicoRepository.findAtivasPorMusicosEDatas(musicoIds, datas);
    }

    private List<String> montarAlertas(
            Celebracao celebracao,
            Escala escala,
            List<EscalaMusico> ativas,
            List<EscalaMusico> conflitos) {
        if (escala == null || ativas.isEmpty()) {
            return List.of();
        }
        List<String> alertas = new ArrayList<>();
        for (EscalaMusico em : ativas) {
            if (!EscalaAssembler.possuiInstrumento(em.getMusico(), em.getInstrumento())) {
                alertas.add("O instrumento "
                        + em.getInstrumento().getNome()
                        + " não está cadastrado para "
                        + EscalaAssembler.nomeExibicao(em.getMusico())
                        + ".");
            }
        }
        Set<String> vistos = new HashSet<>();
        for (EscalaMusico atual : ativas) {
            for (EscalaMusico outra : conflitos) {
                if (outra.getEscala().getId().equals(escala.getId())) {
                    continue;
                }
                if (atual.getMusico() == null || outra.getMusico() == null) {
                    continue;
                }
                if (!atual.getMusico().getId().equals(outra.getMusico().getId())) {
                    continue;
                }
                Celebracao outraCelebracao = outra.getEscala().getCelebracao();
                if (!EscalaHorario.horariosConflitam(celebracao, outraCelebracao)) {
                    continue;
                }
                String chave = atual.getMusico().getId() + ":" + outraCelebracao.getId();
                if (!vistos.add(chave)) {
                    continue;
                }
                alertas.add(EscalaAssembler.nomeExibicao(atual.getMusico())
                        + " já está escalado(a) em "
                        + outraCelebracao.getTitulo()
                        + " ("
                        + DATA.format(outraCelebracao.getData())
                        + " às "
                        + HORA.format(outraCelebracao.getHoraInicio())
                        + ").");
            }
        }
        return alertas;
    }
}
