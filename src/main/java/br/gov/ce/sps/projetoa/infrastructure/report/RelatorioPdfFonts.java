package br.gov.ce.sps.projetoa.infrastructure.report;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/** Fonte DejaVu Sans embutida (sans-serif, próxima ao modelo web/PDF executivo). */
final class RelatorioPdfFonts {

    private final PDFont regular;
    private final PDFont bold;

    private RelatorioPdfFonts(PDFont regular, PDFont bold) {
        this.regular = regular;
        this.bold = bold;
    }

    static RelatorioPdfFonts load(PDDocument document) throws IOException {
        try (InputStream reg = resource("/report/fonts/DejaVuSans.ttf");
                InputStream bld = resource("/report/fonts/DejaVuSans-Bold.ttf")) {
            if (reg == null || bld == null) {
                throw new IOException("Fontes do relatório não encontradas em /report/fonts");
            }
            return new RelatorioPdfFonts(
                    PDType0Font.load(document, reg, true),
                    PDType0Font.load(document, bld, true));
        }
    }

    private static InputStream resource(String path) {
        return RelatorioPdfFonts.class.getResourceAsStream(path);
    }

    PDFont regular() {
        return regular;
    }

    PDFont bold() {
        return bold;
    }
}
