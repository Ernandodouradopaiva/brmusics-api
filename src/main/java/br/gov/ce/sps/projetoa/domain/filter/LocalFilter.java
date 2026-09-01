package br.gov.ce.sps.projetoa.domain.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalFilter {
    private String nome;
    private String cidade;
    private Boolean ativo;
}
