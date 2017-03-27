package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;

import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;

public class LectorODT extends AbstractDialogLectorCodigo<OrdenDeTrabajo> {

	private static final long serialVersionUID = 244330423364916129L;

	public LectorODT(Frame owner, DialogLectorCodigoCallback<OrdenDeTrabajo> callback) {
		this(owner, "Lector de ODT", callback);
	}
	
	public LectorODT(Frame owner, String titulo, DialogLectorCodigoCallback<OrdenDeTrabajo> callback) {
		super(owner, titulo, "ODT", callback);
	}

}