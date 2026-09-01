package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.dto.MinhaEscalaAgendaModel;
import br.gov.ce.sps.projetoa.api.dto.MinhaEscalaItemModel;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.service.escala.MinhaEscalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/minha-escala")
@RequiredArgsConstructor
public class MinhaEscalaController {

    private final MinhaEscalaService minhaEscalaService;

    @PreAuthorize("hasAuthority('" + Permissoes.MinhaEscala.LISTAR + "')")
    @GetMapping
    public MinhaEscalaAgendaModel agenda() {
        return minhaEscalaService.agenda();
    }

    @PreAuthorize("hasAuthority('" + Permissoes.MinhaEscala.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public MinhaEscalaItemModel buscar(@PathVariable UUID codigo) {
        return minhaEscalaService.buscar(codigo);
    }
}
