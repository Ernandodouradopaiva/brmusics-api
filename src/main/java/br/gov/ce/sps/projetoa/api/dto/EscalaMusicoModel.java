package br.gov.ce.sps.projetoa.api.dto;

import br.gov.ce.sps.projetoa.domain.model.enums.EscalaConfirmacaoStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EscalaMusicoModel {

    private UUID codigo;
    private UUID musicoCodigo;
    private String musicoNome;
    private UUID instrumentoCodigo;
    private String instrumentoNome;
    private String observacao;
    private EscalaConfirmacaoStatus statusConfirmacao;
    private Integer ordem;
    private boolean instrumentoDoMusico;
}
