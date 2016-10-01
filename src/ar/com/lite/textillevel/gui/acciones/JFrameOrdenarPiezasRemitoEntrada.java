package ar.com.lite.textillevel.gui.acciones;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.util.GuiUtil;

public class JFrameOrdenarPiezasRemitoEntrada extends JFrame {

	private static final long serialVersionUID = -1276054822090966716L;

	public JFrameOrdenarPiezasRemitoEntrada(Frame padre) {
		setSize(new Dimension(1000, 600));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setTitle("Ordenar priezas remito de entrada");
		GuiUtil.centrar(this);
		setExtendedState(MAXIMIZED_BOTH);
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent we) {
				salir();
			}
		});
	}

	private void salir() {
		int ret = FWJOptionPane.showQuestionMessage(this, "Va a cerrar el módulo, esta seguro?", "Lector");
		if (ret == FWJOptionPane.YES_OPTION) {
			dispose();
		}
	}
}
