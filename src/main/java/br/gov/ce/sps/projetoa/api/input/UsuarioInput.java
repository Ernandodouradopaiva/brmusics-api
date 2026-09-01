package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UsuarioInput {
    @NotBlank
    private String nome;

    @NotBlank
    private String cpf;

    @Email
    @NotBlank
    private String email;

    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    private String senha;

    private String cargo;

    private Boolean ativo;

    private List<UUID> grupos;
}
