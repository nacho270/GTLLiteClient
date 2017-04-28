package ar.com.lite.textillevel.gui.acciones.procesosector;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.taglibs.string.util.StringW;

import ar.com.fwcommon.componentes.FWJNumericTextField;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.enums.EAvanceODT;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;
import ar.com.textillevel.modulos.odt.to.InfoAsignacionMaquinaTO;

public class JDialogProcesarEnSector extends JDialog implements ItemConAccionesSobreODTEventListener {

	private static final long serialVersionUID = 1L;

	private JPanel panDetalle;
	private ItemConAccionesSobreODTList odtListPanel;
	private JPanel pnlBotones;
	private JPanel panelCentral;
	private JButton btnCerrar;
	
	private FWJNumericTextField txtCodODT;
	private ESectorMaquina sector;
	private boolean alertMostrandose=false;
	private JTextArea txtDatosODTProcesandose;
	
	public JDialogProcesarEnSector(Frame owner, ESectorMaquina sector) {
		super(owner);
		this.sector = sector;
		setSize(new Dimension(800, 400));
		setResizable(false);
		setTitle(sector.toString());
		construct();
		setModal(true);

		addWindowListener(new WindowAdapter() {
			   public void windowOpened(WindowEvent e){
				   getTxtCodODT().requestFocusInWindow();
			   }
		});

		getOdtListPanel().updateListadoODTs();
	}

	private void construct() {
		setLayout(new GridBagLayout());
		add(getPanDetalle(), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 1, 1, 0, 1));
		add(getPanelBotones(), GenericUtils.createGridBagConstraints(0, 1,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
	}

	private JPanel getPanDetalle() {
		if(panDetalle == null) {
			panDetalle = new JPanel();
			panDetalle.setLayout(new GridBagLayout());
			
			JLabel lblODT = new JLabel("ODT:");
			lblODT.setFont(new Font("SansSerif", Font.BOLD, 30));

			panDetalle.add(lblODT, GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panDetalle.add(getTxtCodODT(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			panDetalle.add(getPanelCentral(), GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 2, 1, 1, 1));
		}
	
		return panDetalle;
	}

	private JPanel getPanelCentral() {
		if(panelCentral == null) {
			panelCentral = new JPanel();
			panelCentral.setLayout(new GridBagLayout());
			panelCentral.add(getOdtListPanel(), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 1, 1, 0.8, 1));
			JScrollPane sp = new JScrollPane(getTxtDatosODTProcesandose());
			panelCentral.add(sp, GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 1, 1, 0.2, 0));
		}
		return panelCentral;
	}
	
	private FWJNumericTextField getTxtCodODT() {
		if(txtCodODT == null) {
			txtCodODT = new FWJNumericTextField();
			Font font1 = new Font("SansSerif", Font.BOLD, 30);
			txtCodODT.setFont(font1);
			
			txtCodODT.addKeyListener(new KeyAdapter() {

	            @Override
	            public void keyPressed(final KeyEvent e) {
	                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	                	if(!alertMostrandose) {
	                		if (txtCodODT.getText().trim().length() != 8) {
	                			alertMostrandose = true;
	                			FWJOptionPane.showErrorMessage(JDialogProcesarEnSector.this, "Ingrese un código válido.", "Error");
	                			getTxtCodODT().setValue(null);
	                			return;
	                		}
	                		String codidoIngresado = txtCodODT.getText();
	                		OrdenDeTrabajo odt;
							try {
								odt = GTLLiteRemoteService.getODTByCodigo(codidoIngresado);
								if(odt == null) {
									alertMostrandose = true;
									FWJOptionPane.showErrorMessage(JDialogProcesarEnSector.this, "ODT inexistente", "Error");
									getTxtCodODT().setValue(null);        	
									return;
								}
								if(odt.getMaquinaActual().getSector() == sector) {
									FWJOptionPane.showErrorMessage(JDialogProcesarEnSector.this, "La ODT " + txtCodODT.getText() + " ya se encuentra en el sector", "Error");
									getTxtCodODT().setValue(null);
									return;
								}
								if(odt.getEstado() == EEstadoODT.EN_OFICINA || odt.getEstado() == EEstadoODT.FACTURADA) {
									FWJOptionPane.showErrorMessage(JDialogProcesarEnSector.this, "La ODT " + odt.getCodigo() + " ya se encuentra " + odt.getEstado(), "Error");
									getTxtCodODT().setValue(null);
									return;
								}
								
								odtEncontrada(odt);
								getTxtCodODT().setValue(null);
							} catch (RemoteException e1) {
								alertMostrandose = true;
								FWJOptionPane.showErrorMessage(JDialogProcesarEnSector.this, StringW.wordWrap("Error al buscar la ODT: " + e1.getMessage()), "Error");
								getTxtCodODT().setValue(null);        	
								return;
							}
	                	}
	                }
                	alertMostrandose = false;
	            }

	        });
			
		}
		return txtCodODT;
	}

	private void odtEncontrada(OrdenDeTrabajo odt) {
		InfoAsignacionMaquinaTO info = GTLLiteRemoteService.getMaquinaAndProximoOrdenBySector(sector);
		odt.setMaquinaActual(info.getMaquina());
		GTLLiteRemoteService.grabarAndRegistrarCambioEstadoAndAvance(odt, EEstadoODT.EN_PROCESO, EAvanceODT.POR_COMENZAR);
		getOdtListPanel().updateListadoODTs();
		getOdtListPanel().scrollToBottom();

	}

	private JPanel getPanelBotones() {
		if(pnlBotones == null){
			pnlBotones = new JPanel();
			pnlBotones.setLayout(new FlowLayout(FlowLayout.CENTER));
			pnlBotones.add(getBtnCerrar());
			getBtnCerrar().setEnabled(true);
		}
		return pnlBotones;
	}

	private JButton getBtnCerrar() {
		if(btnCerrar == null) {
			btnCerrar = new JButton("Cerrar");
			btnCerrar.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int respuesta = FWJOptionPane.showQuestionMessage(JDialogProcesarEnSector.this, StringW.wordWrap("¿Está seguro que desea salir?"), "Pregunta");
					if (respuesta == FWJOptionPane.YES_OPTION) {
						System.exit(1);
					}
				}

			});

		}
		return btnCerrar;
	}

	private ItemConAccionesSobreODTList getOdtListPanel() {
		if(odtListPanel == null) {
			odtListPanel = new ItemConAccionesSobreODTList(sector, this);
		}
		return odtListPanel;
	}

	private JTextArea getTxtDatosODTProcesandose() {
		if(txtDatosODTProcesandose == null) {
			this.txtDatosODTProcesandose = new JTextArea();
			this.txtDatosODTProcesandose.setEditable(false);
			this.txtDatosODTProcesandose.setFocusable(false);
			
			this.txtDatosODTProcesandose.setPreferredSize(new Dimension(150,150));
			this.txtDatosODTProcesandose.setSize(new Dimension(150,150));
		}
		return txtDatosODTProcesandose;
	}

	public void itemODTChangeAvance(ItemConAccionesSobreODTEvent evt, EAvanceODT avance) {
		if(avance == EAvanceODT.EN_PROCESO) {
			getTxtDatosODTProcesandose().setText(extractDatosODT(evt.getItem().getOdt()));
		} else {
			getTxtDatosODTProcesandose().setText(null);
		}
	}

	private String extractDatosODT(OrdenDeTrabajo odt) {
		StringBuilder sb = new StringBuilder();
		return sb.append("EN PROCESO:")
				.append("\nODT: " + odt.getCodigo())
				.toString();
	}

}