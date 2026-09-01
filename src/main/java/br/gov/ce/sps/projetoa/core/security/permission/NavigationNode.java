package br.gov.ce.sps.projetoa.core.security.permission;

import java.util.List;

public record NavigationNode(
        String id,
        String label,
        String recurso,
        List<NavigationNode> children,
        int ordem) {

    public boolean isGrupo() {
        return recurso == null || recurso.isBlank();
    }
}
