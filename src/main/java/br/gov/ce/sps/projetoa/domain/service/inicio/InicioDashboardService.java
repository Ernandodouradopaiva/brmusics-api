package br.gov.ce.sps.projetoa.domain.service.inicio;

import br.gov.ce.sps.projetoa.api.dto.DashboardCelebracaoResumoModel;
import br.gov.ce.sps.projetoa.api.dto.DashboardCoordenadorModel;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InicioDashboardService {

    static final int JANELA_DIAS = 45;
    static final int LIMITE_PROXIMAS = 5;

    private final CelebracaoRepository celebracaoRepository;
    private final EscalaRepository escalaRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final RepertorioRepository repertorioRepository;
    private final RepertorioItemRepository repertorioItemRepository;
    private final WhatsAppEnvioRepository whatsAppEnvioRepository;

    @Transactional(readOnly = true)
    public DashboardCoordenadorModel montar() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDate inicio = agora.toLocalDate();
        LocalDate fim = inicio.plusDays(JANELA_DIAS);

        List<Celebracao> celebracoes = celebracaoRepository
                .findByDataBetweenOrderByDataAscHoraInicioAsc(inicio, fim)
                .stream()
                .filter(InicioDashboardService::celebracaoVisivel)
                .filter(c -> !jaOcorreu(c, agora))
                .toList();

        List<Long> celebracaoIds = celebracoes.stream().map(Celebracao::getId).toList();
        Map<Long, Escala> escalaPorCelebracao = celebracaoIds.isEmpty()
                ? Map.of()
                : escalaRepository.findComCelebracaoByCelebracaoIdIn(celebracaoIds).stream()
                .collect(Collectors.toMap(e -> e.getCelebracao().getId(), e -> e, (a, b) -> a));

        List<Long> escalaIds = escalaPorCelebracao.values().stream().map(Escala::getId).toList();
        Map<Long, List<EscalaMusico>> ativasPorEscala = escalaIds.isEmpty()
                ? Map.of()
                : escalaMusicoRepository.findAtivasComMusicoEInstrumentoByEscalaIdIn(escalaIds).stream()
                .collect(Collectors.groupingBy(em -> em.getEscala().getId()));

        Map<Long, Repertorio> repertorioPorCelebracao = celebracaoIds.isEmpty()
                ? Map.of()
                : repertorioRepository.findComCelebracaoByCelebracaoIdIn(celebracaoIds).stream()
                .collect(Collectors.toMap(r -> r.getCelebracao().getId(), r -> r, (a, b) -> a));

        List<Long> repertorioIds = repertorioPorCelebracao.values().stream().map(Repertorio::getId).toList();
        Map<Long, List<RepertorioItem>> itensPorRepertorio = repertorioIds.isEmpty()
                ? Map.of()
                : repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(repertorioIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRepertorio().getId()));

        int escalasRascunho = 0;
        int repertoriosIncompletos = 0;
        int confirmacoesPendentes = 0;
        List<DashboardCelebracaoResumoModel> itens = new ArrayList<>();

        for (Celebracao celebracao : celebracoes) {
            Escala escala = escalaPorCelebracao.get(celebracao.getId());
            List<EscalaMusico> ativas = escala == null
                    ? List.of()
                    : ativasPorEscala.getOrDefault(escala.getId(), List.of());
            Repertorio repertorio = repertorioPorCelebracao.get(celebracao.getId());
            List<RepertorioItem> musicas = repertorio == null
                    ? List.of()
                    : itensPorRepertorio.getOrDefault(repertorio.getId(), List.of());
            boolean repertorioCompleto = !musicas.isEmpty();
            boolean escalaPublicada = escala != null && escala.getStatus() == EscalaStatus.PUBLICADA;

            if (escala == null || escala.isRascunho()) {
                escalasRascunho++;
            }
            if (!repertorioCompleto) {
                repertoriosIncompletos++;
            }
            if (escalaPublicada) {
                confirmacoesPendentes += (int) ativas.stream()
                        .filter(em -> em.getStatusConfirmacao() == null
                                || em.getStatusConfirmacao() == EscalaConfirmacaoStatus.PENDENTE)
                        .count();
            }
            itens.add(toResumo(celebracao, escala, ativas.size(), repertorioCompleto, escalaPublicada));
        }

        int whatsappPendentes = toInt(whatsAppEnvioRepository.countByStatus(WhatsAppEnvioStatus.PENDENTE));
        int whatsappErros = toInt(whatsAppEnvioRepository.countByStatus(WhatsAppEnvioStatus.ERRO));

        DashboardCoordenadorModel dashboard = new DashboardCoordenadorModel();
        dashboard.setEscalasRascunho(escalasRascunho);
        dashboard.setWhatsappPendentes(whatsappPendentes);
        dashboard.setWhatsappErros(whatsappErros);
        dashboard.setRepertoriosIncompletos(repertoriosIncompletos);
        dashboard.setConfirmacoesPendentes(confirmacoesPendentes);
        dashboard.setAlertas(montarAlertas(repertoriosIncompletos, whatsappErros, confirmacoesPendentes));
        if (!itens.isEmpty()) {
            DashboardCelebracaoResumoModel proxima = itens.getFirst();
            dashboard.setProximaCelebracao(proxima);
            dashboard.setMusicosEscaladosProxima(proxima.getQuantidadeMusicos());
            int fimLista = Math.min(itens.size(), 1 + LIMITE_PROXIMAS);
            dashboard.setProximasCelebracoes(itens.size() > 1 ? List.copyOf(itens.subList(1, fimLista)) : List.of());
        } else {
            dashboard.setProximasCelebracoes(List.of());
        }
        return dashboard;
    }

    static List<String> montarAlertas(int repertoriosIncompletos, int whatsappErros, int confirmacoesPendentes) {
        List<String> alertas = new ArrayList<>();
        if (repertoriosIncompletos > 0) {
            alertas.add(repertoriosIncompletos == 1
                    ? "1 celebração está sem repertório."
                    : repertoriosIncompletos + " celebrações estão sem repertório.");
        }
        if (whatsappErros > 0) {
            alertas.add(whatsappErros == 1
                    ? "1 envio de WhatsApp apresentou erro."
                    : whatsappErros + " envios de WhatsApp apresentaram erro.");
        }
        if (confirmacoesPendentes > 0) {
            alertas.add(confirmacoesPendentes == 1
                    ? "1 músico ainda não confirmou presença."
                    : confirmacoesPendentes + " músicos ainda não confirmaram presença.");
        }
        return List.copyOf(alertas);
    }

    private static DashboardCelebracaoResumoModel toResumo(
            Celebracao celebracao,
            Escala escala,
            int quantidadeMusicos,
            boolean repertorioCompleto,
            boolean escalaPublicada) {
        DashboardCelebracaoResumoModel item = new DashboardCelebracaoResumoModel();
        item.setCelebracaoCodigo(celebracao.getCodigo());
        item.setEscalaCodigo(escala != null ? escala.getCodigo() : null);
        item.setTitulo(celebracao.getTitulo());
        item.setData(celebracao.getData());
        item.setHoraInicio(celebracao.getHoraInicio());
        item.setLocalNome(celebracao.getLocal() != null ? celebracao.getLocal().getNome() : null);
        item.setQuantidadeMusicos(quantidadeMusicos);
        item.setRepertorioCompleto(repertorioCompleto);
        item.setEscalaPublicada(escalaPublicada);
        return item;
    }

    private static boolean celebracaoVisivel(Celebracao celebracao) {
        CelebracaoStatus status = celebracao.getStatus();
        return status != CelebracaoStatus.CANCELADA && status != CelebracaoStatus.REALIZADA;
    }

    private static boolean jaOcorreu(Celebracao celebracao, LocalDateTime agora) {
        if (celebracao.getData() == null) {
            return false;
        }
        LocalTime hora = celebracao.getHoraInicio() != null ? celebracao.getHoraInicio() : LocalTime.MIN;
        return LocalDateTime.of(celebracao.getData(), hora).isBefore(agora);
    }

    private static int toInt(long valor) {
        return (int) Math.min(valor, Integer.MAX_VALUE);
    }
}
