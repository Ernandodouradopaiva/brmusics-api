package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UsuarioModelBasico {
    private Long id;
    private UUID codigo;
    private String nome;
    private String cpf;
    private String cargo;
    private String email;
    private Boolean ativo;
    private Boolean recebeEmail;
    private List<GrupoModelBasico> grupos = new ArrayList<>();
}
