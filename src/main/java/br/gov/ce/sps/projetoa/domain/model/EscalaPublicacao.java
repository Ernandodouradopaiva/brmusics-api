package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "escala_publicacao")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "escala_publicacao_id_seq", allocationSize = 1)
public class EscalaPublicacao extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "versao", nullable = false)
    private Integer versao;

    @Column(name = "publicado_em", nullable = false)
    private OffsetDateTime publicadoEm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publicado_por_id", nullable = false)
    private Usuario publicadoPor;

    @Column(name = "publicado_por_nome", nullable = false)
    private String publicadoPorNome;

    @Column(name = "quantidade_celebracoes", nullable = false)
    private Integer quantidadeCelebracoes;

    @Column(name = "quantidade_musicos", nullable = false)
    private Integer quantidadeMusicos;

    @Column(name = "quantidade_escalas", nullable = false)
    private Integer quantidadeEscalas;

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("data ASC, horaInicio ASC, id ASC")
    private List<EscalaPublicacaoCelebracao> celebracoes = new ArrayList<>();

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<EscalaPublicacaoAlteracao> alteracoes = new ArrayList<>();
}
