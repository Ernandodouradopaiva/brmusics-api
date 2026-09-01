package br.gov.ce.sps.projetoa.domain.service.musica;

import java.util.UUID;

/**
 * Convenção de pasta no storage (MinIO ou local): {@code sistemas/brmusic/musicas/{codigo}}.
 * Upload de cifra/áudio será ligado depois; a exclusão da música já limpa esta pasta.
 */
public final class MusicaStoragePaths {

    public static final String PREFIXO = "sistemas/brmusic/musicas";

    public static final String TIPO_CIFRA = "CIFRA";
    public static final String TIPO_AUDIO = "AUDIO";
    public static final String TIPO_PARTITURA = "PARTITURA";
    public static final String TIPO_OUTRO = "OUTRO";

    private MusicaStoragePaths() {
    }

    public static String pasta(UUID codigoMusica) {
        if (codigoMusica == null) {
            throw new IllegalArgumentException("Código da música é obrigatório para o caminho de storage.");
        }
        return PREFIXO + "/" + codigoMusica;
    }
}
