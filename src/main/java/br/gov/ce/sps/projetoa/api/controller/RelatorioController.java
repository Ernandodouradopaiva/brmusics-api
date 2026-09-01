package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.filter.UsuarioFilter;
import br.gov.ce.sps.projetoa.domain.service.relatorio.RelatorioUsuariosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioUsuariosService relatorioUsuariosService;

    @PreAuthorize("hasAuthority('" + Permissoes.RelatorioUsuarios.GERAR + "')")
    @GetMapping("/usuarios.pdf")
    public ResponseEntity<byte[]> usuariosPdf(UsuarioFilter filtro) {
        byte[] pdf = relatorioUsuariosService.gerarPdf(filtro);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"relatorio-usuarios.pdf\"");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers).body(pdf);
    }
}
