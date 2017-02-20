package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;

import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.gui.util.JDialogPasswordInput;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.lite.textillevel.util.GTLLiteRemoteService.GTLLiteBeanFactory;
import ar.com.textillevel.entidades.documentos.remito.RemitoSalida;
import ar.com.textillevel.entidades.portal.UsuarioSistema;
import ar.com.textillevel.facade.api.remote.UsuarioSistemaFacadeRemote;
import main.GTLLiteGlobalCache;

public class DialogoLectorRemitoSalidaCallback implements DialogLectorCodigoCallback<RemitoSalida> {

	private Frame owner;
	private String idSistema;

	public DialogoLectorRemitoSalidaCallback(Frame owner) {
		this.owner = owner;
	}

	@Override
	public RemitoSalida buscar(String codigo) throws Exception {
		RemitoSalida remitoSalidaByCodigo = GTLLiteRemoteService.getRemitoSalidaByCodigo(codigo);
		if(remitoSalidaByCodigo != null) {//se encontró => guardo de que sistema venía!
			idSistema = codigo.substring(codigo.length()-1, codigo.length());
		} else {
			idSistema = null;
		}
		return remitoSalidaByCodigo;
	}

	@Override
	public void encontrado(RemitoSalida rs) {
		JDialogEntregaMercaderiaRemitoSalida jDialogEntregaMercaderiaRS = new JDialogEntregaMercaderiaRemitoSalida(owner, rs, idSistema);
		GuiUtil.centrar(jDialogEntregaMercaderiaRS);
		jDialogEntregaMercaderiaRS.setVisible(true);
	}

	@Override
	public String validar(RemitoSalida rs) {
		if(rs.getControlado() != null && rs.getControlado()) {
			if(GTLLiteGlobalCache.getInstance().getUsuarioSistema().getPerfil().getIsAdmin()) {
				return null;
			} else {
				UsuarioSistema usrAdmin = null;
				do {
					JDialogPasswordInput jDialogPasswordInput = new JDialogPasswordInput(owner, "Lectura de Remito");
					boolean acepto = jDialogPasswordInput.isAcepto();
					if (!acepto) {
						return "Operación Cancelada";
					}
					String pass = new String(jDialogPasswordInput.getPassword());
					usrAdmin = GTLLiteBeanFactory.getInstance().getBean2(UsuarioSistemaFacadeRemote.class).esPasswordDeAdministrador(pass);
					if (usrAdmin == null) {
						FWJOptionPane.showErrorMessage(owner, "La clave ingresada no peternece a un usuario administrador", "Error");
					}
				} while (usrAdmin == null);
			}
		}
		return null;
	}

}