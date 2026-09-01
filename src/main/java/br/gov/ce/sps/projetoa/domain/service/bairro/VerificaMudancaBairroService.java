package br.gov.ce.sps.projetoa.domain.service.bairro;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificaMudancaBairroService {

    public Boolean verifica(Long id) {
        return id != null;
    }
}