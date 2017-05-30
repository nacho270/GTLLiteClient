package ar.com.lite.textillevel.gui.acciones.procesosector;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

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
import ar.com.textillevel.util.ODTCodigoHelper;

public class JDialogProcesarEnSector extends JDialog implements ItemConAccionesSobreODTEventListener {

	private static final long serialVersionUID = 1L;

	private static final long DOS_MINUTOS = 120000;

	private ItemConAccionesSobreODTList odtListPanel;
	private JPanel panelCentral;
	
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
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			   public void windowOpened(WindowEvent e){
				   getTxtCodODT().requestFocusInWindow();
			   }

			   public void windowClosing(WindowEvent e) {
					int respuesta = FWJOptionPane.showQuestionMessage(JDialogProcesarEnSector.this, StringW.wordWrap("¿Está seguro que desea salir?"), "Pregunta");
					if (respuesta == FWJOptionPane.YES_OPTION) {
						System.exit(1);
					}
			   }
		});

		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				getOdtListPanel().updateListadoODTs();
			}
		}, 0, DOS_MINUTOS);
	}

	private void construct() {
		setLayout(new GridBagLayout());
		JLabel lblODT = new JLabel("ODT:");
		lblODT.setFont(new Font("SansSerif", Font.BOLD, 30));

		JPanel subpanel = new JPanel(new FlowLayout());
		subpanel.add(lblODT);
		subpanel.add(getTxtCodODT());
		
		add(subpanel, GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 5), 1, 1, 0, 0));
		
		add(getPanelCentral(), GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 10, 0, 5), 1, 1, 1, 1));
		
	}

	private JPanel getPanelCentral() {
		if(panelCentral == null) {
			panelCentral = new JPanel();
			panelCentral.setLayout(new GridBagLayout());
			panelCentral.add(getOdtListPanel(), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 10, 5, 5), 1, 1, 0.8, 1));
			JScrollPane sp = new JScrollPane(getTxtDatosODTProcesandose());
			sp.setBorder(BorderFactory.createTitledBorder("EN PROCESO:"));
			panelCentral.add(sp, GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 10, 5, 5), 1, 1, 0.2, 0));
		}
		return panelCentral;
	}
	
	private FWJNumericTextField getTxtCodODT() {
		if(txtCodODT == null) {
			txtCodODT = new FWJNumericTextField();
			
			txtCodODT.setPreferredSize(new Dimension(200, 40));
			txtCodODT.setSize(new Dimension(200, 40));
			
			Font font1 = new Font("SansSerif", Font.BOLD, 30);
			txtCodODT.setFont(font1);
			
			txtCodODT.addKeyListener(new KeyAdapter() {

	            @Override
	            public void keyPressed(final KeyEvent e) {
	                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	                	if(!alertMostrandose) {
//	                		if (txtCodODT.getText().trim().length() != 8) {
//	                			alertMostrandose = true;
//	                			FWJOptionPane.showErrorMessage(JDialogProcesarEnSector.this, "Ingrese un código válido.", "Error");
//	                			getTxtCodODT().setValue(null);
//	                			return;
//	                		}
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
								if(odt.getMaquinaActual() == null) {
									FWJOptionPane.showErrorMessage(JDialogProcesarEnSector.this, StringW.wordWrap("La ODT " + txtCodODT.getText() + " no está en estado " + EEstadoODT.EN_PROCESO + ". Asegúrese de que haya pasado por algún sector anterior."), "Error");
									getTxtCodODT().setValue(null);
									return;
								}
								if(odt.getMaquinaActual() != null && odt.getMaquinaActual().getSector() == sector) {
									FWJOptionPane.showErrorMessage(JDialogProcesarEnSector.this, StringW.wordWrap("La ODT " + txtCodODT.getText() + " ya se encuentra en el sector con estado " + odt.getAvance() + "."), "Error");
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
			
			this.txtDatosODTProcesandose.setPreferredSize(new Dimension(220,150));
			this.txtDatosODTProcesandose.setSize(new Dimension(220,150));
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
		return sb.append("ODT: " + ODTCodigoHelper.getInstance().formatCodigoSinAnioCompleto(odt.getCodigo()))
				.toString();
	}

}