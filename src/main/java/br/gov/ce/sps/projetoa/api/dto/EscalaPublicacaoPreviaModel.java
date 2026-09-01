package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EscalaPublicacaoPreviaModel {

    private int ano;
    private int mes;
    private String competencia;
    private Integer versaoAtual;
    private int proximaVersao;
    private OffsetDateTime publicadoEm;
    private String publicadoPorNome;
    private int quantidadeCelebracoes;
    private int quantidadeMusicos;
    private int quantidadeEscalas;
    private boolean podePublicar;
    private List<String> impedimentos = new ArrayList<>();
    private List<EscalaPublicacaoAlteracaoModel> alteracoes = new ArrayList<>();
}
