package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.api.input.CelebracaoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoTipo;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.service.local.GetLocalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CadastroCelebracaoService {

    private final CelebracaoRepository celebracaoRepository;
    private final GetLocalService getLocalService;

    @Transactional
    public CelebracaoCadastroResultado salvar(CelebracaoInput input) {
        CelebracaoTipo tipo = input.getTipo() == null ? CelebracaoTipo.EXTRAORDINARIA : input.getTipo();
        if (tipo == CelebracaoTipo.FIXA) {
            return salvarSerieFixa(input);
        }
        Celebracao celebracao = new Celebracao();
        aplicarDados(celebracao, input, true);
        celebracao.setTipo(CelebracaoTipo.EXTRAORDINARIA);
        celebracao.setSerieCodigo(null);
        if (celebracao.getStatus() == null) {
            celebracao.setStatus(CelebracaoStatus.RASCUNHO);
        }
        Celebracao salvo = celebracaoRepository.save(celebracao);
        return new CelebracaoCadastroResultado(salvo, 1, null);
    }

    private CelebracaoCadastroResultado salvarSerieFixa(CelebracaoInput input) {
        validarCamposObrigatorios(input);
        Local local = resolverLocalAtivo(input, true, null);
        CelebracaoStatus status = input.getStatus() == null ? CelebracaoStatus.RASCUNHO : input.getStatus();
        UUID serieCodigo = UUID.randomUUID();
        int ano = input.getData().getYear();
        int mes = input.getData().getMonthValue();
        DayOfWeek diaSemana = input.getData().getDayOfWeek();

        List<Celebracao> novas = new ArrayList<>();
        for (LocalDate data : datasDoDiaSemanaNoMes(diaSemana, ano, mes)) {
            if (celebracaoRepository.existsByLocal_IdAndDataAndHoraInicio(
                    local.getId(), data, input.getHoraInicio())) {
                continue;
            }
            Celebracao celebracao = new Celebracao();
            celebracao.setLocal(local);
            celebracao.setTitulo(input.getTitulo().trim());
            celebracao.setData(data);
            celebracao.setHoraInicio(input.getHoraInicio());
            celebracao.setHoraFim(input.getHoraFim());
            celebracao.setDescricao(blankToNull(input.getDescricao()));
            celebracao.setObservacao(blankToNull(input.getObservacao()));
            celebracao.setStatus(status);
            celebracao.setTipo(CelebracaoTipo.FIXA);
            celebracao.setSerieCodigo(serieCodigo);
            novas.add(celebracao);
        }

        if (novas.isEmpty()) {
            throw new NegocioException(
                    "Não foi possível gerar a série: já existem celebrações neste local e horário"
                            + " para todos os "
                            + nomeDiaSemana(diaSemana)
                            + "s de "
                            + String.format("%02d/%d", mes, ano)
                            + ".");
        }

        List<Celebracao> salvas = celebracaoRepository.saveAll(novas);
        Celebracao referencia = salvas.stream()
                .filter(c -> c.getData().equals(input.getData()))
                .findFirst()
                .orElse(salvas.getFirst());
        return new CelebracaoCadastroResultado(referencia, salvas.size(), serieCodigo);
    }

    void aplicarDados(Celebracao celebracao, CelebracaoInput input, boolean novoVinculoLocal) {
        validarCamposObrigatorios(input);
        Local local = resolverLocalAtivo(input, novoVinculoLocal, celebracao);

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
        // tipo e serieCodigo não mudam na edição de uma ocorrência isolada
        if (celebracao.getTipo() == null) {
            celebracao.setTipo(CelebracaoTipo.EXTRAORDINARIA);
        }
    }

    private void validarCamposObrigatorios(CelebracaoInput input) {
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
    }

    private Local resolverLocalAtivo(CelebracaoInput input, boolean novoVinculoLocal, Celebracao existente) {
        Local local = getLocalService.findByCode(input.getLocalCodigo());
        boolean trocouLocal = existente == null
                || existente.getLocal() == null
                || !local.getId().equals(existente.getLocal().getId());
        if ((novoVinculoLocal || trocouLocal) && !Boolean.TRUE.equals(local.getAtivo())) {
            throw new NegocioException("Local inativo não pode ser usado em novas celebrações.");
        }
        return local;
    }

    static List<LocalDate> datasDoDiaSemanaNoMes(DayOfWeek diaSemana, int ano, int mes) {
        LocalDate data = LocalDate.of(ano, mes, 1);
        while (data.getDayOfWeek() != diaSemana) {
            data = data.plusDays(1);
        }
        List<LocalDate> datas = new ArrayList<>();
        while (data.getMonthValue() == mes && data.getYear() == ano) {
            datas.add(data);
            data = data.plusWeeks(1);
        }
        return datas;
    }

    static String nomeDiaSemanaPublico(DayOfWeek dia) {
        return nomeDiaSemana(dia);
    }

    private static String nomeDiaSemana(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "segunda-feira";
            case TUESDAY -> "terça-feira";
            case WEDNESDAY -> "quarta-feira";
            case THURSDAY -> "quinta-feira";
            case FRIDAY -> "sexta-feira";
            case SATURDAY -> "sábado";
            case SUNDAY -> "domingo";
        };
    }

    private static String blankToNull(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        return valor.trim();
    }
}
