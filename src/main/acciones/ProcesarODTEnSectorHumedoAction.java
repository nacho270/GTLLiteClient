package main.acciones;

import java.awt.Frame;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;

public class ProcesarODTEnSectorHumedoAction extends ProcesarConSoloLeerODTEnSectorAbstractAction {

	public ProcesarODTEnSectorHumedoAction(Frame frame) {
		super(frame, ESectorMaquina.SECTOR_HUMEDO);
	}

}