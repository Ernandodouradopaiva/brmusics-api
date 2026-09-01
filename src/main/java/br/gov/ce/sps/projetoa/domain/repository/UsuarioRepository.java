package br.gov.ce.sps.projetoa.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.ce.sps.projetoa.domain.model.Usuario;

@Repository
public interface UsuarioRepository extends CustomJpaRepository<Usuario, Long> {
	Optional<Usuario> findByCodigo(UUID codigoUsuario);
	Optional<Usuario> findByCpf(String cpf);
	Optional<Usuario> findByEmailIgnoreCase(String email);

	@Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.grupos WHERE u.id = :id")
	Optional<Usuario> findByIdWithGrupos(@Param("id") Long id);

	@Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.grupos WHERE u.codigo = :codigo")
	Optional<Usuario> findByCodigoWithGrupos(@Param("codigo") UUID codigo);

	@Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.grupos WHERE u.cpf = :cpf")
	Optional<Usuario> findByCpfWithGrupos(@Param("cpf") String cpf);

	Boolean existsByCodigo(UUID codigo);
	Optional<Usuario> deleteByCodigo(UUID codigo);
}
