package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstrumentoInput {

    @NotBlank
    @Size(max = 255)
    private String nome;

    @Size(max = 2000)
    private String descricao;

    private Boolean ativo;

    private Integer ordem;
}
