package br.gov.ce.sps.projetoa.domain.model;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
@Data
@Embeddable
public class Endereco {

    @NotNull(message = "Bairro é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bairro_id", nullable = false)
    private Bairro bairro;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 255, message = "Logradouro deve ter no máximo 255 caracteres")
    private String logradouro;

    @Column(name = "logradouro_numero")
    @Size(max = 20, message = "Número do logradouro deve ter no máximo 20 caracteres")
    private String logradouroNumero;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    private String complemento;

    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar no formato 00000-000")
    @NotBlank(message = "CEP é obrigatório")
    @Column(name = "cep")
    private String cep;

    // Métodos helper para acessar a hierarquia completa
    
    /**
     * Retorna o Município através do Bairro.
     * Exemplo: endereco.getMunicipio().getNome() -> "Fortaleza"
     */
    public Municipio getMunicipio() {
        return bairro != null ? bairro.getMunicipio() : null;
    }

    /**
     * Retorna o Estado através do Município.
     * Exemplo: endereco.getEstado().getUf() -> "CE"
     */
    public Estado getEstado() {
        Municipio municipio = getMunicipio();
        return municipio != null ? municipio.getEstado() : null;
    }
    
    /**
     * Retorna uma representação completa do endereço em formato String.
     * Exemplo: "Rua das Flores, 123 - Centro, Fortaleza - CE"
     */
    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        
        if (logradouro != null) {
            sb.append(logradouro);
            if (logradouroNumero != null && !logradouroNumero.isEmpty()) {
                sb.append(", ").append(logradouroNumero);
            }
        }
        
        if (bairro != null && bairro.getNome() != null) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(bairro.getNome());
        }
        
        Municipio municipio = getMunicipio();
        if (municipio != null && municipio.getNome() != null) {
            sb.append(", ").append(municipio.getNome());
        }
        
        Estado estado = getEstado();
        if (estado != null && estado.getUf() != null) {
            sb.append(" - ").append(estado.getUf());
        }
        
        return sb.toString();
    }
}
