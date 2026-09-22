package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.assembler.EscalaAssembler;
import br.gov.ce.sps.projetoa.api.dto.EscalaModel;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus;
import br.gov.ce.sps.projetoa.domain.repository.EscalaMusicoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicarEscalaService {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final GetEscalaService getEscalaService;
    private final EscalaRepository escalaRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final RepertorioRepository repertorioRepository;
    private final RepertorioItemRepository repertorioItemRepository;
    private final CadastroEscalaService cadastroEscalaService;

    @Transactional
    public EscalaModel publicar(UUID codigo) {
        Escala escala = getEscalaService.findByCode(codigo);
        Celebracao celebracao = escala.getCelebracao();
        if (celebracao == null) {
            throw new NegocioException("A escala não possui celebração vinculada.");
        }
        if (celebracao.getStatus() == CelebracaoStatus.CANCELADA) {
            throw new NegocioException("Não é possível publicar a escala de uma celebração cancelada.");
        }
        if (escala.getStatus() == EscalaStatus.PUBLICADA) {
            throw new NegocioException("Esta escala já está publicada.");
        }

        List<EscalaMusico> ativas = escalaMusicoRepository
                .findAtivasComMusicoEInstrumentoByEscalaIdIn(List.of(escala.getId()));

        if (ativas.isEmpty()) {
            throw new NegocioException("A celebração "
                    + celebracao.getTitulo()
                    + " ("
                    + DATA.format(celebracao.getData())
                    + ") não possui escala com músicos.");
        }

        for (EscalaMusico em : ativas) {
            Musico musico = em.getMusico();
            if (musico == null) {
                throw new NegocioException("A celebração "
                        + celebracao.getTitulo()
                        + " possui participação sem músico.");
            }
            if (!Boolean.TRUE.equals(musico.getAtivo())) {
                throw new NegocioException(EscalaAssembler.nomeExibicao(musico)
                        + " está inativo e não pode constar na escala de "
                        + celebracao.getTitulo()
                        + ".");
            }
        }

        List<String> conflitos = cadastroEscalaService.alertasConflito(escala);
        if (!conflitos.isEmpty()) {
            throw new NegocioException(conflitos.getFirst());
        }

        Repertorio repertorio = repertorioRepository.findByCelebracao_Id(celebracao.getId()).orElse(null);
        boolean temRepertorio = repertorio != null
                && !repertorioItemRepository
                .findAtivosComMusicaByRepertorioIdIn(List.of(repertorio.getId()))
                .isEmpty();
        if (!temRepertorio) {
            throw new NegocioException("A celebração "
                    + celebracao.getTitulo()
                    + " ("
                    + DATA.format(celebracao.getData())
                    + ") não possui repertório.");
        }

        escala.setStatus(EscalaStatus.PUBLICADA);
        escalaRepository.save(escala);
        return cadastroEscalaService.toModel(escala, List.of());
    }
}
