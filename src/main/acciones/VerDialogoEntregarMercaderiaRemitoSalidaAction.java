package main.acciones;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

import ar.com.lite.textillevel.gui.acciones.DialogoLectorRemitoSalidaCallback;
import ar.com.lite.textillevel.gui.acciones.LectorRemitoSalida;

public class VerDialogoEntregarMercaderiaRemitoSalidaAction implements Action {

	private Frame frame;
	
	public VerDialogoEntregarMercaderiaRemitoSalidaAction(Frame frame){
		this.frame = frame;
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
		new LectorRemitoSalida(frame, new DialogoLectorRemitoSalidaCallback(frame));
	}

}