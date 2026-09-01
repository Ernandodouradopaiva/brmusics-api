package br.gov.ce.sps.projetoa.api.input;

import br.gov.ce.sps.projetoa.domain.model.enums.EscalaConfirmacaoStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EscalaMusicoInput {

    @NotNull
    private UUID musicoCodigo;

    @NotNull
    private UUID instrumentoCodigo;

    @Size(max = 2000)
    private String observacao;

    private EscalaConfirmacaoStatus statusConfirmacao;
}
