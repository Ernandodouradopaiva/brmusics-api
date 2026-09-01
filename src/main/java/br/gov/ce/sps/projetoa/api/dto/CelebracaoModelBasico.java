package br.gov.ce.sps.projetoa.api.dto;

import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class CelebracaoModelBasico {
    private Long id;
    private UUID codigo;
    private UUID localCodigo;
    private String localNome;
    private String titulo;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String descricao;
    private String observacao;
    private CelebracaoStatus status;
}
