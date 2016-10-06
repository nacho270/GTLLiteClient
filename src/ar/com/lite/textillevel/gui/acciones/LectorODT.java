package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;

import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;

public class LectorODT extends AbstractDialogLectorCodigo<OrdenDeTrabajo> {

	private static final long serialVersionUID = 244330423364916129L;

	private Frame owner;

	public LectorODT(Frame owner) {
		super(owner, "ODT");
		this.owner = owner;
	}

	@Override
	protected OrdenDeTrabajo buscar(String codigo) throws Exception {
		return GTLLiteRemoteService.getODTByCodigo(codigo);
	}

	@Override
	protected void encontrado(OrdenDeTrabajo odt) {
		JDialogEditarPiezasODT jDialogEditarPiezasODT = new JDialogEditarPiezasODT(owner, odt);
		GuiUtil.centrar(jDialogEditarPiezasODT);
		jDialogEditarPiezasODT.setVisible(true);		
	}

}