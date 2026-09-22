package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.filter.UsuarioFilter;
import br.gov.ce.sps.projetoa.domain.service.relatorio.RelatorioDominioService;
import br.gov.ce.sps.projetoa.domain.service.relatorio.RelatorioUsuariosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioUsuariosService relatorioUsuariosService;
    private final RelatorioDominioService relatorioDominioService;

    @PreAuthorize("hasAuthority('" + Permissoes.RelatorioUsuarios.GERAR + "')")
    @GetMapping("/usuarios.pdf")
    public ResponseEntity<byte[]> usuariosPdf(UsuarioFilter filtro) {
        return pdf(relatorioUsuariosService.gerarPdf(filtro), "relatorio-usuarios.pdf");
    }

    @PreAuthorize("hasAuthority('" + Permissoes.RelatorioMusicas.GERAR + "')")
    @GetMapping("/musicas.pdf")
    public ResponseEntity<byte[]> musicasPdf() {
        return pdf(relatorioDominioService.musicasPdf(), "relatorio-musicas.pdf");
    }

    @PreAuthorize("hasAuthority('" + Permissoes.RelatorioMusicos.GERAR + "')")
    @GetMapping("/musicos.pdf")
    public ResponseEntity<byte[]> musicosPdf() {
        return pdf(relatorioDominioService.musicosPdf(), "relatorio-musicos.pdf");
    }

    @PreAuthorize("hasAuthority('" + Permissoes.RelatorioMusicosEscala.GERAR + "')")
    @GetMapping("/musicos-escala.pdf")
    public ResponseEntity<byte[]> musicosEscalaPdf(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {
        return pdf(relatorioDominioService.musicosPorEscalaPdf(ano, mes), "relatorio-musicos-escala.pdf");
    }

    @PreAuthorize("hasAuthority('" + Permissoes.RelatorioEscalas.GERAR + "')")
    @GetMapping("/escalas.pdf")
    public ResponseEntity<byte[]> escalasPdf(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {
        return pdf(relatorioDominioService.escalasPdf(ano, mes), "relatorio-escalas.pdf");
    }

    @PreAuthorize("hasAuthority('" + Permissoes.RelatorioRepertorios.GERAR + "')")
    @GetMapping("/repertorios.pdf")
    public ResponseEntity<byte[]> repertoriosPdf(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {
        return pdf(relatorioDominioService.repertoriosPdf(ano, mes), "relatorio-repertorios.pdf");
    }

    @PreAuthorize("hasAuthority('" + Permissoes.RelatorioCelebracoesMes.GERAR + "')")
    @GetMapping("/celebracoes-mes.pdf")
    public ResponseEntity<byte[]> celebracoesMesPdf(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {
        return pdf(relatorioDominioService.celebracoesPorMesPdf(ano, mes), "relatorio-celebracoes-mes.pdf");
    }

    @PreAuthorize("hasAuthority('" + Permissoes.RelatorioMusicosFuncoes.GERAR + "')")
    @GetMapping("/musicos-funcoes.pdf")
    public ResponseEntity<byte[]> musicosFuncoesPdf() {
        return pdf(relatorioDominioService.musicosFuncoesInstrumentosPdf(), "relatorio-musicos-funcoes.pdf");
    }

    private static ResponseEntity<byte[]> pdf(byte[] body, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers).body(body);
    }
}
