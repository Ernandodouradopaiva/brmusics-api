package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MusicoModelBasico {
    private Long id;
    private UUID codigo;
    private String nome;
    private String nomeArtistico;
    private String telefone;
    private String whatsapp;
    private String email;
    private String observacao;
    private Boolean ativo;
    private UUID usuarioCodigo;
    private String usuarioNome;
    private List<InstrumentoModelBasico> instrumentos = new ArrayList<>();
    private OffsetDateTime dataCadastro;
    private OffsetDateTime dataAtualizacao;
}
