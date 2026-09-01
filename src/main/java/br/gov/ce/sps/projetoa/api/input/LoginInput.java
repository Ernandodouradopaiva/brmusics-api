package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.NotBlank;

public record LoginInput(@NotBlank String cpf, @NotBlank String senha) {}
