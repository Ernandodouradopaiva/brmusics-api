package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EscalaPublicacaoModel {

    private UUID codigo;
    private int ano;
    private int mes;
    private int versao;
    private OffsetDateTime publicadoEm;
    private UUID publicadoPorCodigo;
    private String publicadoPorNome;
    private int quantidadeCelebracoes;
    private int quantidadeMusicos;
    private int quantidadeEscalas;
    private List<EscalaPublicacaoAlteracaoModel> alteracoes = new ArrayList<>();
}
