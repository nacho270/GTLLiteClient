package ar.com.lite.textillevel.gui.util;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import ar.com.fwcommon.util.GuiUtil;
import ar.com.fwcommon.util.ImageUtil;

public class WaitDialog extends JDialog {

	private static final long serialVersionUID = 7333760082366921670L;

	private static final String PATH = "ar/com/lite/textillevel/imagenes/ajax-loader.gif";
	private static final WaitDialog instance = new WaitDialog();

	private static JLabel lblTexto;
	private JLabel lblWaitingAnimation;

	private WaitDialog() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		add(getLblTexto(), BorderLayout.NORTH);
		add(getLblWaitingAnimation(), BorderLayout.CENTER);
		setSize(new Dimension(250, 120));
		GuiUtil.centrar(this);
		setUndecorated(true);
	}

	public static void startWait(String texto) {
		getLblTexto().setText("\n" + texto);
		instance.setVisible(true);
	}

	public static void stopWait() {
		instance.setVisible(false);
	}

	public static JLabel getLblTexto() {
		if (lblTexto == null) {
			lblTexto = new JLabel(" ", JLabel.CENTER);
		}
		return lblTexto;
	}

	public JLabel getLblWaitingAnimation() {
		if (lblWaitingAnimation == null) {
			final Icon icon = ImageUtil.loadIcon(PATH);
			lblWaitingAnimation = new JLabel(new ImageIcon(ImageUtil.scale(ImageUtil.iconToImage(icon), 50, 50, true)));
		}
		return lblWaitingAnimation;
	}
}