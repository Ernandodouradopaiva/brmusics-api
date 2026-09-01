package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Audited
@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "usuario")
@SequenceGenerator(name = "seq", sequenceName = "usuario_id_seq", allocationSize = 1)
public class Usuario extends AbstractEntity implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @NotAudited
    @Column(name = "senha")
    private String senha;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "recebe_email")
    private Boolean recebeEmail = Boolean.TRUE;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_grupo", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "grupo_id"))
    private List<Grupo> grupos = new ArrayList<>();

    @Column(name = "ativo")
    private Boolean ativo = Boolean.TRUE;

    public boolean adicionarGrupo(Grupo grupo) {
        return getGrupos().add(grupo);
    }

    public boolean removerGrupo(Grupo grupo) {
        return getGrupos().remove(grupo);
    }

    public List<String> getGruposListString() {
        return getGrupos().stream().map(Grupo::getNome).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "read");
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.getCpf();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getAtivo() != null && getAtivo();
    }
}
