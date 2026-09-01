package br.gov.ce.sps.projetoa.domain.spec;

import br.gov.ce.sps.projetoa.domain.model.Permissao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class PermissaoSpec {

    private PermissaoSpec() {
    }

    public static Specification<Permissao> comBusca(String busca) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(busca)) {
                return builder.conjunction();
            }
            String termo = busca.trim().toLowerCase();
            var or = new ArrayList<Predicate>();
            or.add(builder.like(builder.lower(builder.coalesce(root.get("chave"), "")), "%" + termo + "%"));
            or.add(builder.like(builder.lower(builder.coalesce(root.get("nome"), "")), "%" + termo + "%"));
            or.add(builder.like(builder.lower(builder.coalesce(root.get("descricao"), "")), "%" + termo + "%"));
            or.add(builder.like(builder.lower(builder.coalesce(root.get("modulo"), "")), "%" + termo + "%"));
            return builder.or(or.toArray(new Predicate[0]));
        };
    }
}
