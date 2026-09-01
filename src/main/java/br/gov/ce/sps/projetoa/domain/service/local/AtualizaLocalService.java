package br.gov.ce.sps.projetoa.domain.service.local;

import br.gov.ce.sps.projetoa.api.input.LocalInput;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizaLocalService {

    private final LocalRepository localRepository;
    private final CadastroLocalService cadastroLocalService;

    @Transactional
    public Local atualiza(Local local, LocalInput input) {
        if (local.getCodigo() == null || local.getId() == null) {
            throw new RuntimeException("Local não encontrado");
        }
        cadastroLocalService.aplicarDados(local, input, local.getId());
        return localRepository.save(local);
    }

    @Transactional
    public Local atualizarAtivo(Local local, boolean ativo) {
        local.setAtivo(ativo);
        return localRepository.save(local);
    }
}
