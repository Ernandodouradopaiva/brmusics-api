package br.gov.ce.sps.projetoa.config;

import br.gov.ce.sps.projetoa.api.dto.CelebracaoCadastroModel;
import br.gov.ce.sps.projetoa.api.dto.CelebracaoModelBasico;
import br.gov.ce.sps.projetoa.api.dto.EstadoModelBasico;
import br.gov.ce.sps.projetoa.api.dto.InstrumentoModelBasico;
import br.gov.ce.sps.projetoa.api.dto.MusicaModelBasico;
import br.gov.ce.sps.projetoa.api.dto.MusicoModelBasico;
import br.gov.ce.sps.projetoa.api.input.EstadoInput;
import br.gov.ce.sps.projetoa.api.input.GrupoInput;
import br.gov.ce.sps.projetoa.domain.model.CategoriasLiturgicas;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Estado;
import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);

		TypeMap<GrupoInput, Grupo> grupoInputMap = modelMapper.emptyTypeMap(GrupoInput.class, Grupo.class);
		grupoInputMap.addMappings(mapper -> {
			mapper.skip(Grupo::setId);
			mapper.skip(Grupo::setCodigo);
			mapper.skip(Grupo::setPermissoes);
		});
		grupoInputMap.implicitMappings();

		// ExpressionMap evita PropertyMap (ASM interno não suporta bytecode Java 23)
		modelMapper.typeMap(EstadoInput.class, Estado.class)
				.addMappings(mapper -> mapper.map(EstadoInput::getSigla, Estado::setUf));

		modelMapper.typeMap(Estado.class, EstadoModelBasico.class)
				.addMappings(mapper -> mapper.map(Estado::getUf, EstadoModelBasico::setSigla));

		modelMapper.typeMap(Musico.class, MusicoModelBasico.class)
				.addMappings(mapper -> {
					mapper.skip(MusicoModelBasico::setUsuarioCodigo);
					mapper.skip(MusicoModelBasico::setUsuarioNome);
					mapper.skip(MusicoModelBasico::setInstrumentos);
				})
				.setPostConverter(context -> {
					Musico origem = context.getSource();
					MusicoModelBasico destino = context.getDestination();
					if (origem.getUsuario() != null) {
						destino.setUsuarioCodigo(origem.getUsuario().getCodigo());
						destino.setUsuarioNome(origem.getUsuario().getNome());
					}
					if (origem.getInstrumentos() != null) {
						destino.setInstrumentos(origem.getInstrumentos().stream()
								.sorted((a, b) -> {
									int ordemA = a.getOrdem() != null ? a.getOrdem() : 0;
									int ordemB = b.getOrdem() != null ? b.getOrdem() : 0;
									int cmp = Integer.compare(ordemA, ordemB);
									if (cmp != 0) {
										return cmp;
									}
									String nomeA = a.getNome() != null ? a.getNome() : "";
									String nomeB = b.getNome() != null ? b.getNome() : "";
									return nomeA.compareToIgnoreCase(nomeB);
								})
								.map(instrumento -> modelMapper.map(instrumento, InstrumentoModelBasico.class))
								.toList());
					}
					return destino;
				});

		modelMapper.typeMap(Celebracao.class, CelebracaoModelBasico.class)
				.addMappings(mapper -> {
					mapper.skip(CelebracaoModelBasico::setLocalCodigo);
					mapper.skip(CelebracaoModelBasico::setLocalNome);
				})
				.setPostConverter(context -> {
					Celebracao origem = context.getSource();
					CelebracaoModelBasico destino = context.getDestination();
					if (origem.getLocal() != null) {
						destino.setLocalCodigo(origem.getLocal().getCodigo());
						destino.setLocalNome(origem.getLocal().getNome());
					}
					return destino;
				});

		modelMapper.typeMap(Celebracao.class, CelebracaoCadastroModel.class)
				.addMappings(mapper -> {
					mapper.skip(CelebracaoCadastroModel::setLocalCodigo);
					mapper.skip(CelebracaoCadastroModel::setLocalNome);
				})
				.setPostConverter(context -> {
					Celebracao origem = context.getSource();
					CelebracaoCadastroModel destino = context.getDestination();
					if (origem.getLocal() != null) {
						destino.setLocalCodigo(origem.getLocal().getCodigo());
						destino.setLocalNome(origem.getLocal().getNome());
					}
					return destino;
				});

		modelMapper.typeMap(Musica.class, MusicaModelBasico.class)
				.addMappings(mapper -> mapper.skip(MusicaModelBasico::setCategoriaLiturgicaRotulo))
				.setPostConverter(context -> {
					Musica origem = context.getSource();
					MusicaModelBasico destino = context.getDestination();
					destino.setCategoriaLiturgicaRotulo(CategoriasLiturgicas.rotulo(origem.getCategoriaLiturgica()));
					return destino;
				});

		return modelMapper;
	}
}
