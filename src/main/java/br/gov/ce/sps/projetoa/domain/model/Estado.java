package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.jcip.annotations.Immutable;

@JsonRootName("estado")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SequenceGenerator(name = "seq", sequenceName = "estado_id_seq", allocationSize = 1)
@Immutable
public class Estado extends AbstractEntity {

    @NotBlank
    private String nome;

    @NotBlank
    private String ibge;

    @NotBlank
    @Column(name = "sigla")
    private String uf;

}
