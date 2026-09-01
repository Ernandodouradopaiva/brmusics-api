package br.gov.ce.sps.projetoa.api.dto;

import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EscalaModel {

    private UUID codigo;
    private EscalaStatus status;
    private UUID celebracaoCodigo;
    private String celebracaoTitulo;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String localNome;
    private String diaSemana;
    private CelebracaoStatus celebracaoStatus;
    private List<EscalaMusicoModel> participacoes = new ArrayList<>();
    private List<String> alertas = new ArrayList<>();
}
