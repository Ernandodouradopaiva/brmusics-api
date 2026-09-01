package br.gov.ce.sps.projetoa.domain.service.repertorio;

import br.gov.ce.sps.projetoa.api.assembler.RepertorioAssembler;
import br.gov.ce.sps.projetoa.api.dto.RepertorioModel;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import br.gov.ce.sps.projetoa.domain.service.celebracao.GetCelebracaoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetRepertorioService {

    private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de repertório com código %s";

    private final RepertorioRepository repertorioRepository;
    private final GetCelebracaoService getCelebracaoService;
    private final CadastroRepertorioService cadastroRepertorioService;
    private final RepertorioAssembler repertorioAssembler;

    public Repertorio findByCode(UUID codigo) {
        return repertorioRepository.findComCelebracaoByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, codigo)));
    }

    @Transactional(readOnly = true)
    public RepertorioModel buscar(UUID codigo) {
        return cadastroRepertorioService.toModel(findByCode(codigo));
    }

    @Transactional(readOnly = true)
    public RepertorioModel buscarPorCelebracao(UUID celebracaoCodigo) {
        return repertorioRepository.findComCelebracaoByCelebracaoCodigo(celebracaoCodigo)
                .map(cadastroRepertorioService::toModel)
                .orElseGet(() -> {
                    Celebracao celebracao = getCelebracaoService.findByCode(celebracaoCodigo);
                    return repertorioAssembler.toModelSemRepertorio(celebracao);
                });
    }
}
