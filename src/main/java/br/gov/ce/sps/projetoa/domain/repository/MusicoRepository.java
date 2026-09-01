package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Musico;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MusicoRepository extends CustomJpaRepository<Musico, Long> {

    @Query("SELECT m FROM Musico m LEFT JOIN FETCH m.usuario LEFT JOIN FETCH m.instrumentos WHERE m.codigo = :codigo")
    Optional<Musico> findByCodigo(@Param("codigo") UUID codigo);

    @Query("SELECT DISTINCT m FROM Musico m LEFT JOIN FETCH m.instrumentos WHERE m.id IN :ids")
    List<Musico> findWithInstrumentosByIdIn(@Param("ids") Collection<Long> ids);

    List<Musico> findByCodigoIn(Collection<UUID> codigos);

    boolean existsByInstrumentos_Id(Long instrumentoId);

    boolean existsByWhatsapp(String whatsapp);

    boolean existsByWhatsappAndIdNot(String whatsapp, Long id);

    boolean existsByUsuario_Id(Long usuarioId);

    boolean existsByUsuario_IdAndIdNot(Long usuarioId, Long id);

    @Query("SELECT m FROM Musico m LEFT JOIN FETCH m.usuario WHERE m.usuario.id = :usuarioId")
    Optional<Musico> findByUsuario_Id(@Param("usuarioId") Long usuarioId);
}
