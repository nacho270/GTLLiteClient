package ar.com.lite.textillevel.gui.acciones;

import java.awt.Frame;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;

public class ProcesarODTEnSectorCallback implements DialogLectorCodigoCallback<OrdenDeTrabajo> {

	private ESectorMaquina sector;
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
		GTLLiteRemoteService.grabarAndRegistrarAvanceEnEstadoEnProceso(odt, sector);
		FWJOptionPane.showInformationMessage(owner, "LA ODT " + odt.getCodigo() + " pasó al " + sector, "Información");
	}

	@Override
	public String validar(OrdenDeTrabajo odt) {
		if(odt.getMaquinaActual() == null) {
			return "La ODT no tiene asignada una máquina";
		}
		return null;
	}

}