package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InstrumentoModelBasico {
    private Long id;
    private UUID codigo;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private Integer ordem;
}
