package br.gov.ce.sps.projetoa.domain.filter;

import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CelebracaoFilter {
    private String titulo;
    private Integer mes;
    private Integer ano;
    private UUID localCodigo;
    private CelebracaoStatus status;
}
