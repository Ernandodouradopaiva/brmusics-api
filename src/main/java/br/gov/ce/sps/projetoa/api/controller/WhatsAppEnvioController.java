package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.WhatsAppEnvioAssembler;
import br.gov.ce.sps.projetoa.api.dto.WhatsAppEnvioModel;
import br.gov.ce.sps.projetoa.api.input.WhatsAppLembreteInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.filter.WhatsAppEnvioFilter;
import br.gov.ce.sps.projetoa.domain.model.WhatsAppEnvio;
import br.gov.ce.sps.projetoa.domain.service.whatsapp.WhatsAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/whatsapp/envios")
@RequiredArgsConstructor
public class WhatsAppEnvioController {

    private final WhatsAppService whatsAppService;
    private final WhatsAppEnvioAssembler whatsAppEnvioAssembler;

    @PreAuthorize("hasAuthority('" + Permissoes.WhatsApp.LISTAR + "')")
    @GetMapping
    public Page<WhatsAppEnvioModel> listar(
            WhatsAppEnvioFilter filtro,
            @PageableDefault(size = 5, sort = "dataSolicitacao", direction = Sort.Direction.DESC) Pageable pageable) {
        return toPage(whatsAppService.listar(filtro, pageable), pageable);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.WhatsApp.LISTAR + "')")
    @GetMapping("/erros")
    public Page<WhatsAppEnvioModel> listarErros(
            WhatsAppEnvioFilter filtro,
            @PageableDefault(size = 5, sort = "dataSolicitacao", direction = Sort.Direction.DESC) Pageable pageable) {
        return toPage(whatsAppService.listarErros(filtro, pageable), pageable);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.WhatsApp.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public WhatsAppEnvioModel buscar(@PathVariable UUID codigo) {
        return whatsAppEnvioAssembler.toModel(whatsAppService.buscar(codigo));
    }

    @PreAuthorize("hasAuthority('" + Permissoes.WhatsApp.REENVIAR + "')")
    @PostMapping("/reenviar-pendentes")
    public Map<String, Integer> reenviarPendentesEErros() {
        int reenviados = whatsAppService.reenviarPendentesEErros();
        return Map.of("reenviados", reenviados);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.WhatsApp.REENVIAR + "')")
    @PostMapping("/{codigo}/reenviar")
    public WhatsAppEnvioModel reenviar(@PathVariable UUID codigo) {
        return whatsAppEnvioAssembler.toModel(whatsAppService.reenviar(codigo));
    }

    @PreAuthorize("hasAuthority('" + Permissoes.WhatsApp.ENVIAR + "')")
    @PostMapping("/lembretes")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, Integer> lembretes(@Valid @RequestBody WhatsAppLembreteInput input) {
        int enfileirados = whatsAppService.enfileirarLembretes(input.ano(), input.mes());
        return Map.of("enfileirados", enfileirados);
    }

    private Page<WhatsAppEnvioModel> toPage(Page<WhatsAppEnvio> page, Pageable pageable) {
        List<WhatsAppEnvioModel> dto = whatsAppEnvioAssembler.toCollectionModel(page.getContent());
        return new PageImpl<>(dto, page.getPageable() == null ? pageable : page.getPageable(), page.getTotalElements());
    }
}
