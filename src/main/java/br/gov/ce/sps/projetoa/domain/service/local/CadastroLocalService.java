package br.gov.ce.sps.projetoa.domain.service.local;

import br.gov.ce.sps.projetoa.api.input.LocalInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CadastroLocalService {

    private final LocalRepository localRepository;

    @Transactional
    public Local salvar(LocalInput input) {
        Local local = new Local();
        aplicarDados(local, input, null);
        if (local.getAtivo() == null) {
            local.setAtivo(Boolean.TRUE);
        }
        return localRepository.save(local);
    }

    void aplicarDados(Local local, LocalInput input, Long localIdIgnorar) {
        if (!StringUtils.hasText(input.getNome())) {
            throw new NegocioException("Informe o nome do local.");
        }
        String nome = input.getNome().trim();
        garantirNomeUnico(nome, localIdIgnorar);
        local.setNome(nome);
        local.setEndereco(blankToNull(input.getEndereco()));
        local.setBairro(blankToNull(input.getBairro()));
        local.setCidade(blankToNull(input.getCidade()));
        local.setObservacao(blankToNull(input.getObservacao()));
        if (input.getAtivo() != null) {
            local.setAtivo(input.getAtivo());
        }
    }

    private void garantirNomeUnico(String nome, Long localIdIgnorar) {
        boolean duplicado = localIdIgnorar == null
                ? localRepository.existsByNomeIgnoreCase(nome)
                : localRepository.existsByNomeIgnoreCaseAndIdNot(nome, localIdIgnorar);
        if (duplicado) {
            throw new NegocioException("Já existe um local cadastrado com este nome.");
        }
    }

    private static String blankToNull(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        return valor.trim();
    }
}
