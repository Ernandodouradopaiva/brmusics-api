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
@Table(name = "musica")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "musica_id_seq", allocationSize = 1)
public class Musica extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "autor")
    private String autor;

    @Column(name = "interprete_referencia")
    private String interpreteReferencia;

    @Column(name = "tom_padrao", length = 20)
    private String tomPadrao;

    @Column(name = "categoria_liturgica", length = 40)
    private String categoriaLiturgica;

    @Column(name = "letra", columnDefinition = "TEXT")
    private String letra;

    @Column(name = "cifra", columnDefinition = "TEXT")
    private String cifra;

    @Column(name = "link_referencia", length = 2000)
    private String linkReferencia;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;
}
