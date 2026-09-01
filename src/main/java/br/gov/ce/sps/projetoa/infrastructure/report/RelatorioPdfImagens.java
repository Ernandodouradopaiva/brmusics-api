package br.gov.ce.sps.projetoa.infrastructure.report;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/** Logo institucional embutido nos relatórios PDF (classpath /images). */
final class RelatorioPdfImagens {

    private static final String LOGO_SISTEMA = "/images/logo-sistema.png";

    private final PDImageXObject logoSistema;

    private RelatorioPdfImagens(PDImageXObject logoSistema) {
        this.logoSistema = logoSistema;
    }

    static RelatorioPdfImagens load(PDDocument document) throws IOException {
        return new RelatorioPdfImagens(loadImage(document, LOGO_SISTEMA));
    }

    private static PDImageXObject loadImage(PDDocument document, String path) throws IOException {
        try (InputStream in = RelatorioPdfImagens.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Imagem do relatório não encontrada: " + path);
            }
            return PDImageXObject.createFromByteArray(document, in.readAllBytes(), path);
        }
    }

    PDImageXObject logoSistema() {
        return logoSistema;
    }

    float widthForHeight(PDImageXObject image, float height) {
        return height * image.getWidth() / (float) image.getHeight();
    }

    void draw(PDPageContentStream cs, PDImageXObject image, float x, float bottom, float width, float height)
            throws IOException {
        cs.drawImage(image, x, bottom, width, height);
    }
}
