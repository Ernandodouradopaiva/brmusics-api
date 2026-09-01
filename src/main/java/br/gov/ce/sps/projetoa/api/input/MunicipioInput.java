package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MunicipioInput {

    @NotBlank(message = "Nome do município é obrigatório")
    @Size(max = 60, message = "Nome do município deve ter no máximo 60 caracteres")
    private String nome;

    @NotNull(message = "Código IBGE do município é obrigatório")
    private Long codigoIbge;
}