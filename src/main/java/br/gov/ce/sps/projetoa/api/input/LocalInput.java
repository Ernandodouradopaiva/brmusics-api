package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalInput {

    @NotBlank
    @Size(max = 255)
    private String nome;

    @Size(max = 255)
    private String endereco;

    @Size(max = 255)
    private String bairro;

    @Size(max = 255)
    private String cidade;

    @Size(max = 2000)
    private String observacao;

    private Boolean ativo;
}
