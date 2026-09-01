package br.gov.ce.sps.projetoa.core.security.permission;

public record PermissionDefinition(
        String chave,
        String modulo,
        String recurso,
        String acao,
        String descricao,
        int ordem,
        boolean sistema) {}
