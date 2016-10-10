package main.acciones;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

import ar.com.lite.textillevel.gui.acciones.LectorODT;
import ar.com.lite.textillevel.gui.acciones.OrdenarPiezasODTCallback;

public class VerDialogoOrdenarPiezasODTAction implements Action {

	private Frame frame;
	
	public VerDialogoOrdenarPiezasODTAction(Frame frame){
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
		new LectorODT(frame, new OrdenarPiezasODTCallback(frame));
	}
}