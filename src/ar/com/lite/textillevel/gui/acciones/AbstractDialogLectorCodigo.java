package ar.com.lite.textillevel.gui.acciones;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ar.com.fwcommon.componentes.FWJNumericTextField;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.gui.util.GenericUtils;

public abstract class AbstractDialogLectorCodigo<T> extends JDialog {

	private static final long serialVersionUID = -6496099755778688024L;

	private String titulo;
	
	private JPanel panelBotones;
	private JPanel panelGeneral;
	
	private JButton btnSalir;
	private FWJNumericTextField txtCod;

	
	public AbstractDialogLectorCodigo(Frame owner, String titulo) {
		super(owner);
		this.titulo = titulo;
		setUpComponentes();
		setUpScreen();
	}

	private void setUpScreen() {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("Lector de " + titulo);
		GuiUtil.centrar(this);
		setResizable(true);
		setModal(true);
		setVisible(true);
	}

	private void setUpComponentes() {
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				salir();
			}
		});
		
		add(getPanGeneral(), BorderLayout.CENTER);
		add(getPanelBotones(),BorderLayout.SOUTH);
		pack();
	}

	
	private JPanel getPanelBotones(){
		if(panelBotones == null){
			panelBotones = new JPanel();
			panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
			panelBotones.add(getBtnSalir());
		}
		return panelBotones;
	}

	private JButton getBtnSalir() {
		if (btnSalir == null) {
			btnSalir = new JButton("Salir");
			btnSalir.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					salir();
				}
			});
		}
		return btnSalir;
	}

	private void salir() {
		int ret = FWJOptionPane.showQuestionMessage(this, "�Est� seguro que desea salir?", "Lector de " + titulo);
		if (ret == FWJOptionPane.YES_OPTION) {
			dispose();
		}
	}

	private JPanel getPanGeneral() {
		if(panelGeneral == null) {
			panelGeneral = new JPanel();
			panelGeneral.setLayout(new GridBagLayout());
			JLabel lblODT = new JLabel(titulo + ": ");
			lblODT.setFont(new Font("SansSerif", Font.BOLD, 50));
			panelGeneral.add(lblODT, GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 5), 1, 1, 0, 0));
			panelGeneral.add(getTxtCodigo(),  GenericUtils.createGridBagConstraints(1, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 5), 1, 1, 1, 0));
		}
		return panelGeneral;
	}

	private Component getTxtCodigo() {
		if(txtCod == null) {
			txtCod = new FWJNumericTextField();
			Font font1 = new Font("SansSerif", Font.BOLD, 50);
			txtCod.setFont(font1);
			txtCod.setPreferredSize(new Dimension(400, 50));
			txtCod.addKeyListener(new KeyAdapter() {

	            @Override
	            public void keyReleased(final KeyEvent e) {
	                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	                    if (txtCod.getText().trim().length() == 0) {
	                    	FWJOptionPane.showErrorMessage(AbstractDialogLectorCodigo.this, "Ingrese un c�digo.", "Error");
	                        return;
	                    }
	                    finLectura();
	                    return;
	                }
	            }

	            private void finLectura() {
	            	try {
	            		T obj = buscar(txtCod.getText().trim());
	            		if(obj == null) {
	                    	FWJOptionPane.showErrorMessage(AbstractDialogLectorCodigo.this, "El c�digo " + txtCod.getText().trim() + " es inexistente.", "Error");
	                    	reset();
	                        return;
	            		}
	            		encontrado(obj);
                    } catch (final Throwable re) {
                    	re.printStackTrace();
                    	FWJOptionPane.showErrorMessage(AbstractDialogLectorCodigo.this, "Se ha producido un error al comunicarse con el servidor", "Error");
                    }
	            }

	            private void reset() {
	                txtCod.setText("");
	                txtCod.requestFocus();
	                txtCod.requestFocusInWindow();
	            }

	        });
		}
		return txtCod;
	}

	protected abstract T buscar(String codigo) throws Exception;
	protected abstract void encontrado(T obj);
}