package br.gov.ce.sps.projetoa.api.assembler;

import br.gov.ce.sps.projetoa.api.dto.WhatsAppEnvioModel;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.WhatsAppEnvio;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WhatsAppEnvioAssembler {

    public WhatsAppEnvioModel toModel(WhatsAppEnvio envio) {
        WhatsAppEnvioModel model = new WhatsAppEnvioModel();
        model.setCodigo(envio.getCodigo());
        Musico musico = envio.getMusico();
        if (musico != null) {
            model.setMusicoCodigo(musico.getCodigo());
            model.setMusicoNome(EscalaAssembler.nomeExibicao(musico));
        }
        model.setTelefone(envio.getTelefone());
        model.setTipoMensagem(envio.getTipoMensagem());
        if (envio.getTipoMensagem() != null) {
            model.setTipoMensagemRotulo(envio.getTipoMensagem().getRotulo());
        }
        model.setMensagem(envio.getMensagem());
        model.setStatus(envio.getStatus());
        if (envio.getStatus() != null) {
            model.setStatusRotulo(envio.getStatus().getRotulo());
        }
        model.setProviderMessageId(envio.getProviderMessageId());
        model.setTentativas(envio.getTentativas());
        model.setErro(envio.getErro());
        model.setDataSolicitacao(envio.getDataSolicitacao());
        model.setDataEnvio(envio.getDataEnvio());
        return model;
    }

    public List<WhatsAppEnvioModel> toCollectionModel(List<WhatsAppEnvio> envios) {
        if (envios == null) {
            return List.of();
        }
        return envios.stream().map(this::toModel).toList();
    }
}
