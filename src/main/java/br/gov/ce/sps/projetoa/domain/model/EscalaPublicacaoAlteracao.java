package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.enums.EscalaPublicacaoAlteracaoTipo;
import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "escala_publicacao_alteracao")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "escala_publicacao_alteracao_id_seq", allocationSize = 1)
public class EscalaPublicacaoAlteracao extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publicacao_id", nullable = false)
    private EscalaPublicacao publicacao;

    @Column(name = "celebracao_codigo")
    private UUID celebracaoCodigo;

    @Column(name = "celebracao_titulo")
    private String celebracaoTitulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 40)
    private EscalaPublicacaoAlteracaoTipo tipo;

    @Column(name = "descricao", nullable = false, length = 2000)
    private String descricao;
}
