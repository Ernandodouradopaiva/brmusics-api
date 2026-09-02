package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record GruposInput(
        @NotNull
        @Size(max = 1, message = "O usuário pode ter apenas um perfil (grupo) no BRMusics.")
        List<UUID> gruposIds
) {}
