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
 * Relatório executivo de exemplo — listagem de usuários.
 */
@Component
public class RelatorioUsuariosPdfGenerator {

    private static final float[] COL_WIDTHS = {100f, 200f, 130f, 85f};
    private static final String[] COL_LABELS = {"CPF", "Nome", "Cargo", "Status"};
    private static final float ROW_HEIGHT = 15f;
    private static final float TABLE_HEADER_HEIGHT = 20f;

    public record Linha(String cpf, String nome, String cargo, String status) {}

    public record Resumo(String titulo, String subtitulo, long total, long ativos, long inativos, List<Linha> itens) {}

    public byte[] gerar(Resumo resumo) {
        try (PDDocument document = new PDDocument()) {
            RelatorioPdfFonts fonts = RelatorioPdfFonts.load(document);
            RelatorioPdfFontAwesome fontAwesome = RelatorioPdfFontAwesome.load(document);
            RelatorioPdfImagens imagens = RelatorioPdfImagens.load(document);
            RelatorioExecutivoPdfContext ctx =
                    new RelatorioExecutivoPdfContext(document, fonts, fontAwesome, imagens);
            ctx.newPage();

            String titulo = resumo.titulo() != null ? resumo.titulo() : "Relatório de Usuários";
            ctx.drawTitleBlock(titulo, resumo.subtitulo());

            String dataEmissao = RelatorioExecutivoPdfContext.DATA_FMT.format(LocalDate.now());
            ctx.drawMetaRow(
                    new RelatorioExecutivoPdfContext.MetaItem(
                            RelatorioPdfIcons.Tipo.CALENDARIO, "DATA DE EMISSÃO", dataEmissao),
                    new RelatorioExecutivoPdfContext.MetaItem(
                            RelatorioPdfIcons.Tipo.SISTEMA, "SISTEMA", "BRMusics"),
                    new RelatorioExecutivoPdfContext.MetaItem(
                            RelatorioPdfIcons.Tipo.RELATORIO_EXECUTIVO, "TIPO", "Usuários"));

            ctx.drawSummaryCardsGrid(new RelatorioExecutivoPdfContext.SummaryCardSpec[][] {
                {
                    new RelatorioExecutivoPdfContext.SummaryCardSpec(
                            RelatorioPdfIcons.Tipo.TOTAL_USUARIOS,
                            "TOTAL",
                            RelatorioExecutivoPdfContext.formatNum(resumo.total()),
                            "cadastrados"),
                    new RelatorioExecutivoPdfContext.SummaryCardSpec(
                            RelatorioPdfIcons.Tipo.STATUS,
                            "ATIVOS",
                            RelatorioExecutivoPdfContext.formatNum(resumo.ativos()),
                            "usuários"),
                },
                {
                    new RelatorioExecutivoPdfContext.SummaryCardSpec(
                            RelatorioPdfIcons.Tipo.PESSOA,
                            "INATIVOS",
                            RelatorioExecutivoPdfContext.formatNum(resumo.inativos()),
                            "usuários"),
                    new RelatorioExecutivoPdfContext.SummaryCardSpec(
                            RelatorioPdfIcons.Tipo.PRANCHETA,
                            "EMISSÃO",
                            dataEmissao,
                            "PDF executivo"),
                },
            });

            drawTableHeader(ctx);
            int rowIndex = 0;
            for (Linha linha : resumo.itens()) {
                ensureSpaceForRow(ctx);
                drawDataRow(ctx, linha, rowIndex % 2 == 1);
                rowIndex++;
            }

            ctx.drawObservacoes(
                    "1. Listagem conforme cadastro no sistema BRMusics.",
                    "2. Relatório gerado no padrão visual BRMusics (madeira/creme/dourado).",
                    "3. Logo do sistema: /images/logo-sistema.png.");

            ctx.finalizeDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ReportExcepetion("Não foi possível gerar o PDF do relatório de usuários", e);
        }
    }

    private static void drawTableHeader(RelatorioExecutivoPdfContext ctx) throws IOException {
        ctx.ensureSpace(TABLE_HEADER_HEIGHT + 4f);
        PDPageContentStream cs = ctx.cs();
        float y = ctx.getY();
        float pageWidth = ctx.getPageWidth();
        float bottom = y - TABLE_HEADER_HEIGHT;
        RelatorioPdfDrawing.setFill(cs, RelatorioPdfTheme.FUNDO_TABELA_HEADER);
        cs.addRect(RelatorioPdfTheme.MARGIN, bottom, pageWidth - 2 * RelatorioPdfTheme.MARGIN, TABLE_HEADER_HEIGHT);
        cs.fill();
        float x = RelatorioPdfTheme.MARGIN;
        for (int i = 0; i < COL_LABELS.length; i++) {
            RelatorioPdfDrawing.drawText(
                    cs, ctx.fonts(), COL_LABELS[i], true, 8f, x + 5f, y - 13f, java.awt.Color.WHITE);
            x += COL_WIDTHS[i];
        }
        ctx.setY(bottom - 2f);
    }

    private static void ensureSpaceForRow(RelatorioExecutivoPdfContext ctx) throws IOException {
        float minY = RelatorioPdfTheme.MARGIN + RelatorioPdfTheme.PAGE_FOOTER_RESERVED;
        if (ctx.getY() - ROW_HEIGHT < minY) {
            ctx.newPage();
            drawTableHeader(ctx);
        } else {
            ctx.ensureSpace(ROW_HEIGHT);
        }
    }

    private static void drawDataRow(RelatorioExecutivoPdfContext ctx, Linha linha, boolean alt)
            throws IOException {
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
        String[] vals = {
            nvl(linha.cpf()), nvl(linha.nome()), nvl(linha.cargo()), nvl(linha.status())
        };
        for (int i = 0; i < vals.length; i++) {
            RelatorioPdfDrawing.drawTextFit(
                    cs, ctx.fonts(), vals[i], false, 8f, x + 4f, baseline, COL_WIDTHS[i] - 6f, RelatorioPdfTheme.TEXTO);
            x += COL_WIDTHS[i];
        }
        ctx.setY(bottom);
    }

    private static String nvl(String s) {
        return s == null || s.isBlank() ? "—" : s;
    }
}
