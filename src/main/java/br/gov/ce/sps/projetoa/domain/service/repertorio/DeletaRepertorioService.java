package br.gov.ce.sps.projetoa.domain.service.repertorio;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.model.enums.RepertorioStatus;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletaRepertorioService {

    private final GetRepertorioService getRepertorioService;
    private final RepertorioRepository repertorioRepository;
    private final RepertorioItemRepository repertorioItemRepository;

    @Transactional
    public void deletar(UUID codigo) {
        Repertorio repertorio = getRepertorioService.findByCode(codigo);
        if (repertorio.getStatus() == RepertorioStatus.PUBLICADA) {
            throw new NegocioException("Repertório publicado não pode ser excluído.");
        }
        List<RepertorioItem> itens = repertorioItemRepository.findByRepertorio_Id(repertorio.getId());
        if (!itens.isEmpty()) {
            repertorioItemRepository.deleteAll(itens);
        }
        repertorioRepository.delete(repertorio);
    }
}
