package main.acciones;

import java.awt.Frame;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;

public class ProcesarODTEnSectorSecoAction extends ProcesarConModuloProcesoODTEnSectorAbstractAction {

	public ProcesarODTEnSectorSecoAction(Frame frame) {
		super(frame, ESectorMaquina.SECTOR_SECO);	
	}

}