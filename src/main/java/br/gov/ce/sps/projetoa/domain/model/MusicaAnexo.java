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
@Table(name = "musica_anexo")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "musica_anexo_id_seq", allocationSize = 1)
public class MusicaAnexo extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "musica_id", nullable = false)
    private Musica musica;

    @Column(name = "tipo", nullable = false, length = 40)
    private String tipo;

    @Column(name = "nome_original")
    private String nomeOriginal;

    @Column(name = "content_type", length = 120)
    private String contentType;

    @Column(name = "caminho_relativo", nullable = false, length = 500)
    private String caminhoRelativo;

    @Column(name = "tamanho_bytes")
    private Long tamanhoBytes;
}
