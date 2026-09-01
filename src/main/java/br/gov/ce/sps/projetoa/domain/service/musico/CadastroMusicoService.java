package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.api.input.MusicoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import br.gov.ce.sps.projetoa.domain.service.instrumento.GetInstrumentoService;
import br.gov.ce.sps.projetoa.domain.service.usuario.GetUsuarioService;
import br.gov.ce.sps.projetoa.infrastructure.util.TelefoneUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CadastroMusicoService {

    private final MusicoRepository musicoRepository;
    private final GetUsuarioService getUsuarioService;
    private final GetInstrumentoService getInstrumentoService;

    @Transactional
    public Musico salvar(MusicoInput input) {
        Musico musico = new Musico();
        aplicarDados(musico, input, null);
        if (musico.getAtivo() == null) {
            musico.setAtivo(Boolean.TRUE);
        }
        return musicoRepository.save(musico);
    }

    void aplicarDados(Musico musico, MusicoInput input, Long musicoIdIgnorar) {
        if (!StringUtils.hasText(input.getNome())) {
            throw new NegocioException("Informe o nome do músico.");
        }

        String whatsapp = TelefoneUtils.normalizar(input.getWhatsapp());
        TelefoneUtils.validarWhatsapp(whatsapp);
        garantirWhatsappUnico(whatsapp, musicoIdIgnorar);

        String telefone = TelefoneUtils.normalizar(input.getTelefone());
        TelefoneUtils.validarTelefoneOpcional(telefone);

        musico.setNome(input.getNome().trim());
        musico.setNomeArtistico(blankToNull(input.getNomeArtistico()));
        musico.setTelefone(telefone);
        musico.setWhatsapp(whatsapp);
        musico.setEmail(blankToNull(input.getEmail()));
        musico.setObservacao(blankToNull(input.getObservacao()));
        if (input.getAtivo() != null) {
            musico.setAtivo(input.getAtivo());
        }

        vincularUsuario(musico, input.getUsuarioCodigo(), musicoIdIgnorar);
        vincularInstrumentos(musico, input.getInstrumentosCodigos());
    }

    private void garantirWhatsappUnico(String whatsapp, Long musicoIdIgnorar) {
        boolean duplicado = musicoIdIgnorar == null
                ? musicoRepository.existsByWhatsapp(whatsapp)
                : musicoRepository.existsByWhatsappAndIdNot(whatsapp, musicoIdIgnorar);
        if (duplicado) {
            throw new NegocioException("Já existe um músico cadastrado com este WhatsApp.");
        }
    }

    private void vincularUsuario(Musico musico, UUID usuarioCodigo, Long musicoIdIgnorar) {
        if (usuarioCodigo == null) {
            musico.setUsuario(null);
            return;
        }

        Usuario usuario = getUsuarioService.findByCode(usuarioCodigo);
        boolean duplicado = musicoIdIgnorar == null
                ? musicoRepository.existsByUsuario_Id(usuario.getId())
                : musicoRepository.existsByUsuario_IdAndIdNot(usuario.getId(), musicoIdIgnorar);
        if (duplicado) {
            throw new NegocioException("Este usuário já está vinculado a outro músico.");
        }
        musico.setUsuario(usuario);
    }

    private void vincularInstrumentos(Musico musico, List<UUID> instrumentosCodigos) {
        if (instrumentosCodigos == null) {
            if (musico.getId() == null) {
                musico.setInstrumentos(new HashSet<>());
            }
            return;
        }
        List<Instrumento> selecionados = getInstrumentoService.findAllByUUID(instrumentosCodigos);
        Set<Long> jaVinculados = musico.getInstrumentos() == null
                ? Set.of()
                : musico.getInstrumentos().stream().map(Instrumento::getId).collect(Collectors.toSet());
        for (Instrumento instrumento : selecionados) {
            boolean novoVinculo = instrumento.getId() == null || !jaVinculados.contains(instrumento.getId());
            if (novoVinculo && !Boolean.TRUE.equals(instrumento.getAtivo())) {
                throw new NegocioException("Não é possível vincular o instrumento inativo: " + instrumento.getNome() + ".");
            }
        }
        musico.setInstrumentos(new HashSet<>(selecionados));
    }

    private static String blankToNull(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        return valor.trim();
    }
}
