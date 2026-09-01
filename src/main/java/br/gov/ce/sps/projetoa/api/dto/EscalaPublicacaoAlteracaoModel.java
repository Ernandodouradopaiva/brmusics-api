package br.gov.ce.sps.projetoa.api.dto;

import br.gov.ce.sps.projetoa.domain.model.enums.EscalaPublicacaoAlteracaoTipo;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EscalaPublicacaoAlteracaoModel {

    private UUID celebracaoCodigo;
    private String celebracaoTitulo;
    private EscalaPublicacaoAlteracaoTipo tipo;
    private String descricao;
}
