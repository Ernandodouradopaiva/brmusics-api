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

import java.util.UUID;

@Entity
@Table(name = "escala_publicacao_repertorio")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "escala_publicacao_repertorio_id_seq", allocationSize = 1)
public class EscalaPublicacaoRepertorioItem extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publicacao_celebracao_id", nullable = false)
    private EscalaPublicacaoCelebracao celebracaoSnapshot;

    @Column(name = "musica_codigo", nullable = false)
    private UUID musicaCodigo;

    @Column(name = "musica_titulo", nullable = false)
    private String musicaTitulo;

    @Column(name = "momento_liturgico", nullable = false, length = 40)
    private String momentoLiturgico;

    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;

    @Column(name = "tom", length = 20)
    private String tom;
}
