package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.api.assembler.EscalaAssembler;
import br.gov.ce.sps.projetoa.api.dto.WhatsAppComunicacaoItemModel;
import br.gov.ce.sps.projetoa.api.dto.WhatsAppComunicacaoPreviaModel;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.filter.WhatsAppEnvioFilter;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacao;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoCelebracao;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoParticipacao;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoRepertorioItem;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.WhatsAppEnvio;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaPublicacaoCelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaPublicacaoParticipacaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaPublicacaoRepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaPublicacaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.WhatsAppEnvioRepository;
import br.gov.ce.sps.projetoa.domain.spec.WhatsAppEnvioSpec;
import br.gov.ce.sps.projetoa.infrastructure.util.TelefoneUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);

    private final WhatsAppMessageService whatsAppMessageService;
    private final WhatsAppTemplateService whatsAppTemplateService;
    private final WhatsAppLogService whatsAppLogService;
    private final WhatsAppEnvioRepository whatsAppEnvioRepository;
    private final EscalaPublicacaoRepository escalaPublicacaoRepository;
    private final EscalaPublicacaoCelebracaoRepository escalaPublicacaoCelebracaoRepository;
    private final EscalaPublicacaoParticipacaoRepository escalaPublicacaoParticipacaoRepository;
    private final EscalaPublicacaoRepertorioItemRepository escalaPublicacaoRepertorioItemRepository;
    private final MusicoRepository musicoRepository;
    private final CelebracaoRepository celebracaoRepository;

    @Transactional
    public void enfileirarPublicacao(UUID publicacaoCodigo) {
        EscalaPublicacao publicacao = escalaPublicacaoRepository.findByCodigo(publicacaoCodigo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Não existe publicação de escala com código " + publicacaoCodigo));
        if (publicacao.getVersao() != null && publicacao.getVersao() > 1) {
            int enfileirados = enfileirarAlteracoesDaPublicacao(publicacao);
            log.info("Republicação versão {} de {}/{}: {} alteração(ões) enfileirada(s) no WhatsApp.",
                    publicacao.getVersao(), publicacao.getMes(), publicacao.getAno(), enfileirados);
            return;
        }
        List<WhatsAppCelebracaoResumo> atuais = carregarResumos(publicacao);
        List<WhatsAppDestinatarioNotificacao> destinos = WhatsAppPublicacaoNotificacaoResolver.resolver(
                publicacao.getCodigo(),
                atuais);
        String competencia = whatsAppTemplateService.competencia(publicacao.getAno(), publicacao.getMes());
        enfileirarDestinos(destinos, competencia, publicacao.getVersao(), false);
    }

    @Transactional
    public int enfileirarLembretes(int ano, int mes) {
        if (mes < 1 || mes > 12) {
            throw new NegocioException("Informe um mês válido (1 a 12).");
        }
        EscalaPublicacao publicacao = escalaPublicacaoRepository.findFirstByAnoAndMesOrderByVersaoDesc(ano, mes)
                .orElseThrow(() -> new NegocioException(
                        "Não há escala publicada em " + whatsAppTemplateService.competencia(ano, mes) + "."));
        List<WhatsAppDestinatarioNotificacao> destinos = WhatsAppPublicacaoNotificacaoResolver.lembretes(
                publicacao.getCodigo(),
                carregarResumos(publicacao));
        return enfileirarDestinos(
                destinos,
                whatsAppTemplateService.competencia(ano, mes),
                publicacao.getVersao(),
                false);
    }

    @Transactional(readOnly = true)
    public WhatsAppComunicacaoPreviaModel previaComunicacao(int ano, int mes) {
        validarMes(mes);
        Optional<EscalaPublicacao> atualOpt = escalaPublicacaoRepository.findFirstByAnoAndMesOrderByVersaoDesc(ano, mes);
        if (atualOpt.isEmpty() || atualOpt.get().getVersao() == null || atualOpt.get().getVersao() < 2) {
            return previaVazia(ano, mes, atualOpt.orElse(null));
        }
        EscalaPublicacao atual = atualOpt.get();
        Optional<EscalaPublicacao> anteriorOpt = escalaPublicacaoRepository.findByAnoAndMesAndVersao(
                ano, mes, atual.getVersao() - 1);
        if (anteriorOpt.isEmpty()) {
            return previaVazia(ano, mes, atual);
        }
        List<WhatsAppDestinatarioNotificacao> destinos = destinosAlteracao(atual, anteriorOpt.get());
        WhatsAppComunicacaoPreviaModel model = previaVazia(ano, mes, atual);
        List<WhatsAppComunicacaoItemModel> itens = destinos.stream()
                .map(this::toItem)
                .toList();
        model.setItens(itens);
        model.setJaComunicada(!itens.isEmpty() && itens.stream()
                .allMatch(item -> item.getStatusEnvio() != null
                        && item.getStatusEnvio() != WhatsAppEnvioStatus.ERRO));
        return model;
    }

    @Transactional
    public int comunicarAlteracoes(int ano, int mes) {
        validarMes(mes);
        EscalaPublicacao atual = escalaPublicacaoRepository.findFirstByAnoAndMesOrderByVersaoDesc(ano, mes)
                .orElseThrow(() -> new NegocioException(
                        "Não há escala publicada em " + whatsAppTemplateService.competencia(ano, mes) + "."));
        if (atual.getVersao() == null || atual.getVersao() < 2) {
            throw new NegocioException("Não há alterações a comunicar nesta versão.");
        }
        EscalaPublicacao anterior = escalaPublicacaoRepository
                .findByAnoAndMesAndVersao(ano, mes, atual.getVersao() - 1)
                .orElseThrow(() -> new NegocioException("Não foi possível comparar com a versão anterior."));
        List<WhatsAppDestinatarioNotificacao> destinos = destinosAlteracao(atual, anterior);
        if (destinos.isEmpty()) {
            throw new NegocioException("Não há alterações a comunicar nesta versão.");
        }
        return enfileirarDestinos(
                destinos,
                whatsAppTemplateService.competencia(ano, mes),
                atual.getVersao(),
                true);
    }

    private int enfileirarAlteracoesDaPublicacao(EscalaPublicacao atual) {
        Optional<EscalaPublicacao> anteriorOpt = escalaPublicacaoRepository.findByAnoAndMesAndVersao(
                atual.getAno(), atual.getMes(), atual.getVersao() - 1);
        if (anteriorOpt.isEmpty()) {
            log.warn("Republicação {}/{} versão {}: versão anterior ausente; WhatsApp de alterações não enfileirado.",
                    atual.getMes(), atual.getAno(), atual.getVersao());
            return 0;
        }
        List<WhatsAppDestinatarioNotificacao> destinos = destinosAlteracao(atual, anteriorOpt.get());
        if (destinos.isEmpty()) {
            return 0;
        }
        return enfileirarDestinos(
                destinos,
                whatsAppTemplateService.competencia(atual.getAno(), atual.getMes()),
                atual.getVersao(),
                true);
    }

    @Transactional(readOnly = true)
    public Page<WhatsAppEnvio> listar(WhatsAppEnvioFilter filtro, Pageable pageable) {
        return whatsAppEnvioRepository.findAll(WhatsAppEnvioSpec.usandoFiltro(filtro), pageable);
    }

    @Transactional(readOnly = true)
    public Page<WhatsAppEnvio> listarErros(WhatsAppEnvioFilter filtro, Pageable pageable) {
        WhatsAppEnvioFilter erros = filtro == null ? new WhatsAppEnvioFilter() : filtro;
        erros.setStatus(WhatsAppEnvioStatus.ERRO);
        return listar(erros, pageable);
    }

    @Transactional(readOnly = true)
    public WhatsAppEnvio buscar(UUID codigo) {
        return whatsAppLogService.findByCode(codigo);
    }

    @Transactional
    public WhatsAppEnvio reenviar(UUID codigo) {
        return whatsAppMessageService.reenviar(codigo);
    }

    public int reenviarPendentesEErros() {
        return whatsAppMessageService.reenviarPendentesEErros();
    }

    private int enfileirarDestinos(
            List<WhatsAppDestinatarioNotificacao> destinos,
            String competencia,
            Integer versao,
            boolean reenviarErro) {
        if (destinos.isEmpty()) {
            return 0;
        }
        List<UUID> musicoCodigos = destinos.stream()
                .map(WhatsAppDestinatarioNotificacao::musicoCodigo)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<UUID, Musico> musicos = musicoCodigos.isEmpty()
                ? Map.of()
                : musicoRepository.findByCodigoIn(musicoCodigos).stream()
                .collect(Collectors.toMap(Musico::getCodigo, Function.identity(), (a, b) -> a));

        int criados = 0;
        for (WhatsAppDestinatarioNotificacao destino : destinos) {
            Musico musico = musicos.get(destino.musicoCodigo());
            String telefone = musico != null ? TelefoneUtils.normalizar(musico.getWhatsapp()) : null;
            String nome = musico != null ? EscalaAssembler.nomeExibicao(musico) : destino.musicoNome();
            WhatsAppMensagemContexto contexto = new WhatsAppMensagemContexto(
                    nome,
                    competencia,
                    versao,
                    destino.blocos());
            String mensagem = whatsAppTemplateService.montar(destino.tipo(), contexto);
            if (!StringUtils.hasText(telefone)) {
                log.warn("WhatsApp não enviado: músico {} sem telefone.", destino.musicoCodigo());
                WhatsAppEnvio envio = whatsAppMessageService.enfileirar(
                        destino.chaveIdempotencia(),
                        musico,
                        "NA",
                        destino.tipo(),
                        mensagem);
                if (envio.getStatus() == WhatsAppEnvioStatus.PENDENTE) {
                    whatsAppLogService.marcarErro(envio, "Músico sem WhatsApp cadastrado.");
                }
                continue;
            }
            WhatsAppEnvio existente = whatsAppLogService.findByChave(destino.chaveIdempotencia()).orElse(null);
            if (existente != null && existente.getStatus() == WhatsAppEnvioStatus.ENVIADO) {
                continue;
            }
            if (existente != null
                    && (existente.getStatus() == WhatsAppEnvioStatus.PENDENTE
                    || existente.getStatus() == WhatsAppEnvioStatus.PROCESSANDO)) {
                continue;
            }
            WhatsAppEnvio envio = whatsAppMessageService.enfileirar(
                    destino.chaveIdempotencia(),
                    musico,
                    telefone,
                    destino.tipo(),
                    mensagem);
            if (reenviarErro && envio.getStatus() == WhatsAppEnvioStatus.ERRO) {
                whatsAppMessageService.reenviar(envio.getCodigo());
                criados++;
                continue;
            }
            if (envio.getStatus() == WhatsAppEnvioStatus.PENDENTE && (envio.getTentativas() == null || envio.getTentativas() == 0)) {
                criados++;
            }
        }
        return criados;
    }

    private List<WhatsAppDestinatarioNotificacao> destinosAlteracao(
            EscalaPublicacao atual,
            EscalaPublicacao anterior) {
        List<WhatsAppCelebracaoResumo> atuais = carregarResumos(atual);
        List<WhatsAppCelebracaoResumo> anteriores = carregarResumos(anterior);
        return WhatsAppAlteracaoResolver.resolver(
                atual.getCodigo(),
                atuais,
                anteriores,
                identificarCanceladas(anteriores, atuais));
    }

    private WhatsAppComunicacaoPreviaModel previaVazia(int ano, int mes, EscalaPublicacao publicacao) {
        WhatsAppComunicacaoPreviaModel model = new WhatsAppComunicacaoPreviaModel();
        model.setAno(ano);
        model.setMes(mes);
        model.setCompetencia(whatsAppTemplateService.competencia(ano, mes));
        if (publicacao != null) {
            model.setPublicacaoCodigo(publicacao.getCodigo());
            model.setVersao(publicacao.getVersao());
        }
        model.setJaComunicada(false);
        model.setItens(List.of());
        return model;
    }

    private WhatsAppComunicacaoItemModel toItem(WhatsAppDestinatarioNotificacao destino) {
        WhatsAppComunicacaoItemModel item = new WhatsAppComunicacaoItemModel();
        item.setMusicoCodigo(destino.musicoCodigo());
        item.setMusicoNome(destino.musicoNome());
        item.setTipo(destino.tipo());
        item.setTipoRotulo(destino.tipo() != null ? destino.tipo().getRotulo() : null);
        item.setResumo(destino.resumo());
        if (!destino.blocos().isEmpty()) {
            WhatsAppEscalaMensalBloco bloco = destino.blocos().getFirst();
            item.setData(bloco.data());
            item.setCelebracaoTitulo(bloco.celebracaoTitulo());
        }
        whatsAppLogService.findByChave(destino.chaveIdempotencia())
                .ifPresent(envio -> item.setStatusEnvio(envio.getStatus()));
        return item;
    }

    private Set<UUID> identificarCanceladas(
            List<WhatsAppCelebracaoResumo> anteriores,
            List<WhatsAppCelebracaoResumo> atuais) {
        Set<UUID> atuaisCodigos = atuais.stream()
                .map(WhatsAppCelebracaoResumo::celebracaoCodigo)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<UUID> ausentes = anteriores.stream()
                .map(WhatsAppCelebracaoResumo::celebracaoCodigo)
                .filter(codigo -> codigo != null && !atuaisCodigos.contains(codigo))
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

    private static void validarMes(int mes) {
        if (mes < 1 || mes > 12) {
            throw new NegocioException("Informe um mês válido (1 a 12).");
        }
    }

    private List<WhatsAppCelebracaoResumo> carregarResumos(EscalaPublicacao publicacao) {
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
        List<WhatsAppCelebracaoResumo> resumos = new ArrayList<>();
        for (EscalaPublicacaoCelebracao cel : celebracoes) {
            List<WhatsAppParticipacaoResumo> participacoes = partes
                    .getOrDefault(cel.getId(), List.of())
                    .stream()
                    .sorted(Comparator.comparing(EscalaPublicacaoParticipacao::getOrdem, Comparator.nullsLast(Integer::compareTo)))
                    .map(p -> new WhatsAppParticipacaoResumo(
                            p.getMusicoCodigo(),
                            p.getMusicoNome(),
                            p.getInstrumentoCodigo(),
                            p.getInstrumentoNome()))
                    .toList();
            List<WhatsAppRepertorioItemResumo> repertorio = reps.getOrDefault(cel.getId(), List.of()).stream()
                    .sorted(Comparator.comparing(EscalaPublicacaoRepertorioItem::getOrdem, Comparator.nullsLast(Integer::compareTo)))
                    .map(r -> new WhatsAppRepertorioItemResumo(
                            r.getMomentoLiturgico(),
                            r.getMusicaTitulo(),
                            r.getTom(),
                            r.getOrdem()))
                    .toList();
            resumos.add(new WhatsAppCelebracaoResumo(
                    cel.getCelebracaoCodigo(),
                    cel.getCelebracaoTitulo(),
                    cel.getData(),
                    cel.getHoraInicio(),
                    cel.getHoraFim(),
                    cel.getLocalNome(),
                    participacoes,
                    repertorio));
        }
        return resumos;
    }
}
