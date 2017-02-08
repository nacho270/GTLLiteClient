package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;

import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.entidades.documentos.remito.RemitoSalida;

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
			return "No se puede realizar esta operación, el remito ya fue controlado.";
		}
		return null;
	}

}