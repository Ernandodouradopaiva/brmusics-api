package br.gov.ce.sps.projetoa.domain.service.estado;

import br.gov.ce.sps.projetoa.domain.exception.EntidadeNaoEncontradaException;
import br.gov.ce.sps.projetoa.domain.model.Estado;
import br.gov.ce.sps.projetoa.domain.repository.EstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEstadoService {
    
    private final EstadoRepository estadoRepository;
    
    public Estado buscarPorId(Long id) {
        return estadoRepository.findById(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Estado não encontrado"));
    }
    
    public Estado buscarPorSigla(String sigla) {
        return estadoRepository.findByUf(sigla.toUpperCase())
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Estado não encontrado"));
    }
    
    public List<Estado> listarTodos() {
        return estadoRepository.findAllByOrderByNomeAsc();
    }
}