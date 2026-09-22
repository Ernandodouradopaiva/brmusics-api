package br.gov.ce.sps.projetoa.domain.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicaFilter {
    /** Busca unificada: título, trecho da letra, autor ou intérprete. */
    private String termo;
    private String titulo;
    private String autor;
    private String categoria;
    private Boolean ativo;
}
