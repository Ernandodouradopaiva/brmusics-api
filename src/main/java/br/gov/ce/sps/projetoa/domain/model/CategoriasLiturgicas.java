package br.gov.ce.sps.projetoa.domain.model;

import java.util.List;
import java.util.Locale;

import org.springframework.util.StringUtils;

/**
 * Categorias litúrgicas sugeridas. Persistidas como texto para permitir evolução
 * sem migration de enum (novos códigos podem ser aceitos).
 */
public final class CategoriasLiturgicas {

    public static final String ENTRADA = "ENTRADA";
    public static final String ATO_PENITENCIAL = "ATO_PENITENCIAL";
    public static final String GLORIA = "GLORIA";
    public static final String SALMO = "SALMO";
    public static final String ACLAMACAO = "ACLAMACAO";
    public static final String PRECES = "PRECES";
    public static final String OFERTORIO = "OFERTORIO";
    public static final String SANTO = "SANTO";
    public static final String ORACAO_EUCAISTICA = "ORACAO_EUCAISTICA";
    public static final String ELEVACAO = "ELEVACAO";
    public static final String AMEM = "AMEM";
    public static final String CORDEIRO = "CORDEIRO";
    public static final String COMUNHAO = "COMUNHAO";
    public static final String POS_COMUNHAO = "POS_COMUNHAO";
    public static final String FINAL = "FINAL";
    public static final String ADORACAO = "ADORACAO";
    public static final String MARIANA = "MARIANA";
    public static final String ESPIRITO_SANTO = "ESPIRITO_SANTO";
    public static final String LOUVOR = "LOUVOR";
    public static final String OUTROS = "OUTROS";

    /** Legado — preferir {@link #COMUNHAO}. */
    public static final String COMUNHAO_01 = "COMUNHAO_01";
    /** Legado — preferir {@link #COMUNHAO}. */
    public static final String COMUNHAO_02 = "COMUNHAO_02";
    /** Legado — preferir {@link #OUTROS}. */
    public static final String OUTRO = "OUTRO";

    public static final List<String> SUGERIDAS = List.of(
            ENTRADA,
            ATO_PENITENCIAL,
            GLORIA,
            SALMO,
            ACLAMACAO,
            PRECES,
            OFERTORIO,
            SANTO,
            ORACAO_EUCAISTICA,
            ELEVACAO,
            AMEM,
            CORDEIRO,
            COMUNHAO,
            POS_COMUNHAO,
            FINAL,
            ADORACAO,
            MARIANA,
            ESPIRITO_SANTO,
            LOUVOR,
            OUTROS);

    private CategoriasLiturgicas() {
    }

    public static String normalizar(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        String codigo = valor.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        if (COMUNHAO_01.equals(codigo) || COMUNHAO_02.equals(codigo)) {
            return COMUNHAO;
        }
        if (OUTRO.equals(codigo)) {
            return OUTROS;
        }
        if (codigo.length() > 40) {
            return codigo.substring(0, 40);
        }
        return codigo;
    }

    /** Índice canônico para ordenação; códigos desconhecidos vão ao final. */
    public static int indiceOrdenacao(String codigo) {
        if (!StringUtils.hasText(codigo)) {
            return SUGERIDAS.size();
        }
        String efetivo = codigo;
        if (COMUNHAO_01.equals(codigo) || COMUNHAO_02.equals(codigo)) {
            efetivo = COMUNHAO;
        } else if (OUTRO.equals(codigo)) {
            efetivo = OUTROS;
        }
        int indice = SUGERIDAS.indexOf(efetivo);
        return indice < 0 ? SUGERIDAS.size() : indice;
    }

    public static String rotulo(String codigo) {
        if (!StringUtils.hasText(codigo)) {
            return null;
        }
        return switch (codigo) {
            case ENTRADA -> "Entrada";
            case ATO_PENITENCIAL -> "Ato Penitencial";
            case GLORIA -> "Glória";
            case SALMO -> "Salmo Responsorial";
            case ACLAMACAO -> "Aclamação ao Evangelho";
            case PRECES -> "Preces";
            case OFERTORIO -> "Apresentação das Oferendas";
            case SANTO -> "Santo";
            case ORACAO_EUCAISTICA -> "Oração Eucarística";
            case ELEVACAO -> "Elevação";
            case AMEM -> "Amém";
            case CORDEIRO -> "Cordeiro de Deus";
            case COMUNHAO, COMUNHAO_01, COMUNHAO_02 -> "Comunhão";
            case POS_COMUNHAO -> "Pós-Comunhão / Ação de Graças";
            case FINAL -> "Final";
            case ADORACAO -> "Adoração";
            case MARIANA -> "Mariana";
            case ESPIRITO_SANTO -> "Espírito Santo";
            case LOUVOR -> "Louvor";
            case OUTROS, OUTRO -> "Outros";
            default -> codigo.replace('_', ' ');
        };
    }
}
