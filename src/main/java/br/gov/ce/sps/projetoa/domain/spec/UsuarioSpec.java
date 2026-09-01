package br.gov.ce.sps.projetoa.domain.spec;

import br.gov.ce.sps.projetoa.domain.filter.UsuarioFilter;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class UsuarioSpec {

    public static Specification<Usuario> usandoFiltro(UsuarioFilter filtro) {
        return (root, query, builder) -> {
            var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(filtro.getBusca())) {
                String termo = filtro.getBusca().trim().toLowerCase();
                String cpfTermo = filtro.getBusca().trim().replaceAll("\\D", "");
                var orBusca = new ArrayList<Predicate>();
                orBusca.add(builder.like(builder.lower(root.get("nome")), "%" + termo + "%"));
                if (StringUtils.hasText(cpfTermo)) {
                    orBusca.add(builder.like(root.get("cpf"), "%" + cpfTermo + "%"));
                }
                predicates.add(builder.or(orBusca.toArray(new Predicate[0])));
            } else {
                if (StringUtils.hasText(filtro.getNome())) {
                    predicates.add(builder.like(builder.lower(root.get("nome")), "%" + filtro.getNome().trim().toLowerCase() + "%"));
                }

                if (StringUtils.hasText(filtro.getCpf())) {
                    String cpf = filtro.getCpf().replaceAll("\\D", "");
                    predicates.add(builder.equal(root.get("cpf"), cpf));
                }
            }

            if (filtro.getAtivo() != null) {
                if (filtro.getAtivo()) {
                    predicates.add(builder.isTrue(root.get("ativo")));
                } else {
                    predicates.add(builder.isFalse(root.get("ativo")));
                }
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
