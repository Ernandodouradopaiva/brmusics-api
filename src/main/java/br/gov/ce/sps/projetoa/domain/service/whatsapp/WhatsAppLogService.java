package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.WhatsAppEnvio;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import br.gov.ce.sps.projetoa.domain.repository.WhatsAppEnvioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WhatsAppLogService {

    private static final String MSG_NAO_ENCONTRADO = "Não existe um envio de WhatsApp com código %s";

    private final WhatsAppEnvioRepository whatsAppEnvioRepository;

    @Transactional
    public WhatsAppEnvio registrarPendente(
            String chaveIdempotencia,
            Musico musico,
            String telefone,
            WhatsAppTipoMensagem tipo,
            String mensagem) {
        Optional<WhatsAppEnvio> existente = whatsAppEnvioRepository.findByChaveIdempotencia(chaveIdempotencia);
        if (existente.isPresent()) {
            return existente.get();
        }
        WhatsAppEnvio envio = new WhatsAppEnvio();
        envio.setChaveIdempotencia(chaveIdempotencia);
        envio.setMusico(musico);
        envio.setTelefone(telefone);
        envio.setTipoMensagem(tipo);
        envio.setMensagem(mensagem);
        envio.setStatus(WhatsAppEnvioStatus.PENDENTE);
        envio.setTentativas(0);
        envio.setDataSolicitacao(OffsetDateTime.now());
        return whatsAppEnvioRepository.save(envio);
    }

    @Transactional(readOnly = true)
    public WhatsAppEnvio findByCode(UUID codigo) {
        return whatsAppEnvioRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, codigo)));
    }

    @Transactional(readOnly = true)
    public Optional<WhatsAppEnvio> findByChave(String chaveIdempotencia) {
        return whatsAppEnvioRepository.findByChaveIdempotencia(chaveIdempotencia);
    }

    @Transactional
    public WhatsAppEnvio marcarEnviado(WhatsAppEnvio envio, String providerMessageId) {
        envio.setStatus(WhatsAppEnvioStatus.ENVIADO);
        envio.setProviderMessageId(providerMessageId);
        envio.setDataEnvio(OffsetDateTime.now());
        envio.setErro(null);
        return whatsAppEnvioRepository.save(envio);
    }

    @Transactional
    public WhatsAppEnvio marcarErro(WhatsAppEnvio envio, String erro) {
        envio.setStatus(WhatsAppEnvioStatus.ERRO);
        envio.setErro(truncar(erro, 2000));
        return whatsAppEnvioRepository.save(envio);
    }

    @Transactional
    public WhatsAppEnvio marcarProcessando(WhatsAppEnvio envio) {
        envio.setStatus(WhatsAppEnvioStatus.PROCESSANDO);
        envio.setTentativas(envio.getTentativas() == null ? 1 : envio.getTentativas() + 1);
        envio.setErro(null);
        return whatsAppEnvioRepository.save(envio);
    }

    @Transactional
    public void atualizarStatusProvedor(String providerMessageId, String statusProvedor, String erro) {
        if (!StringUtils.hasText(providerMessageId)) {
            return;
        }
        whatsAppEnvioRepository.findByProviderMessageId(providerMessageId).ifPresent(envio -> {
            if ("failed".equalsIgnoreCase(statusProvedor)) {
                envio.setStatus(WhatsAppEnvioStatus.ERRO);
                envio.setErro(truncar(StringUtils.hasText(erro) ? erro : "Falha reportada pelo WhatsApp.", 2000));
                whatsAppEnvioRepository.save(envio);
            }
        });
    }

    @Transactional
    public boolean transicionar(Long id, WhatsAppEnvioStatus origem, WhatsAppEnvioStatus destino) {
        return whatsAppEnvioRepository.transicionarStatus(id, origem, destino) == 1;
    }

    private static String truncar(String texto, int max) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}
