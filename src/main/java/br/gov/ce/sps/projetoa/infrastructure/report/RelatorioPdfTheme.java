package br.gov.ce.sps.projetoa.infrastructure.report;

import java.awt.Color;

/** Paleta e medidas dos relatórios PDF — identidade visual BRMusics (madeira/creme/dourado). */
final class RelatorioPdfTheme {

    static final float MARGIN = 40f;
    static final float PAGE_FOOTER_RESERVED = 30f;
    static final float RUNNING_HEADER_HEIGHT = 20f;

    /** Madeira / accent (#5c3a22) — títulos, ícones, faixa e cabeçalho de tabela. */
    static final Color VERDE_SPS = new Color(0x5c, 0x3a, 0x22);
    /** Creme (#f5f0e8) — fundo dos ícones e faixas suaves. */
    static final Color VERDE_MENTA = new Color(0xf5, 0xf0, 0xe8);
    /** Dourado (#c4a574) — linha de destaque sob o título. */
    static final Color AMARELO_ACCENT = new Color(0xc4, 0xa5, 0x74);
    static final Color TEXTO = new Color(0x1e, 0x29, 0x3b);
    static final Color TEXTO_LABEL = new Color(0x64, 0x74, 0x8b);
    static final Color TEXTO_VALOR = new Color(0x1e, 0x29, 0x3b);
    static final Color BORDA = new Color(0xe8, 0xdc, 0xc8);
    static final Color FUNDO_CARD = Color.WHITE;
    static final Color FUNDO_TABELA_HEADER = new Color(0x5c, 0x3a, 0x22);
    static final Color TEXTO_HEADER_FAIXA = Color.WHITE;
    static final Color TEXTO_HEADER_FAIXA_SECUNDARIO = new Color(0xeb, 0xe4, 0xd6);
    static final Color FUNDO_LINHA_ALT = new Color(0xfa, 0xf6, 0xf0);

    static final float ICON_BOX = 44f;
    static final float LOGO_MAX_HEIGHT = 44f;
    static final float HEADER_BAND_PAD_TOP = 6f;
    static final float HEADER_BAND_PAD_BOTTOM = 12f;
    static final float HEADER_BAND_LOGO_INSET = 10f;
    static final float ICON_CIRCLE = 34f;
    static final float CARD_TOP_BORDER = 4f;
    static final float CARD_HEIGHT = 64f;
    static final float CARD_ICON_RATIO = 0.28f;
    static final float CARD_GAP = 10f;
    static final float CARD_ROW_GAP = 8f;
    static final float CARDS_BLOCK_MARGIN = 16f;
    static final float OBSERVACOES_TOP_MARGIN = 24f;
    static final float CARD_LABEL_FONT = 6.5f;
    static final float CARD_VALUE_FONT_MAX = 14f;
    static final float CARD_VALUE_FONT_MIN = 9.5f;
    static final float CARD_SUFFIX_FONT = 6.5f;

    static final String INSTITUCIONAL = "BRMusics — Gestão de músicos, escalas e repertórios";
    static final String BADGE_EXECUTIVO = "RELATÓRIO BRMUSICS";

    private RelatorioPdfTheme() {}
}
