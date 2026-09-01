package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
@JsonRootName("bairro")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SequenceGenerator(name = "seq", sequenceName = "bairro_id_seq", allocationSize = 1)
public class Bairro extends AbstractEntity {

    @NotBlank
    private String nome;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "municipio_id", nullable = false)
    private Municipio municipio;

    // Helper methods para acessar a hierarquia
    public Estado getEstado() {
        return municipio != null ? municipio.getEstado() : null;
    }
    
    public String getNomeCompleto() {
        StringBuilder sb = new StringBuilder(nome);
        if (municipio != null) {
            sb.append(", ").append(municipio.getNome());
            if (municipio.getEstado() != null) {
                sb.append(" - ").append(municipio.getEstado().getUf());
            }
        }
        return sb.toString();
    }
}
