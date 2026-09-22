package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CelebracaoCadastroModel extends CelebracaoModelBasico {

    /** Quantidade de ocorrências criadas (1 para extraordinária; N para fixa). */
    private Integer quantidadeGerada;

    private UUID serieCodigo;
}
