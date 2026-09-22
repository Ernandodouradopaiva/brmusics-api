package br.gov.ce.sps.projetoa.infrastructure.report;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * Facade dos ícones de relatório PDF — delega para Font Awesome Free Solid.
 * Mapeamento alinhado a {@code src/shared/report-icons.ts} no frontend.
 */
public final class RelatorioPdfIcons {

    private RelatorioPdfIcons() {}

    /** Tipos usados nos geradores de relatório (mapeiam para {@link RelatorioPdfFontAwesome.Icon}). */
    public enum Tipo {
        CALENDARIO(RelatorioPdfFontAwesome.Icon.CALENDAR_DAYS),
        PERIODO(RelatorioPdfFontAwesome.Icon.CALENDAR_DAYS),
        PESSOAS(RelatorioPdfFontAwesome.Icon.USERS),
        TOTAL_COLABORADORES(RelatorioPdfFontAwesome.Icon.USERS),
        TOTAL_USUARIOS(RelatorioPdfFontAwesome.Icon.USERS),
        SETOR(RelatorioPdfFontAwesome.Icon.BUILDING),
        UNIDADE(RelatorioPdfFontAwesome.Icon.BUILDING),
        FUNIL(RelatorioPdfFontAwesome.Icon.FILTER),
        TIPO_SETOR(RelatorioPdfFontAwesome.Icon.FILTER),
        CARGO(RelatorioPdfFontAwesome.Icon.BRIEFCASE),
        FUNCAO(RelatorioPdfFontAwesome.Icon.BRIEFCASE),
        VINCULO(RelatorioPdfFontAwesome.Icon.USER_TIE),
        MATRICULA(RelatorioPdfFontAwesome.Icon.ID_CARD),
        CPF(RelatorioPdfFontAwesome.Icon.ID_CARD),
        MASCULINO(RelatorioPdfFontAwesome.Icon.MALE),
        FEMININO(RelatorioPdfFontAwesome.Icon.FEMALE),
        PESSOA(RelatorioPdfFontAwesome.Icon.USER),
        INDEFINIDO(RelatorioPdfFontAwesome.Icon.CIRCLE_QUESTION),
        OBSERVACOES(RelatorioPdfFontAwesome.Icon.FILE_LINES),
        RELATORIO_EXECUTIVO(RelatorioPdfFontAwesome.Icon.CLIPBOARD_LIST),
        PRANCHETA(RelatorioPdfFontAwesome.Icon.CLIPBOARD_LIST),
        ENDERECO(RelatorioPdfFontAwesome.Icon.LOCATION_DOT),
        TELEFONE(RelatorioPdfFontAwesome.Icon.PHONE),
        EMAIL(RelatorioPdfFontAwesome.Icon.ENVELOPE),
        STATUS(RelatorioPdfFontAwesome.Icon.CIRCLE_CHECK),
        SISTEMA(RelatorioPdfFontAwesome.Icon.BUILDING);

        private final RelatorioPdfFontAwesome.Icon fontAwesome;

        Tipo(RelatorioPdfFontAwesome.Icon fontAwesome) {
            this.fontAwesome = fontAwesome;
        }

        RelatorioPdfFontAwesome.Icon fontAwesome() {
            return fontAwesome;
        }
    }

    static void drawFilled(
            PDPageContentStream cs,
            RelatorioPdfFontAwesome icons,
            Tipo tipo,
            float cx,
            float cy,
            float size,
            Color color)
            throws IOException {
        icons.draw(cs, tipo.fontAwesome(), cx, cy, size, color);
    }
}
