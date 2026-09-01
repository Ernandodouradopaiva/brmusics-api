package br.gov.ce.sps.projetoa.domain.service.estado;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Estado;
import br.gov.ce.sps.projetoa.domain.repository.EstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CadastroEstadoService {
    
    private final EstadoRepository estadoRepository;
    
    public Estado cadastrar(Estado estado) {
        // Validar se já existe por sigla
        estadoRepository.findByUf(estado.getUf())
            .ifPresent(e -> {
                throw new NegocioException("Já existe um estado cadastrado com a sigla " + estado.getUf());
            });
            
        // Validar se já existe por IBGE
        estadoRepository.findByIbge(estado.getIbge())
            .ifPresent(e -> {
                throw new NegocioException("Já existe um estado cadastrado com o código IBGE " + estado.getIbge());
            });
        
        // Garantir que a sigla está em maiúsculo
        estado.setUf(estado.getUf().toUpperCase());
        estado.setNome(estado.getNome().toUpperCase());
        
        return estadoRepository.save(estado);
    }
}