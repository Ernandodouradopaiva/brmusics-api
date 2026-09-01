package br.gov.ce.sps.projetoa.domain.spec;

import br.gov.ce.sps.projetoa.domain.model.Grupo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class GrupoSpec {

    private GrupoSpec() {
    }

    public static Specification<Grupo> comBusca(String busca) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(busca)) {
                return builder.conjunction();
            }
            String termo = busca.trim().toLowerCase();
            return builder.like(builder.lower(root.get("nome")), "%" + termo + "%");
        };
    }
}
