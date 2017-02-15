package main;

import java.util.Collections;
import java.util.List;

import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.entidades.Modulo;
import ar.com.fwcommon.templates.main.login.FWLoginManager;
import ar.com.lite.textillevel.util.GTLLiteRemoteService.GTLLiteBeanFactory;
import ar.com.textillevel.entidades.portal.UsuarioSistema;
import ar.com.textillevel.facade.api.remote.UsuarioSistemaFacadeRemote;

public class GTLLiteClientLoginManager extends FWLoginManager {

	private static final Integer ID_MODULO_SISTEMA_GTL_LITE = 116;

	public GTLLiteClientLoginManager(int idAplicacion) {
		super(idAplicacion);
	}

	@Override
	public List<Modulo> getModulosAplicacion() throws FWException {
		return Collections.emptyList();
	}

	@Override
	public List<Modulo> getModulosUsuario() throws FWException {
		return Collections.emptyList();
	}

	public boolean login(String usuario, String password) throws FWException {
		UsuarioSistema usuarioSistema = GTLLiteBeanFactory.getInstance().getBean2(UsuarioSistemaFacadeRemote.class).login(usuario, password);
		if(usuarioSistema == null){
			return false;
		}
		for(ar.com.textillevel.entidades.portal.Modulo m : usuarioSistema.getPerfil().getModulos()) {
			if(m.getSistema() != null && m.getSistema() && m.getId().equals(ID_MODULO_SISTEMA_GTL_LITE)) {
				GTLLiteGlobalCache.getInstance().setUsuarioSistema(usuarioSistema);
				return true;
			}
		}
		return false;
	}

}
