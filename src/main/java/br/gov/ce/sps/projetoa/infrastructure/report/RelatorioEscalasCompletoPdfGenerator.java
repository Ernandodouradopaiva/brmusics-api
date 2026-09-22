package br.gov.ce.sps.projetoa.infrastructure.report;

import br.gov.ce.sps.projetoa.infrastructure.service.report.ReportExcepetion;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Relatório de escalas do período com equipe e repertório por celebração.
 */
@Component
public class RelatorioEscalasCompletoPdfGenerator {

    private static final float SECTION_GAP = 10f;
    private static final float LINE_H = 12f;
    private static final float SUBTITLE_H = 14f;

    public record BlocoEscala(
            String cabecalho,
            String status,
            List<String> equipe,
            List<String> repertorio) {}

    public record Pedido(
            String titulo,
            String subtitulo,
            RelatorioExecutivoPdfContext.SummaryCardSpec[][] cards,
            List<BlocoEscala> blocos,
            String... observacoes) {}

    public byte[] gerar(Pedido pedido) {
        try (PDDocument document = new PDDocument()) {
            RelatorioPdfFonts fonts = RelatorioPdfFonts.load(document);
            RelatorioPdfFontAwesome fontAwesome = RelatorioPdfFontAwesome.load(document);
            RelatorioPdfImagens imagens = RelatorioPdfImagens.load(document);
            RelatorioExecutivoPdfContext ctx =
                    new RelatorioExecutivoPdfContext(document, fonts, fontAwesome, imagens);
            ctx.newPage();

            ctx.drawTitleBlock(pedido.titulo(), pedido.subtitulo());
            String dataEmissao = RelatorioExecutivoPdfContext.DATA_FMT.format(LocalDate.now());
            ctx.drawMetaRow(
                    new RelatorioExecutivoPdfContext.MetaItem(
                            RelatorioPdfIcons.Tipo.CALENDARIO, "DATA DE EMISSÃO", dataEmissao),
                    new RelatorioExecutivoPdfContext.MetaItem(
                            RelatorioPdfIcons.Tipo.SISTEMA, "SISTEMA", "BRMusics"),
                    new RelatorioExecutivoPdfContext.MetaItem(
                            RelatorioPdfIcons.Tipo.RELATORIO_EXECUTIVO, "TIPO", "Escalas completas"));

            if (pedido.cards() != null && pedido.cards().length > 0) {
                ctx.drawSummaryCardsGrid(pedido.cards());
            }

            if (pedido.blocos() == null || pedido.blocos().isEmpty()) {
                ctx.ensureSpace(20f);
                RelatorioPdfDrawing.drawText(
                        ctx.cs(),
                        ctx.fonts(),
                        "Nenhuma escala encontrada no período.",
                        false,
                        9f,
                        RelatorioPdfTheme.MARGIN,
                        ctx.getY(),
                        RelatorioPdfTheme.TEXTO_LABEL);
                ctx.setY(ctx.getY() - 16f);
            } else {
                for (BlocoEscala bloco : pedido.blocos()) {
                    drawBloco(ctx, bloco);
                }
            }

            if (pedido.observacoes() != null && pedido.observacoes().length > 0) {
                ctx.drawObservacoes(pedido.observacoes());
            }

            ctx.finalizeDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ReportExcepetion("Não foi possível gerar o PDF do relatório de escalas", e);
        }
    }

    private static void drawBloco(RelatorioExecutivoPdfContext ctx, BlocoEscala bloco) throws IOException {
        float contentW = ctx.getContentWidth();
        List<String> equipe = bloco.equipe() == null || bloco.equipe().isEmpty()
                ? List.of("Sem músicos escalados.")
                : bloco.equipe();
        List<String> repertorio = bloco.repertorio() == null || bloco.repertorio().isEmpty()
                ? List.of("Sem repertório montado.")
                : bloco.repertorio();

        float estimado = 28f + SUBTITLE_H * 2 + LINE_H * (equipe.size() + repertorio.size()) + SECTION_GAP + 8f;
        ctx.ensureSpace(Math.min(estimado, 120f));

        float y = ctx.getY();
        PDPageContentStream cs = ctx.cs();
        float bandH = 22f;
        RelatorioPdfDrawing.setFill(cs, RelatorioPdfTheme.FUNDO_TABELA_HEADER);
        cs.addRect(RelatorioPdfTheme.MARGIN, y - bandH, contentW, bandH);
        cs.fill();

        String titulo = bloco.cabecalho() == null ? "Escala" : bloco.cabecalho();
        String status = bloco.status() == null || bloco.status().isBlank() ? "" : "  ·  " + bloco.status();
        RelatorioPdfDrawing.drawTextFit(
                cs,
                ctx.fonts(),
                titulo + status,
                true,
                9f,
                RelatorioPdfTheme.MARGIN + 6f,
                y - 15f,
                contentW - 12f,
                java.awt.Color.WHITE);
        ctx.setY(y - bandH - 6f);

        drawSubtitulo(ctx, "Equipe");
        for (String linha : equipe) {
            drawLinha(ctx, "• " + linha);
        }
        ctx.setY(ctx.getY() - 4f);

        drawSubtitulo(ctx, "Repertório");
        for (String linha : repertorio) {
            drawLinha(ctx, "• " + linha);
        }
        ctx.setY(ctx.getY() - SECTION_GAP);
    }

    private static void drawSubtitulo(RelatorioExecutivoPdfContext ctx, String texto) throws IOException {
        ctx.ensureSpace(SUBTITLE_H + 2f);
        RelatorioPdfDrawing.drawText(
                ctx.cs(),
                ctx.fonts(),
                texto,
                true,
                8.5f,
                RelatorioPdfTheme.MARGIN + 2f,
                ctx.getY(),
                RelatorioPdfTheme.VERDE_SPS);
        ctx.setY(ctx.getY() - SUBTITLE_H);
    }

    private static void drawLinha(RelatorioExecutivoPdfContext ctx, String texto) throws IOException {
        float maxW = ctx.getContentWidth() - 10f;
        List<String> linhas = RelatorioPdfDrawing.wrap(texto, ctx.fonts().regular(), 8.5f, maxW);
        for (String linha : linhas) {
            ctx.ensureSpace(LINE_H);
            RelatorioPdfDrawing.drawText(
                    ctx.cs(),
                    ctx.fonts(),
                    linha,
                    false,
                    8.5f,
                    RelatorioPdfTheme.MARGIN + 8f,
                    ctx.getY(),
                    RelatorioPdfTheme.TEXTO);
            ctx.setY(ctx.getY() - LINE_H);
        }
    }
}
