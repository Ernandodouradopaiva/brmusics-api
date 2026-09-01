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
import br.gov.ce.sps.projetoa.domain.model.enums.RepertorioStatus;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import br.gov.ce.sps.projetoa.domain.service.celebracao.GetCelebracaoService;
import br.gov.ce.sps.projetoa.domain.service.musica.GetMusicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CadastroRepertorioService {

    private final RepertorioRepository repertorioRepository;
    private final RepertorioItemRepository repertorioItemRepository;
    private final GetCelebracaoService getCelebracaoService;
    private final GetMusicaService getMusicaService;
    private final RepertorioAssembler repertorioAssembler;

    @Transactional
    public RepertorioModel salvar(RepertorioInput input) {
        Celebracao celebracao = getCelebracaoService.findByCode(input.getCelebracaoCodigo());
        if (repertorioRepository.existsByCelebracao_Id(celebracao.getId())) {
            throw new NegocioException("Esta celebração já possui repertório. Use a edição para alterar as músicas.");
        }
        Repertorio repertorio = new Repertorio();
        repertorio.setCelebracao(celebracao);
        repertorio.setStatus(RepertorioStatus.RASCUNHO);
        repertorio.setObservacao(blankToNull(input.getObservacao()));
        repertorioRepository.save(repertorio);
        sincronizarItens(repertorio, input.getItens());
        return toModel(repertorio);
    }

    void assertPodeAlterar(Repertorio repertorio) {
        if (repertorio.getStatus() == RepertorioStatus.PUBLICADA) {
            throw new NegocioException("Repertório publicado não pode ser alterado.");
        }
    }

    void sincronizarItens(Repertorio repertorio, List<RepertorioItemInput> inputs) {
        assertPodeAlterar(repertorio);
        List<RepertorioItemInput> itens = inputs == null ? List.of() : inputs;
        List<RepertorioItem> existentes = repertorioItemRepository.findByRepertorio_Id(repertorio.getId());
        Map<UUID, RepertorioItem> porCodigo = new HashMap<>();
        for (RepertorioItem existente : existentes) {
            if (existente.getCodigo() != null) {
                porCodigo.put(existente.getCodigo(), existente);
            }
        }

        Set<UUID> mantidos = new HashSet<>();
        Map<String, Integer> proximaOrdemPorMomento = new HashMap<>();

        for (RepertorioItemInput itemInput : itens) {
            String momento = CategoriasLiturgicas.normalizar(itemInput.getMomentoLiturgico());
            if (!StringUtils.hasText(momento)) {
                throw new NegocioException("Informe o momento litúrgico de cada música do repertório.");
            }
            if (itemInput.getMusicaCodigo() == null) {
                throw new NegocioException("Informe a música de cada item do repertório.");
            }

            Musica musica = getMusicaService.findByCode(itemInput.getMusicaCodigo());
            RepertorioItem atual = itemInput.getCodigo() == null ? null : porCodigo.get(itemInput.getCodigo());
            if (itemInput.getCodigo() != null && atual == null) {
                throw new NegocioException("Item de repertório não encontrado neste cadastro.");
            }
            if (atual != null
                    && atual.getRepertorio() != null
                    && atual.getRepertorio().getId() != null
                    && !atual.getRepertorio().getId().equals(repertorio.getId())) {
                throw new NegocioException("Item de repertório não pertence a esta celebração.");
            }

            boolean novo = atual == null || !Boolean.TRUE.equals(atual.getAtivo());
            if (novo && !Boolean.TRUE.equals(musica.getAtivo())) {
                throw new NegocioException("Música inativa não pode ser incluída no repertório.");
            }

            if (atual == null) {
                atual = new RepertorioItem();
                atual.setRepertorio(repertorio);
            }
            atual.setMusica(musica);
            atual.setMomentoLiturgico(momento);
            atual.setAtivo(Boolean.TRUE);
            atual.setObservacao(blankToNull(itemInput.getObservacao()));
            atual.setTom(resolverTom(itemInput.getTom(), musica));
            int ordem = itemInput.getOrdem() != null
                    ? itemInput.getOrdem()
                    : proximaOrdemPorMomento.getOrDefault(momento, 0);
            atual.setOrdem(ordem);
            proximaOrdemPorMomento.put(momento, Math.max(proximaOrdemPorMomento.getOrDefault(momento, 0), ordem + 1));
            repertorioItemRepository.save(atual);
            if (atual.getCodigo() != null) {
                mantidos.add(atual.getCodigo());
            }
        }

        for (RepertorioItem existente : existentes) {
            if (Boolean.TRUE.equals(existente.getAtivo())
                    && (existente.getCodigo() == null || !mantidos.contains(existente.getCodigo()))) {
                existente.setAtivo(Boolean.FALSE);
                repertorioItemRepository.save(existente);
            }
        }
    }

    RepertorioModel toModel(Repertorio repertorio) {
        List<RepertorioItem> ativas = new ArrayList<>(repertorioItemRepository
                .findAtivosComMusicaByRepertorioIdIn(List.of(repertorio.getId())));
        return repertorioAssembler.toModel(repertorio, ativas);
    }

    /**
     * Tom do item é exclusivo da celebração. Nunca grava em {@code musica.tomPadrao}.
     */
    static String resolverTom(String tomInformado, Musica musica) {
        if (StringUtils.hasText(tomInformado)) {
            return tomInformado.trim();
        }
        if (musica != null && StringUtils.hasText(musica.getTomPadrao())) {
            return musica.getTomPadrao();
        }
        return null;
    }

    private static String blankToNull(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        return valor.trim();
    }
}
