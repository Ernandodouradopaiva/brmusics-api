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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "escala_publicacao_celebracao")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "escala_publicacao_celebracao_id_seq", allocationSize = 1)
public class EscalaPublicacaoCelebracao extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publicacao_id", nullable = false)
    private EscalaPublicacao publicacao;

    @Column(name = "celebracao_codigo", nullable = false)
    private UUID celebracaoCodigo;

    @Column(name = "celebracao_titulo", nullable = false)
    private String celebracaoTitulo;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Column(name = "local_nome")
    private String localNome;

    @OneToMany(mappedBy = "celebracaoSnapshot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("ordem ASC, id ASC")
    private List<EscalaPublicacaoParticipacao> participacoes = new ArrayList<>();

    @OneToMany(mappedBy = "celebracaoSnapshot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("ordem ASC, id ASC")
    private List<EscalaPublicacaoRepertorioItem> repertorioItens = new ArrayList<>();
}
