package br.gov.ce.sps.projetoa.domain.service.municipio;

import br.gov.ce.sps.projetoa.domain.exception.EntidadeNaoEncontradaException;
import br.gov.ce.sps.projetoa.domain.model.Municipio;
import br.gov.ce.sps.projetoa.domain.repository.MunicipioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMunicipioService {
    
    private final MunicipioRepository municipioRepository;
    
    public Municipio buscarPorId(Long id) {
        return municipioRepository.findById(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Município não encontrado"));
    }
    
    public Municipio buscarPorCodigoIbge(Long codigoIbge) {
        return municipioRepository.findByCodigoIbge(codigoIbge)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Município não encontrado"));
    }
    
    public List<Municipio> listarPorEstado(Long estadoId) {
        return municipioRepository.findByEstadoIdOrderByNomeAsc(estadoId);
    }
    
    public List<Municipio> listarPorEstadoSigla(String sigla) {
        return municipioRepository.findByEstadoUfOrderByNomeAsc(sigla);
    }
}