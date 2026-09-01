package br.gov.ce.sps.projetoa.infrastructure.report;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

final class RelatorioPdfDrawing {

    private RelatorioPdfDrawing() {}

    static void setFill(PDPageContentStream cs, Color color) throws IOException {
        cs.setNonStrokingColor(color);
    }

    static void setStroke(PDPageContentStream cs, Color color) throws IOException {
        cs.setStrokingColor(color);
    }

    static void fillRect(PDPageContentStream cs, float x, float bottom, float width, float height, Color fill)
            throws IOException {
        setFill(cs, fill);
        cs.addRect(x, bottom, width, height);
        cs.fill();
    }

    static void drawText(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            String text,
            boolean bold,
            float size,
            float x,
            float baseline,
            Color color)
            throws IOException {
        if (text == null || text.isBlank()) {
            return;
        }
        PDFont font = bold ? fonts.bold() : fonts.regular();
        setFill(cs, color);
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, baseline);
        cs.showText(sanitize(text));
        cs.endText();
    }

    static void drawTextFit(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            String text,
            boolean bold,
            float size,
            float x,
            float baseline,
            float maxWidth,
            Color color)
            throws IOException {
        drawText(cs, fonts, truncate(text, bold ? fonts.bold() : fonts.regular(), size, maxWidth), bold, size, x, baseline, color);
    }

    /** Reduz o tamanho da fonte até o texto caber; se ainda não couber no mínimo, trunca com reticências. */
    static void drawTextFitScaled(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            String text,
            boolean bold,
            float maxSize,
            float minSize,
            float x,
            float baseline,
            float maxWidth,
            Color color)
            throws IOException {
        if (text == null || text.isBlank()) {
            return;
        }
        PDFont font = bold ? fonts.bold() : fonts.regular();
        String safe = sanitize(text);
        float size = maxSize;
        while (size > minSize && font.getStringWidth(safe) / 1000f * size > maxWidth) {
            size -= 0.25f;
        }
        if (font.getStringWidth(safe) / 1000f * size > maxWidth) {
            safe = truncate(safe, font, size, maxWidth);
        }
        drawText(cs, fonts, safe, bold, size, x, baseline, color);
    }

    static List<String> wrap(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = sanitize(text).split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            float w = font.getStringWidth(candidate) / 1000f * fontSize;
            if (w > maxWidth && !line.isEmpty()) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (!line.isEmpty()) {
            lines.add(line.toString());
        }
        return lines;
    }

    static String truncate(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        if (text == null) {
            return "";
        }
        String t = sanitize(text);
        if (font.getStringWidth(t) / 1000f * fontSize <= maxWidth) {
            return t;
        }
        String ellipsis = "...";
        for (int i = t.length() - 1; i > 0; i--) {
            String sub = t.substring(0, i) + ellipsis;
            if (font.getStringWidth(sub) / 1000f * fontSize <= maxWidth) {
                return sub;
            }
        }
        return ellipsis;
    }

    static void drawIconBox(PDPageContentStream cs, float x, float y, float size, Color fill, Color border)
            throws IOException {
        setFill(cs, fill);
        cs.addRect(x, y, size, size);
        cs.fill();
        setStroke(cs, border);
        cs.setLineWidth(0.6f);
        cs.addRect(x, y, size, size);
        cs.stroke();
    }

    static void drawCircle(PDPageContentStream cs, float cx, float cy, float diameter, Color fill, Color border)
            throws IOException {
        float r = diameter / 2f;
        setFill(cs, fill);
        cs.moveTo(cx + r, cy);
        for (int i = 1; i <= 24; i++) {
            double a = 2 * Math.PI * i / 24;
            cs.lineTo((float) (cx + r * Math.cos(a)), (float) (cy + r * Math.sin(a)));
        }
        cs.closePath();
        cs.fill();
        setStroke(cs, border);
        cs.setLineWidth(0.5f);
        cs.moveTo(cx + r, cy);
        for (int i = 1; i <= 24; i++) {
            double a = 2 * Math.PI * i / 24;
            cs.lineTo((float) (cx + r * Math.cos(a)), (float) (cy + r * Math.sin(a)));
        }
        cs.closePath();
        cs.stroke();
    }

    static void drawHorizontalRule(PDPageContentStream cs, float x, float y, float width) throws IOException {
        setStroke(cs, RelatorioPdfTheme.BORDA);
        cs.setLineWidth(0.75f);
        cs.moveTo(x, y);
        cs.lineTo(x + width, y);
        cs.stroke();
    }

    static void drawYellowAccent(PDPageContentStream cs, float x, float y, float width) throws IOException {
        setFill(cs, RelatorioPdfTheme.AMARELO_ACCENT);
        cs.addRect(x, y, width, 3f);
        cs.fill();
    }

    /**
     * Card resumo: borda verde no topo, faixa menta à esquerda com ícone, dados à direita.
     */
    static void drawSummaryCard(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            RelatorioPdfFontAwesome icons,
            float x,
            float bottom,
            float width,
            float height,
            RelatorioPdfIcons.Tipo icon,
            String label,
            String value,
            String suffix)
            throws IOException {
        float top = bottom + height;
        setFill(cs, RelatorioPdfTheme.VERDE_SPS);
        cs.addRect(x, top - RelatorioPdfTheme.CARD_TOP_BORDER, width, RelatorioPdfTheme.CARD_TOP_BORDER);
        cs.fill();

        float iconW = width * RelatorioPdfTheme.CARD_ICON_RATIO;
        setFill(cs, RelatorioPdfTheme.VERDE_MENTA);
        cs.addRect(x, bottom, iconW, height - RelatorioPdfTheme.CARD_TOP_BORDER);
        cs.fill();

        setFill(cs, RelatorioPdfTheme.FUNDO_CARD);
        cs.addRect(x + iconW, bottom, width - iconW, height - RelatorioPdfTheme.CARD_TOP_BORDER);
        cs.fill();

        setStroke(cs, RelatorioPdfTheme.BORDA);
        cs.setLineWidth(0.75f);
        cs.addRect(x, bottom, width, height);
        cs.stroke();

        float contentH = height - RelatorioPdfTheme.CARD_TOP_BORDER;
        float iconCx = x + iconW / 2f;
        float iconCy = bottom + contentH / 2f;
        float iconSize = Math.min(iconW * 0.68f, contentH * 0.48f);
        RelatorioPdfIcons.drawFilled(cs, icons, icon, iconCx, iconCy, iconSize, RelatorioPdfTheme.VERDE_SPS);

        float padX = 7f;
        float textX = x + iconW + padX;
        float textW = width - iconW - padX - 6f;
        float contentTop = top - RelatorioPdfTheme.CARD_TOP_BORDER;
        float labelY = contentTop - 10f;
        float valueY = bottom + contentH * 0.36f;
        float suffixY = bottom + 9f;

        drawTextFitScaled(
                cs,
                fonts,
                label,
                true,
                RelatorioPdfTheme.CARD_LABEL_FONT,
                5.5f,
                textX,
                labelY,
                textW,
                RelatorioPdfTheme.TEXTO_LABEL);
        drawTextFitScaled(
                cs,
                fonts,
                value,
                true,
                RelatorioPdfTheme.CARD_VALUE_FONT_MAX,
                RelatorioPdfTheme.CARD_VALUE_FONT_MIN,
                textX,
                valueY,
                textW,
                RelatorioPdfTheme.VERDE_SPS);
        if (suffix != null && !suffix.isBlank()) {
            drawText(cs, fonts, suffix, false, RelatorioPdfTheme.CARD_SUFFIX_FONT, textX, suffixY, RelatorioPdfTheme.TEXTO_LABEL);
        }
    }

    static void drawMetaItem(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            RelatorioPdfFontAwesome icons,
            float x,
            float baseline,
            float maxWidth,
            RelatorioPdfIcons.Tipo icon,
            String label,
            String value)
            throws IOException {
        float circleD = RelatorioPdfTheme.ICON_CIRCLE;
        float cy = baseline - circleD / 2f + 2f;
        drawCircle(cs, x + circleD / 2f, cy, circleD, RelatorioPdfTheme.VERDE_MENTA, RelatorioPdfTheme.BORDA);
        RelatorioPdfIcons.drawFilled(cs, icons, icon, x + circleD / 2f, cy, 18f, RelatorioPdfTheme.VERDE_SPS);
        float textX = x + circleD + 12f;
        float textMaxW = Math.max(12f, maxWidth - (circleD + 12f) - 4f);
        drawTextFit(cs, fonts, label, true, 7f, textX, baseline + 2f, textMaxW, RelatorioPdfTheme.TEXTO_LABEL);
        drawTextFitScaled(
                cs,
                fonts,
                value,
                true,
                11f,
                7.5f,
                textX,
                baseline - 12f,
                textMaxW,
                RelatorioPdfTheme.TEXTO_VALOR);
    }

    /**
     * Faixa meta em largura total (rótulo + valor com quebra de linha e fonte reduzida),
     * usada para textos longos como nome de unidade/caminhão.
     *
     * @return altura consumida a partir do {@code topBaseline} (para o caller ajustar {@code y})
     */
    static float drawMetaFullWidth(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            RelatorioPdfFontAwesome icons,
            float x,
            float topBaseline,
            float maxWidth,
            RelatorioPdfIcons.Tipo icon,
            String label,
            String value)
            throws IOException {
        float circleD = RelatorioPdfTheme.ICON_CIRCLE;
        float cy = topBaseline - circleD / 2f + 2f;
        drawCircle(cs, x + circleD / 2f, cy, circleD, RelatorioPdfTheme.VERDE_MENTA, RelatorioPdfTheme.BORDA);
        RelatorioPdfIcons.drawFilled(cs, icons, icon, x + circleD / 2f, cy, 18f, RelatorioPdfTheme.VERDE_SPS);
        float textX = x + circleD + 12f;
        float textMaxW = Math.max(40f, maxWidth - (circleD + 12f) - 4f);
        drawText(cs, fonts, label, true, 7f, textX, topBaseline + 2f, RelatorioPdfTheme.TEXTO_LABEL);

        float valueSize = 9.5f;
        float lineH = 12f;
        List<String> lines = wrap(value != null ? value : "", fonts.bold(), valueSize, textMaxW);
        // Se muitas linhas, reduz fonte e recalcula.
        if (lines.size() > 2) {
            valueSize = 8f;
            lineH = 10.5f;
            lines = wrap(value != null ? value : "", fonts.bold(), valueSize, textMaxW);
        }
        float valueY = topBaseline - 12f;
        int drawn = 0;
        for (String line : lines) {
            if (drawn >= 3) {
                break;
            }
            drawText(cs, fonts, line, true, valueSize, textX, valueY, RelatorioPdfTheme.TEXTO_VALOR);
            valueY -= lineH;
            drawn++;
        }
        float bottom = Math.min(cy - circleD / 2f, valueY + lineH - 2f);
        return topBaseline - bottom + 6f;
    }

    static void drawRunningHeader(PDPageContentStream cs, RelatorioPdfFonts fonts, float pageWidth, float pageHeight)
            throws IOException {
        float y = pageHeight - RelatorioPdfTheme.MARGIN + 4f;
        drawText(cs, fonts, RelatorioPdfTheme.INSTITUCIONAL, false, 7.5f, RelatorioPdfTheme.MARGIN, y, RelatorioPdfTheme.TEXTO_LABEL);
        setStroke(cs, RelatorioPdfTheme.BORDA);
        cs.setLineWidth(0.5f);
        float lineY = pageHeight - RelatorioPdfTheme.MARGIN - RelatorioPdfTheme.RUNNING_HEADER_HEIGHT + 6f;
        cs.moveTo(RelatorioPdfTheme.MARGIN, lineY);
        cs.lineTo(pageWidth - RelatorioPdfTheme.MARGIN, lineY);
        cs.stroke();
    }

    static void drawPageFooter(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            float pageWidth,
            int pageNumber,
            int totalPages)
            throws IOException {
        String pagina = "Página " + pageNumber + " de " + totalPages;
        float textWidth = fonts.regular().getStringWidth(pagina) / 1000f * 7.5f;
        float y = RelatorioPdfTheme.MARGIN - 14f;
        drawText(cs, fonts, RelatorioPdfTheme.INSTITUCIONAL, false, 7.5f, RelatorioPdfTheme.MARGIN, y, RelatorioPdfTheme.TEXTO_LABEL);
        drawText(
                cs,
                fonts,
                pagina,
                false,
                7.5f,
                pageWidth - RelatorioPdfTheme.MARGIN - textWidth,
                y,
                RelatorioPdfTheme.TEXTO_LABEL);
    }

    private static String sanitize(String s) {
        return s.replace('\n', ' ').replace('\r', ' ');
    }
}
