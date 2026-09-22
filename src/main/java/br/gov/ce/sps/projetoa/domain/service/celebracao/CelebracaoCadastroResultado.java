package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.domain.model.Celebracao;

import java.util.UUID;

public record CelebracaoCadastroResultado(
        Celebracao referencia,
        int quantidadeGerada,
        UUID serieCodigo
) {
}
