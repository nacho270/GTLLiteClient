package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;

import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.entidades.documentos.remito.RemitoEntrada;

public class LectorRemitoEntrada extends AbstractDialogLectorCodigo<RemitoEntrada> {

	private static final long serialVersionUID = -2895616823852710105L;

	private Frame owner;

	public LectorRemitoEntrada(Frame owner) {
		super(owner, "Remito de entrada");
		this.owner = owner;
	}

	protected RemitoEntrada buscar(String codigo) throws Exception {
		return GTLLiteRemoteService.getRemitoEntradaByNumero(Integer.valueOf(codigo));
	}

	protected void encontrado(RemitoEntrada remito) {
		remito = GTLLiteRemoteService.getByIdEager(remito.getId());
		new JDialogOrdenarPiezasRemitoEntrada(owner, remito).setVisible(true);
	}
}