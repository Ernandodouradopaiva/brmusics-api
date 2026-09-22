package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.dto.FrequenciaMensalPreviaModel;
import br.gov.ce.sps.projetoa.api.input.FrequenciaMensalSalvarInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.service.frequencia.FrequenciaMensalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frequencias")
@RequiredArgsConstructor
public class FrequenciaController {

    private final FrequenciaMensalService frequenciaMensalService;

    @PreAuthorize("hasAuthority('" + Permissoes.Frequencia.LISTAR + "')")
    @GetMapping("/mensal")
    public FrequenciaMensalPreviaModel listarMensal(
            @RequestParam int ano,
            @RequestParam int mes) {
        return frequenciaMensalService.listarMensal(ano, mes);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Frequencia.EDITAR + "')")
    @PutMapping("/mensal")
    public FrequenciaMensalPreviaModel salvarMensal(@Valid @RequestBody FrequenciaMensalSalvarInput input) {
        return frequenciaMensalService.salvarMensal(input);
    }
}
