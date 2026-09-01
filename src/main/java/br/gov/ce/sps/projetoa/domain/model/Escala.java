package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.List;

@Audited
@Entity
@Table(name = "escala")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "escala_id_seq", allocationSize = 1)
public class Escala extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "celebracao_id", nullable = false, unique = true)
    private Celebracao celebracao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EscalaStatus status = EscalaStatus.RASCUNHO;

    @OneToMany(mappedBy = "escala", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @OrderBy("ordem ASC, id ASC")
    private List<EscalaMusico> participacoes = new ArrayList<>();

    public boolean isRascunho() {
        return status == null || status == EscalaStatus.RASCUNHO;
    }
}
