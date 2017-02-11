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

import org.apache.taglibs.string.util.StringW;

import ar.com.fwcommon.componentes.FWJNumericTextField;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.fwcommon.util.StringUtil;
import ar.com.lite.textillevel.gui.util.GenericUtils;

public abstract class AbstractDialogLectorCodigo<T> extends JDialog {

	private static final long serialVersionUID = -6496099755778688024L;

	private String titulo;
	
	private JPanel panelBotones;
	private JPanel panelGeneral;
	
	private JButton btnSalir;
	private FWJNumericTextField txtCod;

	private DialogLectorCodigoCallback<T> callback;
	private boolean alertMostrandose=false;
	
	public AbstractDialogLectorCodigo(Frame owner, String titulo, final DialogLectorCodigoCallback<T> callback) {
		super(owner);
		this.titulo = titulo;
		this.callback = callback;
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
		int ret = FWJOptionPane.showQuestionMessage(this, "¿Está seguro que desea salir?", "Lector de " + titulo);
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
			txtCod = new FWJNumericTextField(0l, 99999999l);
			Font font1 = new Font("SansSerif", Font.BOLD, 50);
			txtCod.setFont(font1);
			txtCod.setPreferredSize(new Dimension(400, 50));
			txtCod.addKeyListener(new KeyAdapter() {

	            @Override
	            public void keyPressed(final KeyEvent e) {
	                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	                	if(!alertMostrandose) {
		                    if (txtCod.getText().trim().length() == 0) {
		                    	alertMostrandose = true;
		                    	FWJOptionPane.showErrorMessage(AbstractDialogLectorCodigo.this, "Ingrese un código.", "Error");
		                    	requestFocus();
		                        return;
		                    }
		                    finLectura();
		                    return;
	                	}
	                }
                	alertMostrandose = false;
	            }

	            private void finLectura() {
	            	String texto = txtCod.getText();
					if (StringUtil.isNullOrEmptyString(texto)) {
						FWJOptionPane.showErrorMessage(AbstractDialogLectorCodigo.this, "Debe ingresar el código a buscar.", "Error");
                    	reset();
                        return;
	            	}
					if (texto.length() != 8) {
						FWJOptionPane.showErrorMessage(AbstractDialogLectorCodigo.this, "El código de ODT debe contener 8 caracteres.", "Error");
						requestFocus();
						return;
	            	}
	            	try {
	            		T obj = callback.buscar(texto.trim());
	            		if(obj == null) {
	                    	FWJOptionPane.showErrorMessage(AbstractDialogLectorCodigo.this, "El código " + texto.trim() + " es inexistente.", "Error");
	                    	reset();
	                        return;
	            		}
	            		String msgValidacion = callback.validar(obj);
	            		if (!StringUtil.isNullOrEmptyString(msgValidacion)) {
	            			FWJOptionPane.showErrorMessage(AbstractDialogLectorCodigo.this, StringW.wordWrap(msgValidacion), "Error");
	                    	reset();
	                        return;
	            		}
	            		callback.encontrado(obj);
	            		dispose();
                    } catch (final Throwable re) {
                    	re.printStackTrace();
                    	FWJOptionPane.showErrorMessage(AbstractDialogLectorCodigo.this, "Se ha producido un error al comunicarse con el servidor", "Error");
                    }
	            }

	            private void reset() {
	                txtCod.setValue(null);
	                requestFocus();
	            }

				private void requestFocus() {
					txtCod.requestFocus();
	                txtCod.requestFocusInWindow();
				}

	        });
		}
		return txtCod;
	}
}