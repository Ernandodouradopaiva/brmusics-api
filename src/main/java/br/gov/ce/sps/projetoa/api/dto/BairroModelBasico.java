package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BairroModelBasico {
    private Long id;
    private UUID codigo;
    private String nome;
    private MunicipioModelBasico municipio;
}