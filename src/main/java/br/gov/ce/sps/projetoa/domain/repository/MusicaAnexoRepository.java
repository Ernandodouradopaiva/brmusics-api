package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.MusicaAnexo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicaAnexoRepository extends CustomJpaRepository<MusicaAnexo, Long> {

    List<MusicaAnexo> findByMusica_Id(Long musicaId);

    boolean existsByMusica_Id(Long musicaId);

    void deleteByMusica_Id(Long musicaId);
}
