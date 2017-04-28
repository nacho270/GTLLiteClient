package main.acciones;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.gui.acciones.procesosector.JDialogProcesarEnSector;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;

public abstract class ProcesarConModuloProcesoODTEnSectorAbstractAction implements Action {

	private Frame frame;
	private ESectorMaquina sector;

	public ProcesarConModuloProcesoODTEnSectorAbstractAction(Frame frame, ESectorMaquina sector) {
		if(!sector.isAdmiteInterProcesamiento()) {
			throw new IllegalArgumentException("No se puede implementar este módulo con el sector " + sector  + "(ver atributo " + sector + ".admiteInterProcesamiento)");
		}
		this.frame = frame;
		this.sector = sector;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		
	}

	public Object getValue(String key) {
		return null;
	}

	public boolean isEnabled() {
		return true;
	}

	public void putValue(String key, Object value) {
		
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		
	}

	public void setEnabled(boolean b) {
		
	}

	public void actionPerformed(ActionEvent e) {
		JDialogProcesarEnSector dialog = new JDialogProcesarEnSector(frame, sector);
		GuiUtil.centrarEnFramePadre(dialog);
		dialog.setVisible(true);
	}

	protected ESectorMaquina getSector() {
		return sector;
	}

}