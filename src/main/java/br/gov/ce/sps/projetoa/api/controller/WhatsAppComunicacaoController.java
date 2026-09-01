package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.dto.WhatsAppComunicacaoPreviaModel;
import br.gov.ce.sps.projetoa.api.input.WhatsAppComunicacaoInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.service.whatsapp.WhatsAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/whatsapp/comunicacoes")
@RequiredArgsConstructor
public class WhatsAppComunicacaoController {

    private final WhatsAppService whatsAppService;

    @PreAuthorize("hasAnyAuthority('"
            + Permissoes.Escala.PUBLICAR + "','"
            + Permissoes.WhatsApp.LISTAR + "','"
            + Permissoes.WhatsApp.ENVIAR + "')")
    @GetMapping("/previa")
    public WhatsAppComunicacaoPreviaModel previa(
            @RequestParam int ano,
            @RequestParam int mes) {
        return whatsAppService.previaComunicacao(ano, mes);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.WhatsApp.ENVIAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, Integer> comunicar(@Valid @RequestBody WhatsAppComunicacaoInput input) {
        int enfileirados = whatsAppService.comunicarAlteracoes(input.ano(), input.mes());
        return Map.of("enfileirados", enfileirados);
    }
}
