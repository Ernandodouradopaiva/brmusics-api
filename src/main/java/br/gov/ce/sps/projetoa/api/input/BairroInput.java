package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BairroInput {

    @NotBlank(message = "Nome do bairro é obrigatório")
    private String nome;
}
