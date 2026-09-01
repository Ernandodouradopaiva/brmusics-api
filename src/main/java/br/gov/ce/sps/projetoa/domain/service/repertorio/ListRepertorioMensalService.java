package br.gov.ce.sps.projetoa.domain.service.repertorio;

import br.gov.ce.sps.projetoa.api.assembler.RepertorioAssembler;
import br.gov.ce.sps.projetoa.api.dto.RepertorioMensalItemModel;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListRepertorioMensalService {

    private final CelebracaoRepository celebracaoRepository;
    private final RepertorioRepository repertorioRepository;
    private final RepertorioItemRepository repertorioItemRepository;
    private final RepertorioAssembler repertorioAssembler;

    @Transactional(readOnly = true)
    public List<RepertorioMensalItemModel> listar(int ano, int mes) {
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
        Map<Long, Repertorio> repertorioPorCelebracao = repertorioRepository
                .findComCelebracaoByCelebracaoIdIn(celebracaoIds)
                .stream()
                .collect(Collectors.toMap(r -> r.getCelebracao().getId(), r -> r, (a, b) -> a));

        List<Long> repertorioIds = repertorioPorCelebracao.values().stream().map(Repertorio::getId).toList();
        Map<Long, List<RepertorioItem>> itensPorRepertorio = repertorioIds.isEmpty()
                ? Map.of()
                : repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(repertorioIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRepertorio().getId()));

        List<RepertorioMensalItemModel> itens = new ArrayList<>();
        for (Celebracao celebracao : celebracoes) {
            Repertorio repertorio = repertorioPorCelebracao.get(celebracao.getId());
            List<RepertorioItem> ativos = repertorio == null
                    ? List.of()
                    : itensPorRepertorio.getOrDefault(repertorio.getId(), List.of());
            itens.add(repertorioAssembler.toMensalItem(celebracao, repertorio, ativos));
        }
        return itens;
    }
}
