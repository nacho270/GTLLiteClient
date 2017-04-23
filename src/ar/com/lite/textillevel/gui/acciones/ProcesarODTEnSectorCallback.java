package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;
import javax.swing.JOptionPane;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;

public class ProcesarODTEnSectorCallback implements DialogLectorCodigoCallback<OrdenDeTrabajo> {

	private ESectorMaquina sector;
	@SuppressWarnings("unused")
	private Frame owner;

	public ProcesarODTEnSectorCallback(ESectorMaquina sector, Frame owner) {
		this.sector = sector;
		this.owner = owner;
	}

	@Override
	public OrdenDeTrabajo buscar(String codigo) throws Exception {
		return GTLLiteRemoteService.getODTByCodigo(codigo);
	}

	@Override
	public void encontrado(OrdenDeTrabajo odt) {
		if(odt.getMaquinaActual() != null && odt.getMaquinaActual().getTipoMaquina().getSectorMaquina() == sector) {
			GenericUtils.showTemporaryDialog(3000, "Información", new JOptionPane("LA ODT ya se encuentra en " + sector, JOptionPane.WARNING_MESSAGE));
			return;
		}
		
		GTLLiteRemoteService.grabarAndRegistrarAvanceEnEstadoEnProceso(odt, sector);
		GenericUtils.showTemporaryDialog(3000, "Información", new JOptionPane("LA ODT " + odt.getCodigo() + " pasó al " + sector, JOptionPane.INFORMATION_MESSAGE));
	}

	@Override
	public String validar(OrdenDeTrabajo odt) {
		return null;
	}

}