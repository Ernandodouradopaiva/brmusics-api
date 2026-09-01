package br.gov.ce.sps.projetoa.infrastructure.report;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 * Ícones Font Awesome Free Solid (webfont fa-solid-900.ttf) para relatórios PDF.
 * Codepoints alinhados a {@code @fortawesome/free-solid-svg-icons}.
 */
final class RelatorioPdfFontAwesome {

    private static final String FONT_PATH = "/report/fonts/fa-solid-900.ttf";

    private final PDFont font;

    private RelatorioPdfFontAwesome(PDFont font) {
        this.font = font;
    }

    static RelatorioPdfFontAwesome load(PDDocument document) throws IOException {
        try (InputStream in = RelatorioPdfFontAwesome.class.getResourceAsStream(FONT_PATH)) {
            if (in == null) {
                throw new IOException("Fonte Font Awesome não encontrada: " + FONT_PATH);
            }
            return new RelatorioPdfFontAwesome(PDType0Font.load(document, in, true));
        }
    }

    void draw(PDPageContentStream cs, Icon icon, float cx, float cy, float sizePt, Color color) throws IOException {
        String glyph = icon.glyph();
        float fontSize = sizePt * 0.9f;
        float textWidth = font.getStringWidth(glyph) / 1000f * fontSize;
        float offsetY = -fontSize * 0.38f;
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.setNonStrokingColor(color);
        cs.newLineAtOffset(cx - textWidth / 2f, cy + offsetY);
        cs.showText(glyph);
        cs.endText();
    }

    /** Ícones solid — nomes equivalentes ao pacote npm Font Awesome. */
    enum Icon {
        CALENDAR_DAYS("\uF073"),
        USERS("\uF0C0"),
        BUILDING("\uF1AD"),
        FILTER("\uF0B0"),
        BRIEFCASE("\uF0B1"),
        USER_TIE("\uF508"),
        ID_CARD("\uF2C2"),
        MALE("\uF183"),
        FEMALE("\uF182"),
        USER("\uF007"),
        FILE_LINES("\uF15C"),
        CLIPBOARD_LIST("\uF46D"),
        LOCATION_DOT("\uF3C5"),
        PHONE("\uF095"),
        ENVELOPE("\uF0E0"),
        CIRCLE_CHECK("\uF058"),
        CIRCLE_QUESTION("\uF059");

        private final String glyph;

        Icon(String glyph) {
            this.glyph = glyph;
        }

        String glyph() {
            return glyph;
        }
    }
}
