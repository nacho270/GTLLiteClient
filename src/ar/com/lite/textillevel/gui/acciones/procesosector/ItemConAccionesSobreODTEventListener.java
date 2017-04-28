package ar.com.lite.textillevel.gui.acciones.procesosector;

import java.util.EventListener;
import ar.com.textillevel.modulos.odt.enums.EAvanceODT;

public interface ItemConAccionesSobreODTEventListener extends EventListener {

	public void itemODTChangeAvance(ItemConAccionesSobreODTEvent evt, EAvanceODT avance);

}
