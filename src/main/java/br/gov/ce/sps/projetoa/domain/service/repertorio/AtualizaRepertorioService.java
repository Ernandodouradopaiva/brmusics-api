package br.gov.ce.sps.projetoa.domain.service.repertorio;

import br.gov.ce.sps.projetoa.api.dto.RepertorioModel;
import br.gov.ce.sps.projetoa.api.input.RepertorioInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtualizaRepertorioService {

    private final GetRepertorioService getRepertorioService;
    private final CadastroRepertorioService cadastroRepertorioService;
    private final RepertorioRepository repertorioRepository;

    @Transactional
    public RepertorioModel atualizar(UUID codigo, RepertorioInput input) {
        Repertorio repertorio = getRepertorioService.findByCode(codigo);
        cadastroRepertorioService.assertPodeAlterar(repertorio);
        if (input.getCelebracaoCodigo() != null
                && repertorio.getCelebracao() != null
                && !input.getCelebracaoCodigo().equals(repertorio.getCelebracao().getCodigo())) {
            throw new NegocioException("Não é permitido transferir o repertório para outra celebração.");
        }
        repertorio.setObservacao(blankToNull(input.getObservacao()));
        repertorioRepository.save(repertorio);
        cadastroRepertorioService.sincronizarItens(repertorio, input.getItens());
        return cadastroRepertorioService.toModel(repertorio);
    }

    private static String blankToNull(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        return valor.trim();
    }
}
