package br.gov.ce.sps.projetoa.api.input;

import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class CelebracaoInput {

    @NotNull
    private UUID localCodigo;

    @NotBlank
    @Size(max = 255)
    private String titulo;

    @NotNull
    private LocalDate data;

    @NotNull
    private LocalTime horaInicio;

    private LocalTime horaFim;

    @Size(max = 2000)
    private String descricao;

    @Size(max = 2000)
    private String observacao;

    private CelebracaoStatus status;

    /**
     * FIXA gera todas as ocorrências do mesmo dia da semana no ano de {@code data}.
     * EXTRAORDINARIA (padrão) cria apenas a data informada.
     */
    private CelebracaoTipo tipo;
}
