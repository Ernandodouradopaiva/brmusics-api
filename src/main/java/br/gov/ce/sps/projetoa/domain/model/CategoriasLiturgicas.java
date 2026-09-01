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
    public static final String OFERTORIO = "OFERTORIO";
    public static final String SANTO = "SANTO";
    public static final String CORDEIRO = "CORDEIRO";
    public static final String COMUNHAO = "COMUNHAO";
    public static final String POS_COMUNHAO = "POS_COMUNHAO";
    public static final String FINAL = "FINAL";
    public static final String OUTRO = "OUTRO";

    public static final List<String> SUGERIDAS = List.of(
            ENTRADA,
            ATO_PENITENCIAL,
            GLORIA,
            SALMO,
            ACLAMACAO,
            OFERTORIO,
            SANTO,
            CORDEIRO,
            COMUNHAO,
            POS_COMUNHAO,
            FINAL,
            OUTRO);

    private CategoriasLiturgicas() {
    }

    public static String normalizar(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        String codigo = valor.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        if (codigo.length() > 40) {
            return codigo.substring(0, 40);
        }
        return codigo;
    }

    public static String rotulo(String codigo) {
        if (!StringUtils.hasText(codigo)) {
            return null;
        }
        return switch (codigo) {
            case ENTRADA -> "Entrada";
            case ATO_PENITENCIAL -> "Ato penitencial";
            case GLORIA -> "Glória";
            case SALMO -> "Salmo";
            case ACLAMACAO -> "Aclamação";
            case OFERTORIO -> "Ofertório";
            case SANTO -> "Santo";
            case CORDEIRO -> "Cordeiro";
            case COMUNHAO -> "Comunhão";
            case POS_COMUNHAO -> "Pós-comunhão";
            case FINAL -> "Final";
            case OUTRO -> "Outro";
            default -> codigo.replace('_', ' ');
        };
    }
}
