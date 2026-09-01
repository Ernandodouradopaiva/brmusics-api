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
@Table(name = "escala_publicacao_participacao")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "escala_publicacao_participacao_id_seq", allocationSize = 1)
public class EscalaPublicacaoParticipacao extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publicacao_celebracao_id", nullable = false)
    private EscalaPublicacaoCelebracao celebracaoSnapshot;

    @Column(name = "musico_codigo", nullable = false)
    private UUID musicoCodigo;

    @Column(name = "musico_nome", nullable = false)
    private String musicoNome;

    @Column(name = "instrumento_codigo", nullable = false)
    private UUID instrumentoCodigo;

    @Column(name = "instrumento_nome", nullable = false)
    private String instrumentoNome;

    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;
}
