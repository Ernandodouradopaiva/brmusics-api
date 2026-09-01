package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.api.input.CelebracaoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.service.local.GetLocalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CadastroCelebracaoService {

    private final CelebracaoRepository celebracaoRepository;
    private final GetLocalService getLocalService;

    @Transactional
    public Celebracao salvar(CelebracaoInput input) {
        Celebracao celebracao = new Celebracao();
        aplicarDados(celebracao, input, true);
        if (celebracao.getStatus() == null) {
            celebracao.setStatus(CelebracaoStatus.RASCUNHO);
        }
        return celebracaoRepository.save(celebracao);
    }

    void aplicarDados(Celebracao celebracao, CelebracaoInput input, boolean novoVinculoLocal) {
        if (!StringUtils.hasText(input.getTitulo())) {
            throw new NegocioException("Informe o título da celebração.");
        }
        if (input.getData() == null) {
            throw new NegocioException("Informe a data da celebração.");
        }
        if (input.getHoraInicio() == null) {
            throw new NegocioException("Informe o horário de início da celebração.");
        }
        if (input.getHoraFim() != null && !input.getHoraFim().isAfter(input.getHoraInicio())) {
            throw new NegocioException("O horário de término deve ser posterior ao horário de início.");
        }
        if (input.getLocalCodigo() == null) {
            throw new NegocioException("Informe o local da celebração.");
        }

        Local local = getLocalService.findByCode(input.getLocalCodigo());
        boolean trocouLocal = celebracao.getLocal() == null
                || !local.getId().equals(celebracao.getLocal().getId());
        if ((novoVinculoLocal || trocouLocal) && !Boolean.TRUE.equals(local.getAtivo())) {
            throw new NegocioException("Local inativo não pode ser usado em novas celebrações.");
        }

        celebracao.setLocal(local);
        celebracao.setTitulo(input.getTitulo().trim());
        celebracao.setData(input.getData());
        celebracao.setHoraInicio(input.getHoraInicio());
        celebracao.setHoraFim(input.getHoraFim());
        celebracao.setDescricao(blankToNull(input.getDescricao()));
        celebracao.setObservacao(blankToNull(input.getObservacao()));
        if (input.getStatus() != null) {
            celebracao.setStatus(input.getStatus());
        }
    }

    private static String blankToNull(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        return valor.trim();
    }
}
