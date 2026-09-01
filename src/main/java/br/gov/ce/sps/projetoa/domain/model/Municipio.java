package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.jcip.annotations.Immutable;

@JsonRootName("municipio")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Immutable
@SequenceGenerator(name = "seq", sequenceName = "municipio_id_seq", allocationSize = 1)
public class Municipio extends AbstractEntity {

    @Size(max = 60)
    @NotBlank
    private String nome;

    @Column(name = "codigoibge")
    @NotNull
    private Long codigoIbge;

    @Valid
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_id", nullable = false)
    @JsonIgnore
    private Estado estado;

    // Helper method para acessar estado
    public Estado getEstado() {
        return estado;
    }
}
