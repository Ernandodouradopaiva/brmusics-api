package br.gov.ce.sps.projetoa.infrastructure.report;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/** Cartões de gráfico no PDF — espelha {@code chartCard} dos dashboards (Recharts). */
final class RelatorioPdfChartCards {

    static final Color[] CORES = {
        new Color(37, 99, 235),
        new Color(124, 58, 237),
        new Color(219, 39, 119),
        new Color(234, 88, 12),
        new Color(6, 182, 212),
        new Color(34, 197, 94),
        new Color(234, 179, 8),
        new Color(239, 68, 68),
        new Color(225, 29, 72)
    };

    private RelatorioPdfChartCards() {}

    record Item(String label, long value) {}

    @FunctionalInterface
    interface ChartDrawer {
        void draw(PDPageContentStream cs, float x, float bottom, float width, float height) throws IOException;
    }

    static float drawCard(
            RelatorioExecutivoPdfContext ctx, String title, String subtitle, float chartHeight, ChartDrawer drawer)
            throws IOException {
        float pad = 10f;
        float headerH = subtitle != null && !subtitle.isBlank() ? 36f : 24f;
        float cardH = headerH + chartHeight + pad * 2;
        ctx.ensureSpace(cardH + 8f);
        float width = ctx.getContentWidth();
        float x = RelatorioPdfTheme.MARGIN;
        float top = ctx.getY();
        float bottom = top - cardH;

        PDPageContentStream stream = ctx.cs();
        RelatorioPdfDrawing.fillRect(stream, x, bottom, width, cardH, Color.WHITE);
        RelatorioPdfDrawing.setStroke(stream, RelatorioPdfTheme.BORDA);
        stream.setLineWidth(0.75f);
        stream.addRect(x, bottom, width, cardH);
        stream.stroke();

        RelatorioPdfDrawing.drawText(stream, ctx.fonts(), title, true, 10f, x + pad, top - 14f, RelatorioPdfTheme.TEXTO);
        float contentTop = top - headerH;
        if (subtitle != null && !subtitle.isBlank()) {
            List<String> lines = RelatorioPdfDrawing.wrap(subtitle, ctx.fonts().regular(), 8f, width - pad * 2);
            float sy = top - 26f;
            for (String line : lines) {
                RelatorioPdfDrawing.drawText(
                        ctx.cs(), ctx.fonts(), line, false, 8f, x + pad, sy, RelatorioPdfTheme.TEXTO_LABEL);
                sy -= 10f;
            }
        }

        float chartBottom = bottom + pad;
        drawer.draw(ctx.cs(), x + pad, chartBottom, width - pad * 2, chartHeight);
        ctx.setY(bottom - 8f);
        return ctx.getY();
    }

    static void drawDonut(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            float x,
            float bottom,
            float width,
            float height,
            List<Item> items)
            throws IOException {
        if (items == null || items.isEmpty()) {
            empty(cs, fonts, x, bottom);
            return;
        }
        long total = items.stream().mapToLong(Item::value).sum();
        if (total <= 0) {
            empty(cs, fonts, x, bottom);
            return;
        }
        float cx = x + width * 0.38f;
        float cy = bottom + height * 0.52f;
        float outer = Math.min(width * 0.32f, height * 0.42f);
        float inner = outer * 0.48f;
        float angle = 90f;
        for (int i = 0; i < items.size(); i++) {
            float sweep = 360f * items.get(i).value() / total;
            fillRingSlice(cs, cx, cy, inner, outer, angle, -sweep, CORES[i % CORES.length]);
            angle -= sweep;
        }
        drawLegendWithFonts(cs, fonts, x + width * 0.58f, bottom + height - 6f, width * 0.4f, items);
    }

    static void drawHorizontalBars(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            float x,
            float bottom,
            float width,
            float height,
            List<Item> items,
            int maxItems,
            Color singleColor)
            throws IOException {
        drawHorizontalBars(cs, fonts, x, bottom, width, height, items, maxItems, singleColor, 0.42f, 42);
    }

    static void drawHorizontalBars(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            float x,
            float bottom,
            float width,
            float height,
            List<Item> items,
            int maxItems,
            Color singleColor,
            float labelWidthRatio,
            int labelMaxChars)
            throws IOException {
        if (items == null || items.isEmpty()) {
            empty(cs, fonts, x, bottom);
            return;
        }
        List<Item> slice = items.stream().limit(maxItems).toList();
        long max = slice.stream().mapToLong(Item::value).max().orElse(1L);
        if (max <= 0) {
            max = 1;
        }
        float labelW = width * labelWidthRatio;
        float barAreaW = width - labelW - 44f;
        float rowH = Math.min(16f, height / Math.max(slice.size(), 1));
        float y = bottom + height - rowH;
        for (int i = 0; i < slice.size(); i++) {
            Item item = slice.get(i);
            String label = labelMaxChars > 0 ? truncate(item.label(), labelMaxChars) : item.label();
            RelatorioPdfDrawing.drawTextFit(
                    cs, fonts, label, false, 7f, x, y + 3f, labelW - 4f, RelatorioPdfTheme.TEXTO);
            float barW = barAreaW * (item.value() / (float) max);
            Color cor = singleColor != null ? singleColor : CORES[i % CORES.length];
            RelatorioPdfDrawing.fillRect(cs, x + labelW, y, Math.max(barW, 2f), rowH - 3f, cor);
            RelatorioPdfDrawing.drawText(
                    cs,
                    fonts,
                    RelatorioExecutivoPdfContext.formatNum(item.value()),
                    true,
                    6.5f,
                    x + labelW + barAreaW + 4f,
                    y + 3f,
                    RelatorioPdfTheme.TEXTO);
            y -= rowH;
        }
    }

