package main.acciones;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

import ar.com.lite.textillevel.gui.acciones.LectorODT;
import ar.com.lite.textillevel.gui.acciones.ProcesarODTEnSectorCallback;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;

public abstract class ProcesarConSoloLeerODTEnSectorAbstractAction implements Action {

	private Frame frame;
	private ESectorMaquina sector;
	
	public ProcesarConSoloLeerODTEnSectorAbstractAction(Frame frame, ESectorMaquina sector) {
		if(sector.isAdmiteInterProcesamiento()) {
			throw new IllegalArgumentException("No se puede implementar este m�dulo con el sector " + sector  + "(ver atributo " + sector + ".admiteInterProcesamiento)");
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
		new LectorODT(frame, sector.toString(), new ProcesarODTEnSectorCallback(sector, frame));
	}

	protected ESectorMaquina getSector() {
		return sector;
	}

}