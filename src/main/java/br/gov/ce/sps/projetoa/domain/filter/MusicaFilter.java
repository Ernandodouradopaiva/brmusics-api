package br.gov.ce.sps.projetoa.domain.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicaFilter {
    private String titulo;
    private String autor;
    private String categoria;
    private Boolean ativo;
}
