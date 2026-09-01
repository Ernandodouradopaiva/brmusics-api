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
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaConfirmacaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import br.gov.ce.sps.projetoa.domain.service.celebracao.GetCelebracaoService;
import br.gov.ce.sps.projetoa.domain.service.instrumento.GetInstrumentoService;
import br.gov.ce.sps.projetoa.domain.service.musico.GetMusicoService;
import br.gov.ce.sps.projetoa.domain.service.musico.MusicoEscalaRegra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CadastroEscalaService {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EscalaRepository escalaRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final MusicoRepository musicoRepository;
    private final GetCelebracaoService getCelebracaoService;
    private final GetMusicoService getMusicoService;
    private final GetInstrumentoService getInstrumentoService;
    private final MusicoEscalaRegra musicoEscalaRegra;
    private final EscalaAssembler escalaAssembler;

    @Transactional
    public EscalaModel salvar(EscalaInput input) {
        Celebracao celebracao = getCelebracaoService.findByCode(input.getCelebracaoCodigo());
        if (escalaRepository.existsByCelebracao_Id(celebracao.getId())) {
            throw new NegocioException("Esta celebração já possui escala. Use a edição para alterar a equipe.");
        }
        Escala escala = new Escala();
        escala.setCelebracao(celebracao);
        escala.setStatus(EscalaStatus.RASCUNHO);
        escalaRepository.save(escala);
        List<String> alertas = sincronizarParticipacoes(escala, input.getParticipacoes(), true);
        return toModel(escala, alertas);
    }

    @Transactional
    public Escala obterOuCriarRascunho(Celebracao celebracao) {
        return escalaRepository.findByCelebracao_Id(celebracao.getId()).orElseGet(() -> {
            Escala nova = new Escala();
            nova.setCelebracao(celebracao);
            nova.setStatus(EscalaStatus.RASCUNHO);
            return escalaRepository.save(nova);
        });
    }

    void assertPodeAlterar(Escala escala) {
        Celebracao celebracao = escala.getCelebracao();
        if (celebracao != null && celebracao.getStatus() == CelebracaoStatus.CANCELADA) {
            throw new NegocioException("Não é possível alterar a escala de uma celebração cancelada.");
        }
    }

    List<String> sincronizarParticipacoes(Escala escala, List<EscalaMusicoInput> inputs, boolean resetarConfirmacao) {
        assertPodeAlterar(escala);
        List<EscalaMusicoInput> participacoes = inputs == null ? List.of() : inputs;
        validarDuplicidadeNaRequisicao(participacoes);

        List<EscalaMusico> existentes = escalaMusicoRepository.findByEscala_Id(escala.getId());
        Map<String, EscalaMusico> porChave = new LinkedHashMap<>();
        for (EscalaMusico em : existentes) {
            porChave.put(chave(em.getMusico().getId(), em.getInstrumento().getId()), em);
        }

        Set<String> mantidas = new HashSet<>();
        int ordem = 0;
        List<String> alertasInstrumento = new ArrayList<>();

        for (EscalaMusicoInput item : participacoes) {
            Musico musico = getMusicoService.findByCode(item.getMusicoCodigo());
            Instrumento instrumento = getInstrumentoService.findByCode(item.getInstrumentoCodigo());
            String chave = chave(musico.getId(), instrumento.getId());
            EscalaMusico atual = porChave.get(chave);
            boolean jaAtivo = atual != null && Boolean.TRUE.equals(atual.getAtivo());
            if (!jaAtivo) {
                musicoEscalaRegra.assertPodeEntrarEmNovaEscala(musico);
                if (!Boolean.TRUE.equals(instrumento.getAtivo())) {
                    throw new NegocioException("Não é possível escalar com o instrumento inativo: " + instrumento.getNome() + ".");
                }
            }
            if (!EscalaAssembler.possuiInstrumento(musico, instrumento)) {
                alertasInstrumento.add("O instrumento "
                        + instrumento.getNome()
                        + " não está cadastrado para "
                        + EscalaAssembler.nomeExibicao(musico)
                        + ".");
            }

            if (atual == null) {
                atual = new EscalaMusico();
                atual.setEscala(escala);
                atual.setMusico(musico);
                atual.setInstrumento(instrumento);
            }
            atual.setAtivo(Boolean.TRUE);
            atual.setOrdem(ordem++);
            atual.setObservacao(blankToNull(item.getObservacao()));
            if (resetarConfirmacao) {
                atual.setStatusConfirmacao(EscalaConfirmacaoStatus.PENDENTE);
            } else if (item.getStatusConfirmacao() != null) {
                atual.setStatusConfirmacao(item.getStatusConfirmacao());
            } else if (atual.getStatusConfirmacao() == null) {
                atual.setStatusConfirmacao(EscalaConfirmacaoStatus.PENDENTE);
            }
            escalaMusicoRepository.save(atual);
            mantidas.add(chave);
        }

        for (EscalaMusico em : existentes) {
            String chave = chave(em.getMusico().getId(), em.getInstrumento().getId());
            if (!mantidas.contains(chave) && Boolean.TRUE.equals(em.getAtivo())) {
                em.setAtivo(Boolean.FALSE);
                escalaMusicoRepository.save(em);
            }
        }

        List<String> alertas = new ArrayList<>(alertasInstrumento);
        alertas.addAll(alertasConflito(escala));
        return alertas;
    }

    List<String> alertasConflito(Escala escala) {
        Celebracao celebracao = escala.getCelebracao();
        if (celebracao == null || celebracao.getData() == null) {
            return List.of();
        }
        List<EscalaMusico> ativas = escalaMusicoRepository
                .findAtivasComMusicoEInstrumentoByEscalaIdIn(List.of(escala.getId()));
        if (ativas.isEmpty()) {
            return List.of();
        }
        List<Long> musicoIds = ativas.stream()
                .map(em -> em.getMusico().getId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<EscalaMusico> candidatas = escalaMusicoRepository.findAtivasPorMusicosEDatas(
                musicoIds, List.of(celebracao.getData()));
        List<String> alertas = new ArrayList<>();
        Set<String> vistos = new HashSet<>();
        for (EscalaMusico atual : ativas) {
            for (EscalaMusico outra : candidatas) {
                if (outra.getEscala().getId().equals(escala.getId())) {
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

    EscalaModel toModel(Escala escala, List<String> alertas) {
        List<EscalaMusico> ativas = new ArrayList<>(escalaMusicoRepository
                .findAtivasComMusicoEInstrumentoByEscalaIdIn(List.of(escala.getId())));
        ordenar(ativas);
        hidratarInstrumentosDoMusico(ativas);
        return escalaAssembler.toModel(escala, ativas, alertas);
    }

    static void ordenar(List<EscalaMusico> ativas) {
        ativas.sort(Comparator
                .comparing((EscalaMusico em) -> em.getOrdem() == null ? 0 : em.getOrdem())
                .thenComparing(em -> em.getId() == null ? 0L : em.getId()));
    }

    void hidratarInstrumentosDoMusico(List<EscalaMusico> ativas) {
        List<Long> ids = ativas.stream()
                .map(em -> em.getMusico() != null ? em.getMusico().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, Musico> comInstrumentos = musicoRepository.findWithInstrumentosByIdIn(ids).stream()
                .collect(Collectors.toMap(Musico::getId, m -> m, (a, b) -> a));
        for (EscalaMusico em : ativas) {
            if (em.getMusico() == null) {
                continue;
            }
            Musico carregado = comInstrumentos.get(em.getMusico().getId());
            if (carregado != null) {
                em.setMusico(carregado);
            }
        }
    }

    List<EscalaMusicoInput> toInputFromAtivas(List<EscalaMusico> ativas, boolean resetarConfirmacao) {
        List<EscalaMusicoInput> inputs = new ArrayList<>();
        for (EscalaMusico em : ativas) {
            if (em.getMusico() == null || !Boolean.TRUE.equals(em.getMusico().getAtivo())) {
                continue;
            }
            EscalaMusicoInput item = new EscalaMusicoInput();
            item.setMusicoCodigo(em.getMusico().getCodigo());
            item.setInstrumentoCodigo(em.getInstrumento().getCodigo());
            item.setObservacao(em.getObservacao());
            item.setStatusConfirmacao(resetarConfirmacao
                    ? EscalaConfirmacaoStatus.PENDENTE
                    : em.getStatusConfirmacao());
            inputs.add(item);
        }
        return inputs;
    }

    private static void validarDuplicidadeNaRequisicao(List<EscalaMusicoInput> participacoes) {
        Set<String> vistos = new HashSet<>();
        for (EscalaMusicoInput item : participacoes) {
            if (item.getMusicoCodigo() == null || item.getInstrumentoCodigo() == null) {
                throw new NegocioException("Informe o músico e a função de cada participação.");
            }
            String chave = item.getMusicoCodigo() + ":" + item.getInstrumentoCodigo();
            if (!vistos.add(chave)) {
                throw new NegocioException("O mesmo músico não pode ser escalado duas vezes com a mesma função na celebração.");
            }
        }
    }

    private static String chave(Long musicoId, Long instrumentoId) {
        return musicoId + ":" + instrumentoId;
    }

    private static String blankToNull(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        return valor.trim();
    }
}
