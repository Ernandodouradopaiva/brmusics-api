package br.gov.ce.sps.projetoa.domain.service.frequencia;

import br.gov.ce.sps.projetoa.api.assembler.EscalaAssembler;
import br.gov.ce.sps.projetoa.api.dto.FrequenciaMensalPreviaModel;
import br.gov.ce.sps.projetoa.api.input.FrequenciaMensalSalvarInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.FrequenciaMensal;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.repository.FrequenciaMensalRepository;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FrequenciaMensalService {

    private static final Locale PT = Locale.forLanguageTag("pt-BR");

    private final FrequenciaMensalRepository frequenciaMensalRepository;
    private final MusicoRepository musicoRepository;

    @Transactional(readOnly = true)
    public FrequenciaMensalPreviaModel listarMensal(int ano, int mes) {
        validarPeriodo(ano, mes);
        List<Musico> musicos = musicoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream()
                .filter(m -> Boolean.TRUE.equals(m.getAtivo()))
                .toList();
        Map<Long, FrequenciaMensal> porMusico = frequenciaMensalRepository.findByAnoAndMesComMusico(ano, mes)
                .stream()
                .collect(Collectors.toMap(f -> f.getMusico().getId(), Function.identity(), (a, b) -> a, LinkedHashMap::new));

        FrequenciaMensalPreviaModel model = new FrequenciaMensalPreviaModel();
        model.setAno(ano);
        model.setMes(mes);
        model.setCompetencia(competencia(ano, mes));
        List<FrequenciaMensalPreviaModel.FrequenciaMensalItemModel> itens = new ArrayList<>();
        for (Musico musico : musicos) {
            FrequenciaMensal existente = porMusico.get(musico.getId());
            FrequenciaMensalPreviaModel.FrequenciaMensalItemModel item =
                    new FrequenciaMensalPreviaModel.FrequenciaMensalItemModel();
            item.setMusicoCodigo(musico.getCodigo());
            item.setMusicoNome(EscalaAssembler.nomeExibicao(musico));
            item.setMusicoAtivo(musico.getAtivo());
            if (existente != null) {
                item.setCodigo(existente.getCodigo());
                item.setSemana1(existente.getSemana1());
                item.setSemana2(existente.getSemana2());
                item.setSemana3(existente.getSemana3());
                item.setSemana4(existente.getSemana4());
            }
            itens.add(item);
        }
        model.setItens(itens);
        return model;
    }

    @Transactional
    public FrequenciaMensalPreviaModel salvarMensal(FrequenciaMensalSalvarInput input) {
        validarPeriodo(input.ano(), input.mes());
        List<UUID> codigos = input.itens().stream()
                .map(FrequenciaMensalSalvarInput.Item::musicoCodigo)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<UUID, Musico> musicos = musicoRepository.findByCodigoIn(codigos).stream()
                .collect(Collectors.toMap(Musico::getCodigo, Function.identity(), (a, b) -> a));

        for (FrequenciaMensalSalvarInput.Item item : input.itens()) {
            Musico musico = musicos.get(item.musicoCodigo());
            if (musico == null) {
                throw new NegocioException("Músico não encontrado: " + item.musicoCodigo());
            }
            FrequenciaMensal entidade = frequenciaMensalRepository
                    .findByMusico_IdAndAnoAndMes(musico.getId(), input.ano(), input.mes())
                    .orElseGet(FrequenciaMensal::new);
            entidade.setMusico(musico);
            entidade.setAno(input.ano());
            entidade.setMes(input.mes());
            entidade.setSemana1(item.semana1());
            entidade.setSemana2(item.semana2());
            entidade.setSemana3(item.semana3());
            entidade.setSemana4(item.semana4());
            frequenciaMensalRepository.save(entidade);
        }
        return listarMensal(input.ano(), input.mes());
    }

    private static void validarPeriodo(int ano, int mes) {
        if (mes < 1 || mes > 12) {
            throw new NegocioException("Informe um mês válido (1 a 12).");
        }
        if (ano < 2000 || ano > 2100) {
            throw new NegocioException("Informe um ano válido.");
        }
    }

    private static String competencia(int ano, int mes) {
        YearMonth ym = YearMonth.of(ano, mes);
        String nome = ym.getMonth().getDisplayName(TextStyle.FULL, PT);
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1) + "/" + ano;
    }
}
