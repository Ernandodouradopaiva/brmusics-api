package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "local")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "local_id_seq", allocationSize = 1)
public class Local extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;
}
