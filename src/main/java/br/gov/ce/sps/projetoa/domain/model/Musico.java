package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "musico")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "musico_id_seq", allocationSize = 1)
public class Musico extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "nome_artistico")
    private String nomeArtistico;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "whatsapp", nullable = false)
    private String whatsapp;

    @Column(name = "email")
    private String email;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "musico_instrumento",
            joinColumns = @JoinColumn(name = "musico_id"),
            inverseJoinColumns = @JoinColumn(name = "instrumento_id"))
    private Set<Instrumento> instrumentos = new HashSet<>();

    public boolean podeSerEscalado() {
        return Boolean.TRUE.equals(ativo);
    }
}
