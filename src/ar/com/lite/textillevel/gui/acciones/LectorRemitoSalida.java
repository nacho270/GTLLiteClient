package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;

import ar.com.textillevel.entidades.documentos.remito.RemitoSalida;

public class LectorRemitoSalida extends AbstractDialogLectorCodigo<RemitoSalida> {

	private static final long serialVersionUID = 244330423364916129L;

	public LectorRemitoSalida(Frame owner, DialogoLectorRemitoSalidaCallback  callback) {
		super(owner, "Lector de Rem. Salida", "Rem. Salida", callback);
	}

}