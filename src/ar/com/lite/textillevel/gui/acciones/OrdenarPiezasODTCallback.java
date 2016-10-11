package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;

import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;

public class OrdenarPiezasODTCallback implements DialogLectorCodigoCallback<OrdenDeTrabajo> {

	private Frame owner;

	public OrdenarPiezasODTCallback(Frame owner) {
		this.owner = owner;
	}

	@Override
	public OrdenDeTrabajo buscar(String codigo) throws Exception {
		return GTLLiteRemoteService.getODTByCodigo(codigo);
	}

	@Override
	public void encontrado(OrdenDeTrabajo odt) {
		JDialogOrdenarPiezasODT jDialogEditarPiezasODT = new JDialogOrdenarPiezasODT(owner, odt);
		GuiUtil.centrar(jDialogEditarPiezasODT);
		jDialogEditarPiezasODT.setVisible(true);
	}

	@Override
	public String validar(OrdenDeTrabajo odt) {
		EEstadoODT estadoODT = odt.getEstado();
		if (estadoODT.ordinal() >= EEstadoODT.EN_PROCESO.ordinal()) {
			return "No se puede asignar orden a las piezas de esta ODT porque ya se encuentra en proceso.";
		}
		return null;
	}
}
