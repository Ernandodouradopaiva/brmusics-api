package br.gov.ce.sps.projetoa.core.security;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AuthenticationModel {

    private String accessToken;
    private Long id;
    private UUID codigo;
    private String username;
    private String nome;
    private List<String> roles;
    private List<String> authorities;
    private List<String> permissoes;
    private List<String> gruposCodigos;
    private String grupoNome;
    private String cargo;
    private String tokenType = "Bearer";
}
