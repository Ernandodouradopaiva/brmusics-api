package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoTipo;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReplicarCelebracaoMesService {

    private final GetCelebracaoService getCelebracaoService;
    private final CelebracaoRepository celebracaoRepository;

    @Transactional
    public CelebracaoCadastroResultado replicar(UUID codigo, int ano, int mes) {
        if (mes < 1 || mes > 12) {
            throw new NegocioException("Informe um mês válido (1 a 12).");
        }
        Celebracao origem = getCelebracaoService.findByCode(codigo);
        if (origem.getTipo() != CelebracaoTipo.FIXA) {
            throw new NegocioException("Somente celebrações fixas podem ser replicadas para outro mês.");
        }
        Local local = origem.getLocal();
        if (local == null || !Boolean.TRUE.equals(local.getAtivo())) {
            throw new NegocioException("Local inativo não pode ser usado em novas celebrações.");
        }

        DayOfWeek diaSemana = origem.getData().getDayOfWeek();
        UUID serieCodigo = origem.getSerieCodigo();
        if (serieCodigo == null) {
            serieCodigo = UUID.randomUUID();
            origem.setSerieCodigo(serieCodigo);
            celebracaoRepository.save(origem);
        }

        CelebracaoStatus status = origem.getStatus() == null ? CelebracaoStatus.RASCUNHO : origem.getStatus();
        List<Celebracao> novas = new ArrayList<>();
        for (LocalDate data : CadastroCelebracaoService.datasDoDiaSemanaNoMes(diaSemana, ano, mes)) {
            if (celebracaoRepository.existsByLocal_IdAndDataAndHoraInicio(
                    local.getId(), data, origem.getHoraInicio())) {
                continue;
            }
            Celebracao celebracao = new Celebracao();
            celebracao.setLocal(local);
            celebracao.setTitulo(origem.getTitulo());
            celebracao.setData(data);
            celebracao.setHoraInicio(origem.getHoraInicio());
            celebracao.setHoraFim(origem.getHoraFim());
            celebracao.setDescricao(origem.getDescricao());
            celebracao.setObservacao(origem.getObservacao());
            celebracao.setStatus(status);
            celebracao.setTipo(CelebracaoTipo.FIXA);
            celebracao.setSerieCodigo(serieCodigo);
            novas.add(celebracao);
        }

        if (novas.isEmpty()) {
            throw new NegocioException(
                    "Não foi possível replicar: já existem celebrações neste local e horário"
                            + " para todos os "
                            + CadastroCelebracaoService.nomeDiaSemanaPublico(diaSemana)
                            + "s de "
                            + YearMonth.of(ano, mes)
                            + ".");
        }

        List<Celebracao> salvas = celebracaoRepository.saveAll(novas);
        return new CelebracaoCadastroResultado(salvas.getFirst(), salvas.size(), serieCodigo);
    }
}
