package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.enums.FrequenciaStatus;
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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Audited
@Entity
@Table(
        name = "frequencia_mensal",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_frequencia_mensal_musico_periodo",
                columnNames = {"musico_id", "ano", "mes"}))
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "frequencia_mensal_id_seq", allocationSize = 1)
public class FrequenciaMensal extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "musico_id", nullable = false)
    private Musico musico;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Enumerated(EnumType.STRING)
    @Column(name = "semana1", length = 30)
    private FrequenciaStatus semana1;

    @Enumerated(EnumType.STRING)
    @Column(name = "semana2", length = 30)
    private FrequenciaStatus semana2;

    @Enumerated(EnumType.STRING)
    @Column(name = "semana3", length = 30)
    private FrequenciaStatus semana3;

    @Enumerated(EnumType.STRING)
    @Column(name = "semana4", length = 30)
    private FrequenciaStatus semana4;
}
