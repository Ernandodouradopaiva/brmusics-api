package br.gov.ce.sps.projetoa.domain.service.local;

import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletaLocalService {

    private final LocalRepository localRepository;
    private final CelebracaoRepository celebracaoRepository;
    private final GetLocalService getLocalService;

    @Transactional
    public void deletar(UUID codigo) {
        Local local = getLocalService.findByCode(codigo);
        if (celebracaoRepository.existsByLocal_Id(local.getId())) {
            local.setAtivo(Boolean.FALSE);
            localRepository.save(local);
            return;
        }
        localRepository.delete(local);
    }
}
