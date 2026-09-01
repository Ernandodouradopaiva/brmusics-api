package br.gov.ce.sps.projetoa.infrastructure.report;

import java.awt.Color;

/** Paleta e medidas do relatório executivo SPS (referência visual terceirizados por setor). */
final class RelatorioPdfTheme {

    static final float MARGIN = 40f;
    static final float PAGE_FOOTER_RESERVED = 30f;
    static final float RUNNING_HEADER_HEIGHT = 20f;

    /** Verde institucional (títulos, ícones, borda superior dos cards). */
    static final Color VERDE_SPS = new Color(30, 95, 79);
    /** Fundo menta dos ícones. */
    static final Color VERDE_MENTA = new Color(232, 245, 236);
    /** Linha de destaque amarela sob o título. */
    static final Color AMARELO_ACCENT = new Color(244, 180, 26);
    static final Color TEXTO = new Color(15, 23, 42);
    static final Color TEXTO_LABEL = new Color(100, 116, 139);
    static final Color TEXTO_VALOR = new Color(15, 23, 42);
    static final Color BORDA = new Color(226, 232, 240);
    static final Color FUNDO_CARD = Color.WHITE;
    static final Color FUNDO_TABELA_HEADER = new Color(30, 95, 79);
    /** Texto sobre faixa verde do cabeçalho (mesma cor do header da tabela). */
    static final Color TEXTO_HEADER_FAIXA = Color.WHITE;
    static final Color TEXTO_HEADER_FAIXA_SECUNDARIO = new Color(220, 235, 228);
    static final Color FUNDO_LINHA_ALT = new Color(248, 250, 252);

    static final float ICON_BOX = 44f;
    static final float LOGO_MAX_HEIGHT = 44f;
    static final float HEADER_BAND_PAD_TOP = 6f;
    static final float HEADER_BAND_PAD_BOTTOM = 12f;
    /** Recuo do logo em relação à borda da faixa verde (esquerda e topo). */
    static final float HEADER_BAND_LOGO_INSET = 10f;
    static final float ICON_CIRCLE = 34f;
    static final float CARD_TOP_BORDER = 4f;
    static final float CARD_HEIGHT = 64f;
    /** Faixa do ícone — mais estreita para caber números formatados (pt-BR). */
    static final float CARD_ICON_RATIO = 0.28f;
    static final float CARD_GAP = 10f;
    static final float CARD_ROW_GAP = 8f;
    /** Espaço entre o bloco de cards (2 linhas) e o cabeçalho da tabela. */
    static final float CARDS_BLOCK_MARGIN = 16f;
    /** Espaço entre o conteúdo anterior e o bloco de observações. */
    static final float OBSERVACOES_TOP_MARGIN = 24f;
    static final float CARD_LABEL_FONT = 6.5f;
    static final float CARD_VALUE_FONT_MAX = 14f;
    static final float CARD_VALUE_FONT_MIN = 9.5f;
    static final float CARD_SUFFIX_FONT = 6.5f;

    static final String INSTITUCIONAL = "Secretaria da Proteção Social - Governo do Estado do Ceará";
    static final String BADGE_EXECUTIVO = "RELATÓRIO EXECUTIVO";

    private RelatorioPdfTheme() {}
}
