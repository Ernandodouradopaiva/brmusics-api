package br.gov.ce.sps.projetoa.domain.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioFilter {
    private String nome;
    private String cpf;
    private String busca;
    private Boolean ativo;
}
