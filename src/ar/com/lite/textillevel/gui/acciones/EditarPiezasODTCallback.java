package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;

import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.gui.util.JDialogPasswordInput;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.lite.textillevel.util.GTLLiteRemoteService.GTLLiteBeanFactory;
import ar.com.textillevel.entidades.portal.UsuarioSistema;
import ar.com.textillevel.facade.api.remote.UsuarioSistemaFacadeRemote;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;
import main.GTLLiteGlobalCache;

public class EditarPiezasODTCallback implements DialogLectorCodigoCallback<OrdenDeTrabajo> {

	private Frame owner;

	public EditarPiezasODTCallback(Frame owner) {
		this.owner = owner;
	}

	@Override
	public OrdenDeTrabajo buscar(String codigo) throws Exception {
		return GTLLiteRemoteService.getODTByCodigo(codigo);
	}

	@Override
	public void encontrado(OrdenDeTrabajo odt) {
		JDialogAsignarMetrosPiezasODT jDialogEditarPiezasODT = new JDialogAsignarMetrosPiezasODT(owner, odt);
		GuiUtil.centrar(jDialogEditarPiezasODT);
		jDialogEditarPiezasODT.setVisible(true);
	}

	@Override
	public String validar(OrdenDeTrabajo odt) {
		//me fijo si alguna de las piezas tuvo salida		
		for(PiezaODT pODT : odt.getPiezas()) {
			if(pODT.tieneSalida()) {
				return "No se puede asignar metros a las piezas de esta ODT porque a al menos una ya se le dió salida.";
			}
		}

		//me fijo si se pueden asignar metros y si no se puede => solo puede hacerlo un admin o con clave admin 
		if (!odt.puedeAsignarMetrosEnPiezas()) {
			if(GTLLiteGlobalCache.getInstance().getUsuarioSistema().getPerfil().getIsAdmin()) {
				return null;
			} else {
				UsuarioSistema usrAdmin = null;
				do {
					JDialogPasswordInput jDialogPasswordInput = new JDialogPasswordInput(owner, "Lectura de ODT");
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
