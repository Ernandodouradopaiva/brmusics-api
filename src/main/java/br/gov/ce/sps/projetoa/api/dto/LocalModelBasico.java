package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LocalModelBasico {
    private Long id;
    private UUID codigo;
    private String nome;
    private String endereco;
    private String bairro;
    private String cidade;
    private String observacao;
    private Boolean ativo;
}