    static void drawVerticalBars(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            float x,
            float bottom,
            float width,
            float height,
            List<Item> items,
            Color fill)
            throws IOException {
        if (items == null || items.isEmpty()) {
            empty(cs, fonts, x, bottom);
            return;
        }
        long max = items.stream().mapToLong(Item::value).max().orElse(1L);
        if (max <= 0) {
            max = 1;
        }
        float barW = Math.min(22f, (width - 16f) / items.size() - 3f);
        float gap = (width - barW * items.size()) / (items.size() + 1);
        float cursor = x + gap;
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            float barH = (height - 18f) * (item.value() / (float) max);
            Color cor = fill != null ? fill : CORES[i % CORES.length];
            RelatorioPdfDrawing.fillRect(cs, cursor, bottom, barW, barH, cor);
            RelatorioPdfDrawing.drawTextFit(
                    cs, fonts, item.label(), false, 6f, cursor - 2f, bottom - 9f, barW + 8f, RelatorioPdfTheme.TEXTO_LABEL);
            cursor += barW + gap;
        }
    }

    static void drawDualLine(
            PDPageContentStream cs,
            RelatorioPdfFonts fonts,
            float x,
            float bottom,
            float width,
            float height,
            List<Item> series1,
            List<Item> series2,
            Color color1,
            Color color2,
            String legend1,
            String legend2)
            throws IOException {
        if (series1 == null || series1.isEmpty()) {
            empty(cs, fonts, x, bottom);
            return;
        }
        long max = 1;
        for (Item it : series1) {
            max = Math.max(max, it.value());
        }
        if (series2 != null) {
            for (Item it : series2) {
                max = Math.max(max, it.value());
            }
        }
        float plotH = height - 22f;
        float step = width / Math.max(series1.size() - 1, 1);
        drawSeries(cs, x, bottom, step, plotH, series1, max, color1);
        if (series2 != null && !series2.isEmpty()) {
            drawSeries(cs, x, bottom, step, plotH, series2, max, color2);
        }
        float ly = bottom + height - 6f;
        RelatorioPdfDrawing.fillRect(cs, x, ly, 10f, 3f, color1);
        RelatorioPdfDrawing.drawText(cs, fonts, legend1, false, 7f, x + 14f, ly, RelatorioPdfTheme.TEXTO_LABEL);
        if (series2 != null) {
            RelatorioPdfDrawing.fillRect(cs, x + 90f, ly, 10f, 3f, color2);
            RelatorioPdfDrawing.drawText(cs, fonts, legend2, false, 7f, x + 104f, ly, RelatorioPdfTheme.TEXTO_LABEL);
        }
    }

    private static void drawSeries(
            PDPageContentStream cs,
            float x,
            float bottom,
            float step,
            float plotH,
            List<Item> series,
            long max,
            Color color)
            throws IOException {
        RelatorioPdfDrawing.setStroke(cs, color);
        cs.setLineWidth(1.6f);
        boolean first = true;
        for (int i = 0; i < series.size(); i++) {
            float px = x + i * step;
            float py = bottom + plotH * (series.get(i).value() / (float) max);
            if (first) {
                cs.moveTo(px, py);
                first = false;
            } else {
                cs.lineTo(px, py);
            }
        }
        cs.stroke();
    }

    static void drawLegendWithFonts(
            PDPageContentStream cs, RelatorioPdfFonts fonts, float x, float top, float width, List<Item> items)
            throws IOException {
        float y = top;
        for (int i = 0; i < Math.min(items.size(), 8); i++) {
            Item item = items.get(i);
            RelatorioPdfDrawing.fillRect(cs, x, y - 8f, 8f, 8f, CORES[i % CORES.length]);
            RelatorioPdfDrawing.drawTextFit(
                    cs, fonts, truncate(item.label(), 28), false, 6.5f, x + 12f, y - 2f, width - 14f, RelatorioPdfTheme.TEXTO);
            y -= 12f;
        }
    }

    private static void fillRingSlice(
            PDPageContentStream cs,
            float cx,
            float cy,
            float inner,
            float outer,
            float startDeg,
            float sweepDeg,
            Color color)
            throws IOException {
        if (Math.abs(sweepDeg) < 0.01f) {
            return;
        }
        int steps = Math.max(8, (int) (Math.abs(sweepDeg) / 6f));
        float start = (float) Math.toRadians(startDeg);
        float sweep = (float) Math.toRadians(sweepDeg);
        RelatorioPdfDrawing.setFill(cs, color);
        cs.moveTo(cx + (float) Math.cos(start) * inner, cy + (float) Math.sin(start) * inner);
        cs.lineTo(cx + (float) Math.cos(start) * outer, cy + (float) Math.sin(start) * outer);
        for (int i = 1; i <= steps; i++) {
            float a = start + sweep * i / steps;
            cs.lineTo(cx + (float) Math.cos(a) * outer, cy + (float) Math.sin(a) * outer);
        }
        for (int i = steps; i >= 0; i--) {
            float a = start + sweep * i / steps;
            cs.lineTo(cx + (float) Math.cos(a) * inner, cy + (float) Math.sin(a) * inner);
        }
        cs.closePath();
        cs.fill();
    }

    private static void empty(PDPageContentStream cs, RelatorioPdfFonts fonts, float x, float bottom)
            throws IOException {
        RelatorioPdfDrawing.drawText(
                cs, fonts, "Sem dados para exibir.", false, 8f, x, bottom + 20f, RelatorioPdfTheme.TEXTO_LABEL);
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, Math.max(0, max - 3)) + "...";
    }
}
