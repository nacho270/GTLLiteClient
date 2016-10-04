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
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ar.com.fwcommon.componentes.FWJNumericTextField;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;

public class LectorODT extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel panelBotones;
	private JPanel panelGeneral;
	
	private JButton btnSalir;
	private FWJNumericTextField txtCodODT;

	public LectorODT(Frame owner) {
		super(owner);
		setUpComponentes();
		setUpScreen();
	}

	private void setUpScreen() {
		setSize(new Dimension(600, 280));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("Lector de ODT");
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
		int ret = FWJOptionPane.showQuestionMessage(this, "¿Está seguro que desea salir?", "Lector de ODT");
		if (ret == FWJOptionPane.YES_OPTION) {
			dispose();
		}
	}

	private JPanel getPanGeneral() {
		if(panelGeneral == null) {
			panelGeneral = new JPanel();
			panelGeneral.setLayout(new GridBagLayout());
			JLabel lblODT = new JLabel("ODT: ");
			lblODT.setFont(new Font("SansSerif", Font.BOLD, 50));
			panelGeneral.add(lblODT, GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 5), 1, 1, 0, 0));
			panelGeneral.add(getTxtCodODT(),  GenericUtils.createGridBagConstraints(1, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 5), 1, 1, 1, 0));
		}
		return panelGeneral;
	}

	private Component getTxtCodODT() {
		if(txtCodODT == null) {
			txtCodODT = new FWJNumericTextField();
			Font font1 = new Font("SansSerif", Font.BOLD, 50);
			txtCodODT.setFont(font1);
			
			txtCodODT.addKeyListener(new KeyAdapter() {

	            @Override
	            public void keyReleased(final KeyEvent e) {
	                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	                    if (txtCodODT.getText().trim().length() == 0) {
	                    	FWJOptionPane.showErrorMessage(LectorODT.this, "No se ha leido el código", "Error");
	                        return;
	                    }
	                    finLectura();
	                    return;
	                }
	            }

	            private void finLectura() {
	            	try {
	            		OrdenDeTrabajo odt = GTLLiteRemoteService.getODTByCodigo(txtCodODT.getText().trim());
	            		if(odt == null) {
	                    	FWJOptionPane.showErrorMessage(LectorODT.this, "El código " + txtCodODT.getText().trim() + " es inexistente.", "Error");
	                    	reset();
	                        return;
	            		}
	            		System.out.println("Se leyó la ODT " + odt.getId());
	            		//TODO: Levantar el dialogo acá...
                    } catch (final RemoteException re) {
                    	FWJOptionPane.showErrorMessage(LectorODT.this, "Se ha producido un error al comunicarse con el servidor", "Error");
                    }
	            }

	            private void reset() {
	                txtCodODT.setText("");
	                txtCodODT.requestFocus();
	                txtCodODT.requestFocusInWindow();
	            }

	        });
		}
		return txtCodODT;
	}

}