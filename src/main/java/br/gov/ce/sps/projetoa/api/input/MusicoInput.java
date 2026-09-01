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
public class MusicoInput {

    @NotBlank
    @Size(max = 255)
    private String nome;

    @Size(max = 255)
    private String nomeArtistico;

    @Size(max = 32)
    private String telefone;

    @NotBlank
    @Size(max = 32)
    private String whatsapp;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 2000)
    private String observacao;

    private Boolean ativo;

    private UUID usuarioCodigo;

    private List<UUID> instrumentosCodigos;
}
