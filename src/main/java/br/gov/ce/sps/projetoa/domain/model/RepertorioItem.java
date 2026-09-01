package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Audited
@Entity
@Table(name = "repertorio_item")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "repertorio_item_id_seq", allocationSize = 1)
public class RepertorioItem extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repertorio_id", nullable = false)
    private Repertorio repertorio;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "musica_id", nullable = false)
    private Musica musica;

    @Column(name = "momento_liturgico", nullable = false, length = 40)
    private String momentoLiturgico;

    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;

    @Column(name = "tom", length = 20)
    private String tom;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;
}
