package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.core.ratelimit.InMemoryRateLimiter;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.WhatsAppEnvio;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import br.gov.ce.sps.projetoa.domain.repository.WhatsAppEnvioRepository;
import br.gov.ce.sps.projetoa.infrastructure.whatsapp.WhatsAppClient;
import br.gov.ce.sps.projetoa.infrastructure.whatsapp.WhatsAppPhoneFormatter;
import br.gov.ce.sps.projetoa.infrastructure.whatsapp.WhatsAppProperties;
import br.gov.ce.sps.projetoa.infrastructure.whatsapp.WhatsAppSendResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WhatsAppMessageService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppMessageService.class);
    private static final String RATE_KEY = "whatsapp-cloud";

    private final WhatsAppLogService whatsAppLogService;
    private final WhatsAppEnvioRepository whatsAppEnvioRepository;
    private final WhatsAppClient whatsAppClient;
    private final WhatsAppProperties properties;
    private final InMemoryRateLimiter rateLimiter;
    private final ObjectProvider<WhatsAppMessageService> self;

    @Transactional
    public WhatsAppEnvio enfileirar(
            String chaveIdempotencia,
            Musico musico,
            String telefone,
            WhatsAppTipoMensagem tipo,
            String mensagem) {
        if (!StringUtils.hasText(chaveIdempotencia)) {
            throw new NegocioException("Chave de idempotência do envio WhatsApp é obrigatória.");
        }
        return whatsAppLogService.findByChave(chaveIdempotencia)
                .orElseGet(() -> criarPendente(chaveIdempotencia, musico, telefone, tipo, mensagem));
    }

    @Transactional
    public WhatsAppEnvio reenviar(UUID codigo) {
        WhatsAppEnvio envio = whatsAppLogService.findByCode(codigo);
        if (envio.getStatus() == WhatsAppEnvioStatus.ENVIADO) {
            throw new NegocioException("Este envio já foi concluído. Evite duplicidade.");
        }
        if (envio.getStatus() == WhatsAppEnvioStatus.PROCESSANDO) {
            throw new NegocioException("Este envio já está em processamento.");
        }
        if (envio.getStatus() != WhatsAppEnvioStatus.ERRO && envio.getStatus() != WhatsAppEnvioStatus.PENDENTE) {
            throw new NegocioException("Somente envios pendentes ou com erro podem ser reenviados.");
        }
        envio.setStatus(WhatsAppEnvioStatus.PENDENTE);
        envio.setErro(null);
        whatsAppEnvioRepository.save(envio);
        whatsAppEnvioRepository.flush();
        self.getObject().processarUm(envio.getCodigo(), true);
        return whatsAppLogService.findByCode(codigo);
    }

    public int reenviarPendentesEErros() {
        List<WhatsAppEnvio> candidatos = whatsAppEnvioRepository.findByStatusInOrderByDataSolicitacaoAsc(
                List.of(WhatsAppEnvioStatus.PENDENTE, WhatsAppEnvioStatus.ERRO));
        int reenviados = 0;
        WhatsAppMessageService proxy = self.getObject();
        for (WhatsAppEnvio candidato : candidatos) {
            if (!rateLimiter.tryConsume(RATE_KEY, Math.max(1, properties.getRateLimitPerMinute()), 60)) {
                log.debug("Rate limit do WhatsApp atingido no reenvio em lote.");
                break;
            }
            try {
                proxy.reenviar(candidato.getCodigo());
                reenviados++;
            } catch (RuntimeException ex) {
                log.warn("Falha ao reenviar WhatsApp {}: {}", candidato.getCodigo(), ex.getMessage());
            }
        }
        return reenviados;
    }

    public void processarFila() {
        if (!properties.isEnabled()) {
            return;
        }
        liberarProcessamentosPresos();
        List<WhatsAppEnvio> candidatos = whatsAppEnvioRepository.findAll(
                (root, query, builder) -> builder.or(
                        builder.equal(root.get("status"), WhatsAppEnvioStatus.PENDENTE),
                        builder.and(
                                builder.equal(root.get("status"), WhatsAppEnvioStatus.ERRO),
                                builder.lessThan(root.get("tentativas"), properties.getMaxTentativas()),
                                builder.lessThanOrEqualTo(
                                        root.get("dataAtualizacao"),
                                        OffsetDateTime.now().minusSeconds(properties.getRetryIntervalSeconds())))),
                PageRequest.of(0, Math.max(1, properties.getBatchSize()), Sort.by("dataSolicitacao").ascending())
        ).getContent();

        WhatsAppMessageService proxy = self.getObject();
        for (WhatsAppEnvio candidato : candidatos) {
            if (!rateLimiter.tryConsume(RATE_KEY, Math.max(1, properties.getRateLimitPerMinute()), 60)) {
                log.debug("Rate limit do WhatsApp atingido; demais envios ficam na fila.");
                break;
            }
            proxy.processarUm(candidato.getCodigo());
        }
    }

    @Transactional
    public void processarUm(UUID codigo) {
        processarUm(codigo, false);
    }

    @Transactional
    public void processarUm(UUID codigo, boolean forcarManual) {
        WhatsAppEnvio envio = whatsAppLogService.findByCode(codigo);
        if (envio.getStatus() == WhatsAppEnvioStatus.ENVIADO) {
            return;
        }
        if (!forcarManual
                && envio.getStatus() == WhatsAppEnvioStatus.ERRO
                && envio.getTentativas() != null
                && envio.getTentativas() >= properties.getMaxTentativas()) {
            return;
        }
        WhatsAppEnvioStatus origem = envio.getStatus();
        if (origem != WhatsAppEnvioStatus.PENDENTE && origem != WhatsAppEnvioStatus.ERRO) {
            return;
        }
        if (!whatsAppLogService.transicionar(envio.getId(), origem, WhatsAppEnvioStatus.PROCESSANDO)) {
            return;
        }
        WhatsAppEnvio atual = whatsAppLogService.findByCode(codigo);
        atual.setTentativas(atual.getTentativas() == null ? 1 : atual.getTentativas() + 1);
        atual.setStatus(WhatsAppEnvioStatus.PROCESSANDO);
        whatsAppEnvioRepository.save(atual);

        String destino = WhatsAppPhoneFormatter.paraE164(atual.getTelefone(), properties.getDefaultCountryCode());
        if (!StringUtils.hasText(destino) || "NA".equalsIgnoreCase(atual.getTelefone())) {
            whatsAppLogService.marcarErro(atual, "Telefone inválido para envio.");
            return;
        }
        WhatsAppSendResult resultado = whatsAppClient.enviar(destino, atual.getMensagem());
        if (resultado.sucesso()) {
            whatsAppLogService.marcarEnviado(atual, resultado.providerMessageId());
        } else {
            whatsAppLogService.marcarErro(
                    atual,
                    StringUtils.hasText(resultado.erro()) ? resultado.erro() : "Falha ao enviar mensagem.");
        }
    }

    private WhatsAppEnvio criarPendente(
            String chaveIdempotencia,
            Musico musico,
            String telefone,
            WhatsAppTipoMensagem tipo,
            String mensagem) {
        try {
            return whatsAppLogService.registrarPendente(chaveIdempotencia, musico, telefone, tipo, mensagem);
        } catch (DataIntegrityViolationException ex) {
            return whatsAppLogService.findByChave(chaveIdempotencia)
                    .orElseThrow(() -> ex);
        }
    }

    private void liberarProcessamentosPresos() {
        OffsetDateTime limite = OffsetDateTime.now().minusSeconds(Math.max(30, properties.getStuckProcessingSeconds()));
        List<WhatsAppEnvio> presos = whatsAppEnvioRepository.findByStatusAndDataAtualizacaoBefore(
                WhatsAppEnvioStatus.PROCESSANDO, limite);
        for (WhatsAppEnvio preso : presos) {
            whatsAppLogService.transicionar(preso.getId(), WhatsAppEnvioStatus.PROCESSANDO, WhatsAppEnvioStatus.PENDENTE);
        }
    }
}
