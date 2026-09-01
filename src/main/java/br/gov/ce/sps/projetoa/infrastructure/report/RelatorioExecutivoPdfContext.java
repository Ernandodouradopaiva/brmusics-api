package br.gov.ce.sps.projetoa.infrastructure.report;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * Layout institucional compartilhado dos relatórios PDF executivos SPS
 * (faixa verde, logo, badge RELATÓRIO EXECUTIVO, meta row, cards KPI, rodapé paginado).
 *
 */
public final class RelatorioExecutivoPdfContext {

    public static final DateTimeFormatter DATA_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"));

    public record MetaItem(RelatorioPdfIcons.Tipo icon, String label, String value) {}

    public record SummaryCardSpec(RelatorioPdfIcons.Tipo icon, String label, String value, String suffix) {}

    private final PDDocument document;
    private final RelatorioPdfFonts fonts;
    private final RelatorioPdfFontAwesome fontAwesome;
    private final RelatorioPdfImagens imagens;
    private PDPage page;
    private PDPageContentStream cs;
    private float y;
    private float pageWidth;
    private float pageHeight;

    public RelatorioExecutivoPdfContext(
            PDDocument document,
            RelatorioPdfFonts fonts,
            RelatorioPdfFontAwesome fontAwesome,
            RelatorioPdfImagens imagens)
            throws IOException {
        this.document = document;
        this.fonts = fonts;
        this.fontAwesome = fontAwesome;
        this.imagens = imagens;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getContentWidth() {
        return pageWidth - 2 * RelatorioPdfTheme.MARGIN;
    }

    public float getPageWidth() {
        return pageWidth;
    }

    public PDPageContentStream cs() {
        return cs;
    }

    public RelatorioPdfFonts fonts() {
        return fonts;
    }

    public RelatorioPdfFontAwesome fontAwesome() {
        return fontAwesome;
    }

    public void newPage() throws IOException {
        if (cs != null) {
            cs.close();
        }
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        cs = new PDPageContentStream(document, page);
        pageWidth = page.getMediaBox().getWidth();
        pageHeight = page.getMediaBox().getHeight();
        y = pageHeight - RelatorioPdfTheme.MARGIN - RelatorioPdfTheme.RUNNING_HEADER_HEIGHT;
        RelatorioPdfDrawing.drawRunningHeader(cs, fonts, pageWidth, pageHeight);
    }

    public void drawTitleBlock(String titulo, String subtitulo) throws IOException {
        float top = y;
        float bandLeft = RelatorioPdfTheme.MARGIN;
        float bandRight = pageWidth - RelatorioPdfTheme.MARGIN;
        float inset = RelatorioPdfTheme.HEADER_BAND_LOGO_INSET;
        float bandTop = top + RelatorioPdfTheme.HEADER_BAND_PAD_TOP;

        float logoH = RelatorioPdfTheme.LOGO_MAX_HEIGHT;
        float logoY = bandTop - inset - logoH;

        float leftLogoW = imagens.widthForHeight(imagens.logoSistema(), logoH);
        float leftLogoX = bandLeft + inset;

        float textX = leftLogoX + leftLogoW + 14f;
        float textW = bandRight - inset - textX;

        float descY = top - 48f;
        if (subtitulo != null && !subtitulo.isBlank()) {
            List<String> lines = RelatorioPdfDrawing.wrap(subtitulo, fonts.regular(), 9f, textW);
            for (String ignored : lines) {
                descY -= 12f;
            }
        }

        float bandBottom = Math.min(logoY, descY) - RelatorioPdfTheme.HEADER_BAND_PAD_BOTTOM;
        float bandWidth = bandRight - bandLeft;
        RelatorioPdfDrawing.fillRect(
                cs, bandLeft, bandBottom, bandWidth, bandTop - bandBottom, RelatorioPdfTheme.FUNDO_TABELA_HEADER);

        imagens.draw(cs, imagens.logoSistema(), leftLogoX, logoY, leftLogoW, logoH);

        RelatorioPdfDrawing.drawText(
                cs,
                fonts,
                RelatorioPdfTheme.BADGE_EXECUTIVO,
                true,
                8.5f,
                textX,
                top - 14f,
                RelatorioPdfTheme.TEXTO_HEADER_FAIXA);

        String tituloFmt = titulo != null ? titulo.toUpperCase(Locale.forLanguageTag("pt-BR")) : "RELATÓRIO";
        RelatorioPdfDrawing.drawTextFit(
                cs, fonts, tituloFmt, true, 15f, textX, top - 32f, textW, RelatorioPdfTheme.TEXTO_HEADER_FAIXA);

        RelatorioPdfDrawing.drawYellowAccent(cs, textX, top - 38f, 52f);

        descY = top - 48f;
        if (subtitulo != null && !subtitulo.isBlank()) {
            List<String> lines = RelatorioPdfDrawing.wrap(subtitulo, fonts.regular(), 9f, textW);
            for (String line : lines) {
                RelatorioPdfDrawing.drawText(
                        cs,
                        fonts,
                        line,
                        false,
                        9f,
                        textX,
                        descY,
                        RelatorioPdfTheme.TEXTO_HEADER_FAIXA_SECUNDARIO);
                descY -= 12f;
            }
        }

        y = bandBottom - 14f;
    }

    public void drawMetaRow(MetaItem... items) throws IOException {
        ensureSpace(52f);
        RelatorioPdfDrawing.drawHorizontalRule(cs, RelatorioPdfTheme.MARGIN, y, pageWidth - 2 * RelatorioPdfTheme.MARGIN);
        y -= 18f;

        int n = Math.max(1, items.length);
        float gap = 8f;
        float colW = (pageWidth - 2 * RelatorioPdfTheme.MARGIN - gap * (n - 1)) / n;
        for (int i = 0; i < items.length; i++) {
            MetaItem item = items[i];
            RelatorioPdfDrawing.drawMetaItem(
                    cs,
                    fonts,
                    fontAwesome,
                    RelatorioPdfTheme.MARGIN + (colW + gap) * i,
                    y,
                    colW,
                    item.icon(),
                    item.label(),
                    item.value());
        }
        y -= 36f;
    }

    /**
     * Meta em largura total da página — ideal para nomes longos (ex.: unidade/caminhão).
     */
    public void drawMetaFullWidth(RelatorioPdfIcons.Tipo icon, String label, String value) throws IOException {
        if (value == null || value.isBlank()) {
            return;
        }
        ensureSpace(56f);
        float usable = pageWidth - 2 * RelatorioPdfTheme.MARGIN;
        float used = RelatorioPdfDrawing.drawMetaFullWidth(
                cs, fonts, fontAwesome, RelatorioPdfTheme.MARGIN, y, usable, icon, label, value.trim());
        y -= Math.max(40f, used);
    }

    public void drawSummaryCardsGrid(SummaryCardSpec[][] rows) throws IOException {
        float rowGap = RelatorioPdfTheme.CARD_ROW_GAP;
        float blockHeight = RelatorioPdfTheme.CARD_HEIGHT * rows.length + rowGap * (rows.length - 1);
        ensureSpace(blockHeight + RelatorioPdfTheme.CARDS_BLOCK_MARGIN);

        float usable = pageWidth - 2 * RelatorioPdfTheme.MARGIN;
        int cols = rows[0].length;
        float cardW = (usable - RelatorioPdfTheme.CARD_GAP * (cols - 1)) / cols;

        float rowTop = y;
        for (SummaryCardSpec[] row : rows) {
            float rowBottom = rowTop - RelatorioPdfTheme.CARD_HEIGHT;
            float x = RelatorioPdfTheme.MARGIN;
            for (SummaryCardSpec spec : row) {
                RelatorioPdfDrawing.drawSummaryCard(
                        cs,
                        fonts,
                        fontAwesome,
                        x,
                        rowBottom,
                        cardW,
                        RelatorioPdfTheme.CARD_HEIGHT,
                        spec.icon(),
                        spec.label(),
                        spec.value(),
                        spec.suffix());
                x += cardW + RelatorioPdfTheme.CARD_GAP;
            }
            rowTop = rowBottom - rowGap;
        }
        y = rowTop - RelatorioPdfTheme.CARDS_BLOCK_MARGIN;
    }

    public void drawObservacoes(String... notas) throws IOException {
        ensureSpace(48f + RelatorioPdfTheme.OBSERVACOES_TOP_MARGIN);
        y -= RelatorioPdfTheme.OBSERVACOES_TOP_MARGIN;
        float obsIconY = y - 6f;
        RelatorioPdfIcons.drawFilled(
                cs,
                fontAwesome,
                RelatorioPdfIcons.Tipo.OBSERVACOES,
                RelatorioPdfTheme.MARGIN + 8f,
                obsIconY,
                11f,
                RelatorioPdfTheme.VERDE_SPS);
        RelatorioPdfDrawing.drawText(
                cs, fonts, "Observações:", true, 8.5f, RelatorioPdfTheme.MARGIN + 22f, y - 4f, RelatorioPdfTheme.TEXTO);
        y -= 14f;
        for (String nota : notas) {
            List<String> linhas =
                    RelatorioPdfDrawing.wrap(nota, fonts.regular(), 8f, pageWidth - 2 * RelatorioPdfTheme.MARGIN);
            for (String linha : linhas) {
                ensureSpace(11f);
                RelatorioPdfDrawing.drawText(
                        cs, fonts, linha, false, 8f, RelatorioPdfTheme.MARGIN, y, RelatorioPdfTheme.TEXTO_LABEL);
                y -= 11f;
            }
        }
    }

    public void ensureSpace(float needed) throws IOException {
        float minY = RelatorioPdfTheme.MARGIN + RelatorioPdfTheme.PAGE_FOOTER_RESERVED;
        if (y - needed < minY) {
            newPage();
        }
    }

    public void finalizeDocument() throws IOException {
        closeStream();
        int total = document.getNumberOfPages();
        for (int i = 0; i < total; i++) {
            PDPage p = document.getPage(i);
            try (PDPageContentStream footerCs =
                    new PDPageContentStream(document, p, PDPageContentStream.AppendMode.APPEND, true, true)) {
                RelatorioPdfDrawing.drawPageFooter(footerCs, fonts, p.getMediaBox().getWidth(), i + 1, total);
            }
        }
    }

    public void closeStream() throws IOException {
        if (cs != null) {
            cs.close();
            cs = null;
        }
    }

    public static String formatNum(long n) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%,d", n);
    }
}
