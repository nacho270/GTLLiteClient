package main.acciones;

import java.awt.Frame;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;

public class ProcesarODTEnSectorEstampadoAction extends ProcesarODTEnSectorAbstractAction {

	public ProcesarODTEnSectorEstampadoAction(Frame frame) {
		super(frame, ESectorMaquina.SECTOR_ESTAMPERIA);	
	}

}