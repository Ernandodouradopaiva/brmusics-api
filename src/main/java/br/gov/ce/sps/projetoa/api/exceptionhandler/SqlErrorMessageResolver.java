package br.gov.ce.sps.projetoa.api.exceptionhandler;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Locale;

/**
 * Traduz mensagens técnicas de JDBC/PostgreSQL para texto compreensível ao usuário.
 */
final class SqlErrorMessageResolver {

    private SqlErrorMessageResolver() {
    }

    static String resolve(Throwable ex) {
        Throwable root = ExceptionUtils.getRootCause(ex);
        String msg = root != null ? root.getMessage() : ex.getMessage();
        if (msg == null || msg.isBlank()) {
            return null;
        }
        String lower = msg.toLowerCase(Locale.ROOT);

        if (lower.contains("42p01") && lower.contains("does not exist")) {
            return "Erro de configuração do banco de dados: estrutura ausente. Contate o suporte técnico.";
        }

        if (lower.contains("duplicate key") || lower.contains("unique constraint")) {
            if (lower.contains("grupo") && lower.contains("nome")) {
                return "Já existe um grupo com este nome. Escolha outro nome.";
            }
            if (lower.contains("usuario") && lower.contains("cpf")) {
                return "Já existe um usuário cadastrado com este CPF.";
            }
            return "Não foi possível salvar: registro duplicado ou conflito de identificador.";
        }

        if (lower.contains("usuario_grupo")) {
            if (lower.contains("table \"usuario\"") || lower.contains("on table \"usuario\"")) {
                return "Não é possível excluir o usuário pois está vinculado a um ou mais grupos. "
                        + "Remova os vínculos antes de excluir.";
            }
            return "Não é possível excluir o grupo pois existem usuários vinculados. "
                    + "Remova os usuários do grupo antes de excluir.";
        }

        if (lower.contains("grupo_permissao")) {
            return "Não é possível excluir o grupo ou a permissão: existem vínculos ativos.";
        }

        return null;
    }
}
