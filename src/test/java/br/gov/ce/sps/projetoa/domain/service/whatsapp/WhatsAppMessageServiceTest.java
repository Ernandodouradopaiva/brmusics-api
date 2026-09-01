package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.core.ratelimit.InMemoryRateLimiter;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.WhatsAppEnvio;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import br.gov.ce.sps.projetoa.domain.repository.WhatsAppEnvioRepository;
import br.gov.ce.sps.projetoa.infrastructure.whatsapp.FakeWhatsAppClient;
import br.gov.ce.sps.projetoa.infrastructure.whatsapp.WhatsAppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WhatsAppMessageServiceTest {

    @Mock
    private WhatsAppLogService whatsAppLogService;
    @Mock
    private WhatsAppEnvioRepository whatsAppEnvioRepository;
    @Mock
    private ObjectProvider<WhatsAppMessageService> self;

    private WhatsAppProperties properties;
    private WhatsAppMessageService service;

    @BeforeEach
    void setUp() {
        properties = new WhatsAppProperties();
        properties.setEnabled(true);
        properties.setProvider("fake");
        properties.setMaxTentativas(3);
        properties.setRateLimitPerMinute(20);
        properties.setDefaultCountryCode("55");
        service = new WhatsAppMessageService(
                whatsAppLogService,
                whatsAppEnvioRepository,
                new FakeWhatsAppClient(),
                properties,
                new InMemoryRateLimiter(),
                self);
        lenient().when(self.getObject()).thenReturn(service);
    }

    @Test
    void naoDuplicaQuandoChaveJaExiste() {
        WhatsAppEnvio existente = envio(WhatsAppEnvioStatus.PENDENTE, 0);
        when(whatsAppLogService.findByChave("CHAVE-1")).thenReturn(Optional.of(existente));

        WhatsAppEnvio resultado = service.enfileirar(
                "CHAVE-1",
                existente.getMusico(),
                "8599998888",
                WhatsAppTipoMensagem.PUBLICACAO_ESCALA,
                "oi");

        assertThat(resultado).isSameAs(existente);
        verify(whatsAppLogService, never()).registrarPendente(any(), any(), any(), any(), any());
    }

    @Test
    void recusaReenvioDeMensagemJaEnviada() {
        WhatsAppEnvio enviado = envio(WhatsAppEnvioStatus.ENVIADO, 1);
        when(whatsAppLogService.findByCode(enviado.getCodigo())).thenReturn(enviado);

        assertThatThrownBy(() -> service.reenviar(enviado.getCodigo()))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("já foi concluído");
        verify(whatsAppLogService, never()).transicionar(any(), any(), any());
    }

    @Test
    void naoReprocessaQuandoAtingeMaximoDeTentativas() {
        WhatsAppEnvio erro = envio(WhatsAppEnvioStatus.ERRO, 3);
        when(whatsAppLogService.findByCode(erro.getCodigo())).thenReturn(erro);

        service.processarUm(erro.getCodigo());

        verify(whatsAppLogService, never()).transicionar(any(), any(), any());
        verify(whatsAppLogService, never()).marcarEnviado(any(), any());
    }

    @Test
    void processaPendenteComClienteFake() {
        WhatsAppEnvio pendente = envio(WhatsAppEnvioStatus.PENDENTE, 0);
        when(whatsAppLogService.findByCode(pendente.getCodigo())).thenReturn(pendente);
        when(whatsAppLogService.transicionar(
                eq(pendente.getId()),
                eq(WhatsAppEnvioStatus.PENDENTE),
                eq(WhatsAppEnvioStatus.PROCESSANDO))).thenReturn(true);

        service.processarUm(pendente.getCodigo());

        verify(whatsAppLogService).marcarEnviado(eq(pendente), any());
        assertThat(pendente.getTentativas()).isEqualTo(1);
    }

    private static WhatsAppEnvio envio(WhatsAppEnvioStatus status, int tentativas) {
        Musico musico = new Musico();
        musico.setCodigo(UUID.randomUUID());
        musico.setNome("ANA");
        musico.setWhatsapp("8599998888");
        WhatsAppEnvio envio = new WhatsAppEnvio();
        envio.setId(10L);
        envio.setCodigo(UUID.randomUUID());
        envio.setMusico(musico);
        envio.setTelefone("8599998888");
        envio.setTipoMensagem(WhatsAppTipoMensagem.PUBLICACAO_ESCALA);
        envio.setMensagem("oi");
        envio.setStatus(status);
        envio.setTentativas(tentativas);
        envio.setChaveIdempotencia("CHAVE-1");
        return envio;
    }
}
