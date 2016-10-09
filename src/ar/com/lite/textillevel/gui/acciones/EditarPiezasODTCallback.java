package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;

import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;

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
		JDialogEditarPiezasODT jDialogEditarPiezasODT = new JDialogEditarPiezasODT(owner, odt);
		GuiUtil.centrar(jDialogEditarPiezasODT);
		jDialogEditarPiezasODT.setVisible(true);
	}
}
