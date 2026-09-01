package br.gov.ce.sps.projetoa.domain.service.repertorio;

import br.gov.ce.sps.projetoa.api.assembler.RepertorioAssembler;
import br.gov.ce.sps.projetoa.api.dto.RepertorioMensalItemModel;
import br.gov.ce.sps.projetoa.api.dto.RepertorioModel;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import br.gov.ce.sps.projetoa.domain.service.escala.MinhaEscalaService;
import br.gov.ce.sps.projetoa.domain.service.musico.MusicoAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeuRepertorioService {

    private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de repertório com código %s";

    private final MusicoAutenticadoService musicoAutenticadoService;
    private final MinhaEscalaService minhaEscalaService;
    private final RepertorioRepository repertorioRepository;
    private final RepertorioItemRepository repertorioItemRepository;
    private final RepertorioAssembler repertorioAssembler;

    @Transactional(readOnly = true)
    public List<RepertorioMensalItemModel> listar() {
        Musico musico = musicoAutenticadoService.exigirMusicoVinculado();
        List<Celebracao> celebracoes = minhaEscalaService.celebracoesPublicadasDoMusico(musico).stream()
                .sorted(Comparator
                        .comparing(Celebracao::getData, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Celebracao::getHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        if (celebracoes.isEmpty()) {
            return List.of();
        }
        List<Long> celebracaoIds = celebracoes.stream().map(Celebracao::getId).toList();
        Map<Long, Repertorio> porCelebracao = repertorioRepository.findComCelebracaoByCelebracaoIdIn(celebracaoIds)
                .stream()
                .collect(Collectors.toMap(r -> r.getCelebracao().getId(), r -> r, (a, b) -> a));
        List<Long> repertorioIds = porCelebracao.values().stream().map(Repertorio::getId).toList();
        Map<Long, List<RepertorioItem>> itensPorRepertorio = repertorioIds.isEmpty()
                ? Map.of()
                : repertorioItemRepository.findAtivosComMusicaByRepertorioIdIn(repertorioIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRepertorio().getId()));

        List<RepertorioMensalItemModel> itens = new ArrayList<>();
        for (Celebracao celebracao : celebracoes) {
            Repertorio repertorio = porCelebracao.get(celebracao.getId());
            List<RepertorioItem> ativos = repertorio == null
                    ? List.of()
                    : itensPorRepertorio.getOrDefault(repertorio.getId(), List.of());
            itens.add(repertorioAssembler.toMensalItem(celebracao, repertorio, ativos));
        }
        return itens;
    }

    @Transactional(readOnly = true)
    public RepertorioModel buscar(UUID codigo) {
        Musico musico = musicoAutenticadoService.exigirMusicoVinculado();
        Repertorio repertorio = repertorioRepository.findComCelebracaoByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, codigo)));
        garantirAcesso(musico, repertorio.getCelebracao(), codigo);
        return toModel(repertorio);
    }

    @Transactional(readOnly = true)
    public RepertorioModel buscarPorCelebracao(UUID celebracaoCodigo) {
        Musico musico = musicoAutenticadoService.exigirMusicoVinculado();
        Repertorio repertorio = repertorioRepository.findComCelebracaoByCelebracaoCodigo(celebracaoCodigo)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(MSG_NAO_ENCONTRADO, celebracaoCodigo)));
        garantirAcesso(musico, repertorio.getCelebracao(), celebracaoCodigo);
        return toModel(repertorio);
    }

    private RepertorioModel toModel(Repertorio repertorio) {
        List<RepertorioItem> itens = repertorioItemRepository
                .findAtivosComMusicaByRepertorioIdIn(List.of(repertorio.getId()));
        return repertorioAssembler.toModel(repertorio, itens);
    }

    private void garantirAcesso(Musico musico, Celebracao celebracao, UUID codigoConsulta) {
        UUID celebracaoCodigo = celebracao != null ? celebracao.getCodigo() : null;
        if (!minhaEscalaService.musicoParticipaDaCelebracao(musico, celebracaoCodigo)) {
            throw new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, codigoConsulta));
        }
    }
}
