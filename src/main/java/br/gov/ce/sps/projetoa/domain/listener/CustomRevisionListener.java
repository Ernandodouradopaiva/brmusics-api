package br.gov.ce.sps.projetoa.domain.listener;

import org.hibernate.envers.RevisionListener;

import br.gov.ce.sps.projetoa.core.security.SecurityUtil;
import br.gov.ce.sps.projetoa.domain.model.CustomRevisionEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomRevisionListener implements RevisionListener {

	private final SecurityUtil securityUtil;

	@Override
	public void newRevision(Object revisionEntity) {
		CustomRevisionEntity revision = (CustomRevisionEntity) revisionEntity;
		securityUtil.getAuthenticatedUser().ifPresentOrElse(user -> {
			revision.setLoginUsuario(user.getCpf());
			revision.setNomeUsuario(user.getNome());
		}, () -> {
			revision.setLoginUsuario("NAO_AUTENTICADO");
			revision.setNomeUsuario("NAO_AUTENTICADO");
		});
	}
}