package br.gov.ce.sps.projetoa.infrastructure.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Component;

import br.gov.ce.sps.projetoa.infrastructure.service.report.ReportExcepetion;

/**
 * Gerador genérico de listagens PDF no padrão executivo BRMusics.
 */
@Component
public class RelatorioListagemPdfGenerator {

    private static final float ROW_HEIGHT = 15f;
    private static final float TABLE_HEADER_HEIGHT = 20f;

    public record Coluna(String rotulo, float largura) {}

    public record Pedido(
            String titulo,
            String subtitulo,
            String tipo,
            RelatorioExecutivoPdfContext.SummaryCardSpec[][] cards,
            Coluna[] colunas,
            List<String[]> linhas,
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
                            RelatorioPdfIcons.Tipo.RELATORIO_EXECUTIVO, "TIPO", pedido.tipo()));

            if (pedido.cards() != null && pedido.cards().length > 0) {
                ctx.drawSummaryCardsGrid(pedido.cards());
            }

            drawTableHeader(ctx, pedido.colunas());
            int rowIndex = 0;
            for (String[] linha : pedido.linhas()) {
                ensureSpaceForRow(ctx, pedido.colunas());
                drawDataRow(ctx, pedido.colunas(), linha, rowIndex % 2 == 1);
                rowIndex++;
            }

            if (pedido.observacoes() != null && pedido.observacoes().length > 0) {
                ctx.drawObservacoes(pedido.observacoes());
            }

            ctx.finalizeDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ReportExcepetion("Não foi possível gerar o PDF do relatório", e);
        }
    }

    private static void drawTableHeader(RelatorioExecutivoPdfContext ctx, Coluna[] colunas) throws IOException {
        ctx.ensureSpace(TABLE_HEADER_HEIGHT + 4f);
        PDPageContentStream cs = ctx.cs();
        float y = ctx.getY();
        float pageWidth = ctx.getPageWidth();
        float bottom = y - TABLE_HEADER_HEIGHT;
        RelatorioPdfDrawing.setFill(cs, RelatorioPdfTheme.FUNDO_TABELA_HEADER);
        cs.addRect(RelatorioPdfTheme.MARGIN, bottom, pageWidth - 2 * RelatorioPdfTheme.MARGIN, TABLE_HEADER_HEIGHT);
        cs.fill();
        float x = RelatorioPdfTheme.MARGIN;
        for (Coluna coluna : colunas) {
            RelatorioPdfDrawing.drawText(
                    cs, ctx.fonts(), coluna.rotulo(), true, 8f, x + 5f, y - 13f, java.awt.Color.WHITE);
            x += coluna.largura();
        }
        ctx.setY(bottom - 2f);
    }

    private static void ensureSpaceForRow(RelatorioExecutivoPdfContext ctx, Coluna[] colunas) throws IOException {
        float minY = RelatorioPdfTheme.MARGIN + RelatorioPdfTheme.PAGE_FOOTER_RESERVED;
        if (ctx.getY() - ROW_HEIGHT < minY) {
            ctx.newPage();
            drawTableHeader(ctx, colunas);
        } else {
            ctx.ensureSpace(ROW_HEIGHT);
        }
    }

    private static void drawDataRow(
            RelatorioExecutivoPdfContext ctx, Coluna[] colunas, String[] vals, boolean alt) throws IOException {
        PDPageContentStream cs = ctx.cs();
        float y = ctx.getY();
        float pageWidth = ctx.getPageWidth();
        float bottom = y - ROW_HEIGHT;
        if (alt) {
            RelatorioPdfDrawing.setFill(cs, RelatorioPdfTheme.FUNDO_LINHA_ALT);
            cs.addRect(RelatorioPdfTheme.MARGIN, bottom, pageWidth - 2 * RelatorioPdfTheme.MARGIN, ROW_HEIGHT);
            cs.fill();
        }
        float x = RelatorioPdfTheme.MARGIN;
        float baseline = y - 11f;
        for (int i = 0; i < colunas.length; i++) {
            String valor = i < vals.length ? nvl(vals[i]) : "—";
            RelatorioPdfDrawing.drawTextFit(
                    cs,
                    ctx.fonts(),
                    valor,
                    false,
                    8f,
                    x + 4f,
                    baseline,
                    colunas[i].largura() - 6f,
                    RelatorioPdfTheme.TEXTO);
            x += colunas[i].largura();
        }
        ctx.setY(bottom);
    }

    private static String nvl(String s) {
        return s == null || s.isBlank() ? "—" : s;
    }
}
