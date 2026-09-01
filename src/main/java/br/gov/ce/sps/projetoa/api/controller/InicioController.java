package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.dto.DashboardCoordenadorModel;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.service.inicio.InicioDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inicio")
@RequiredArgsConstructor
public class InicioController {

    private final InicioDashboardService inicioDashboardService;

    @PreAuthorize("hasAuthority('" + Permissoes.Inicio.LISTAR + "')")
    @GetMapping("/dashboard")
    public DashboardCoordenadorModel dashboard() {
        return inicioDashboardService.montar();
    }
}
