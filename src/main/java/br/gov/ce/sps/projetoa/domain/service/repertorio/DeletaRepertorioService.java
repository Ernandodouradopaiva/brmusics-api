package br.gov.ce.sps.projetoa.domain.service.repertorio;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.model.enums.RepertorioStatus;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletaRepertorioService {

    private final GetRepertorioService getRepertorioService;
    private final RepertorioItemRepository repertorioItemRepository;

    @Transactional
    public void deletar(UUID codigo) {
        Repertorio repertorio = getRepertorioService.findByCode(codigo);
        if (repertorio.getStatus() == RepertorioStatus.PUBLICADA) {
            throw new NegocioException("Repertório publicado não pode ser excluído.");
        }
        for (RepertorioItem item : repertorioItemRepository.findByRepertorio_Id(repertorio.getId())) {
            if (Boolean.TRUE.equals(item.getAtivo())) {
                item.setAtivo(Boolean.FALSE);
                repertorioItemRepository.save(item);
            }
        }
    }
}
