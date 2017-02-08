package ar.com.lite.textillevel.gui.acciones;

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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.apache.taglibs.string.util.StringW;

import ar.com.fwcommon.componentes.FWDateField;
import ar.com.fwcommon.componentes.FWJNumericTextField;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.componentes.FWJTable;
import ar.com.fwcommon.componentes.FWJTextField;
import ar.com.fwcommon.componentes.PanelTabla;
import ar.com.fwcommon.util.StringUtil;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.entidades.documentos.remito.PiezaRemito;
import ar.com.textillevel.entidades.documentos.remito.RemitoSalida;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;

public class JDialogEntregaMercaderiaRemitoSalida extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel panDetalle;
	private PanelTablaPieza panTablaPieza;
	private JPanel pnlBotones;
	private JButton btnAceptar;
	private JButton btnCancelar;
	private FWJNumericTextField txtNroRemito;
	
	private FWDateField txtFechaEmision;
	private FWJTextField txtProducto;
	private FWJNumericTextField txtCodPiezaSalida;

	private RemitoSalida rs;
	private String idSistema;
	private JPanel panTotales; 
	private JTextField txtMetros;
	private JTextField txtPiezas;

	private JPanel panelDatosCliente; 

	private JTextField txtNroCliente;
	private JPanel panelDatosFactura;
	private boolean acepto;
	private boolean alertMostrandose=false;

	public JDialogEntregaMercaderiaRemitoSalida(Frame owner, RemitoSalida rs, String idSistema) {
		super(owner);
		this.rs = rs;
		this.idSistema = idSistema;
		setSize(new Dimension(590, 750));
		setTitle("Remito de Salida - Control de Piezas");
		construct();
		setDatos();
		setModal(true);

		addWindowListener(new WindowAdapter() {
			   public void windowOpened(WindowEvent e){
				   getTxtCodPiezaSalida().requestFocusInWindow();
			   }
		});

	}

	private void setDatos() {
		Cliente cliente = rs.getCliente();
		getTxtFechaEmision().setFecha(rs.getFechaEmision());
		getTxtProducto().setText(extractODTCodes(rs.getOdts()));
		if(cliente.getDireccionReal() != null) {
			getTxtNroCliente().setText(cliente.getNroCliente()+"");
		}
		getTxtMetros().setText(rs.getTotalMetros().toString());
		getTxtPiezas().setText(rs.getPiezas().size()+"");
	}

	private String extractODTCodes(List<OrdenDeTrabajo> odts) {
		List<String> odtToStrings = new ArrayList<>();
		for(OrdenDeTrabajo odt : odts) {
			odtToStrings.add(odt.getProductoArticulo().toString());
		}
		return StringUtil.getCadena(odtToStrings, " / ");
	}

	private void construct() {
		setLayout(new GridBagLayout());
		add(getPanDetalle(), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 1, 1, 0, 1));
		add(getPanTotales(), GenericUtils.createGridBagConstraints(0, 1,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
		add(getPanelBotones(), GenericUtils.createGridBagConstraints(0, 2,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
	}

	private JPanel getPanTotales() {
		if(panTotales == null) {
			panTotales = new JPanel();
			panTotales.setLayout(new GridBagLayout());
			panTotales.add(new JLabel("Total Piezas Salida:"), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panTotales.add(getTxtPiezas(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			panTotales.add(new JLabel("Total Metros Salida:"), GenericUtils.createGridBagConstraints(2, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panTotales.add(getTxtMetros(), GenericUtils.createGridBagConstraints(3, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
		}
		return panTotales;
	}

	private JTextField getTxtPiezas() {
		if(txtPiezas == null) {
			txtPiezas = new JTextField();
			txtPiezas.setEditable(false);
		}
		return txtPiezas;
	}

	private JTextField getTxtMetros() {
		if(txtMetros == null) {
			txtMetros = new JTextField();
			txtMetros.setEditable(false);
		}
		return txtMetros;
	}

	private JPanel getPanDetalle() {
		if(panDetalle == null) {
			panDetalle = new JPanel();
			panDetalle.setLayout(new GridBagLayout());
			
			panDetalle.add(getPanelDatosCliente(), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 6, 1, 0, 0));

			panDetalle.add(getPanelDatosFactura(), GenericUtils.createGridBagConstraints(0, 1,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 6, 1, 0, 0));

			JLabel lblNroPieza = new JLabel("NRO. de PIEZA:");
			lblNroPieza.setFont(new Font("SansSerif", Font.BOLD, 20));

			panDetalle.add(lblNroPieza, GenericUtils.createGridBagConstraints(0, 2,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panDetalle.add(getTxtCodPiezaSalida(), GenericUtils.createGridBagConstraints(1, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));

			panDetalle.add(getPanTablaPieza(), GenericUtils.createGridBagConstraints(0, 3, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 6, 1, 1, 1));
		}
	
		return panDetalle;
	}

	private JPanel getPanelDatosCliente() {
		if(panelDatosCliente == null){
			panelDatosCliente = new JPanel();
			panelDatosCliente.setLayout(new GridBagLayout());
			panelDatosCliente.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			panelDatosCliente.add(new JLabel("Cliente: "), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtNroCliente(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
		}
		return panelDatosCliente;
	}

	private JTextField getTxtNroCliente() {
		if(txtNroCliente == null) {
			txtNroCliente = new JTextField();
			txtNroCliente.setEditable(false);
		}
		return txtNroCliente;
	}

	private JPanel getPanelDatosFactura() {
		if(panelDatosFactura == null){
			panelDatosFactura = new JPanel();
			panelDatosFactura.setLayout(new GridBagLayout());
			panelDatosFactura.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			panelDatosFactura.add(new JLabel("Remito Nº: "), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtNroRemito(), GenericUtils.createGridBagConstraints(1, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			panelDatosFactura.add(new JLabel("Fecha:"), GenericUtils.createGridBagConstraints(2, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtFechaEmision(), GenericUtils.createGridBagConstraints(3, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			
			panelDatosFactura.add(new JLabel("ODT:"), GenericUtils.createGridBagConstraints(0, 1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtProducto(), GenericUtils.createGridBagConstraints(1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 3, 1, 1, 0));
			
		}
		return panelDatosFactura;
	}

	private FWJTextField getTxtProducto() {
		if(txtProducto == null) {
			txtProducto = new FWJTextField();
			txtProducto.setEditable(false);
		}
		return txtProducto;
	}

	private FWJNumericTextField getTxtCodPiezaSalida() {
		if(txtCodPiezaSalida == null) {
			txtCodPiezaSalida = new FWJNumericTextField();
			Font font1 = new Font("SansSerif", Font.BOLD, 20);
			txtCodPiezaSalida.setFont(font1);
			txtCodPiezaSalida.setPreferredSize(new Dimension(400, 20));
			
			txtCodPiezaSalida.addKeyListener(new KeyAdapter() {

	            @Override
	            public void keyReleased(final KeyEvent e) {
	                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	                	if(!alertMostrandose) {
	                		if (txtCodPiezaSalida.getText().trim().length() != 12) {
	                			alertMostrandose = true;
	                			FWJOptionPane.showErrorMessage(JDialogEntregaMercaderiaRemitoSalida.this, "Ingrese un código válido.", "Error");
	                			getTxtCodPiezaSalida().setValue(null);
	                			return;
	                		}
	                		String codidoIngresado = txtCodPiezaSalida.getText();
	                		PiezaODT piezaODT = GTLLiteRemoteService.getPiezaODTByCodigo(idSistema, codidoIngresado);
	                		if(piezaODT == null) {
	                			alertMostrandose = true;
	                			FWJOptionPane.showErrorMessage(JDialogEntregaMercaderiaRemitoSalida.this, "Pieza inexistente", "Error");
	                			getTxtCodPiezaSalida().setValue(null);        	
	                			return;
	                		}
	                		piezaEncontrada(piezaODT);
	                		getTxtCodPiezaSalida().setValue(null);
	                	}
	                }
	                alertMostrandose = false;
	            }

	        });
			
		}
		return txtCodPiezaSalida;
	}
	
	private FWDateField getTxtFechaEmision() {
		if(txtFechaEmision == null) {
			txtFechaEmision = new FWDateField();
			txtFechaEmision.setFecha(rs.getFechaEmision());
			txtFechaEmision.setEditable(false);
		}
		return txtFechaEmision;
	}

	private FWJNumericTextField getTxtNroRemito() {
		if(txtNroRemito == null) {
			txtNroRemito = new FWJNumericTextField(new Long(0), Long.MAX_VALUE);
			getTxtNroRemito().setText(rs.getNroRemito().toString());
		}
		txtNroRemito.setEditable(false);
		return txtNroRemito;
	}

	private JPanel getPanelBotones() {
		if(pnlBotones == null){
			pnlBotones = new JPanel();
			pnlBotones.setLayout(new FlowLayout(FlowLayout.CENTER));
			pnlBotones.add(getBtnAceptar());
			pnlBotones.add(getBtnCancelar());
			
			getBtnCancelar().setEnabled(true);
		}
		return pnlBotones;
	}

	private JButton getBtnCancelar() {
		if(btnCancelar == null) {
			btnCancelar = new JButton("Cancelar");
			btnCancelar.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					acepto = false;
					dispose();
				}

			});

		}
		return btnCancelar;
	}

	private JButton getBtnAceptar() {
		if(btnAceptar == null) {
			btnAceptar = new JButton("Aceptar");
			btnAceptar.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if(validar()) {
						acepto = true;
						GTLLiteRemoteService.marcarRemitoSalidaComoControlado(idSistema, rs);
						dispose();
					}
					return;
				}

			});
		}
		return btnAceptar;
	}

	private boolean validar() {
		String msgValidacionPiezas = getPanTablaPieza().validar();
		if(msgValidacionPiezas != null) {
			alertMostrandose = true;
			FWJOptionPane.showErrorMessage(JDialogEntregaMercaderiaRemitoSalida.this, StringW.wordWrap(msgValidacionPiezas), "Atención");
			getTxtCodPiezaSalida().setValue(null);
			getTxtCodPiezaSalida().requestFocus();
			return false;
		}
		return true;
	}

	private PanelTablaPieza getPanTablaPieza() {
		if(panTablaPieza == null) {
			panTablaPieza = new PanelTablaPieza(rs);
		}
		return panTablaPieza;
	}

	public boolean isAceptpo() {
		return acepto;
	}

	private void piezaEncontrada(PiezaODT piezaODT) {
		getPanTablaPieza().handlePiezaEncontrada(piezaODT);
	}

	private class PanelTablaPieza extends PanelTabla<PiezaRemito> {

		private static final long serialVersionUID = 1L;

		private static final int COL_NRO_PIEZA = 0;
		private static final int COL_METROS_PIEZA_ODT = 1;
		private static final int COL_METROS_PIEZA_SALIDA = 2;
		private static final int COL_ES_DE_2DA = 3;
		private static final int COL_STATUS_CONTROL = 4;
		private static final int COL_OBJ = 5;
		private static final int CANT_COLS = COL_OBJ + 1;

		private JButton btnImprimir;
		private Map<PiezaRemito, Boolean> mapYaControladas = new HashMap<>();
		private Map<PiezaRemito, Boolean> mapReImpresas = new HashMap<>();

		public PanelTablaPieza(RemitoSalida rs) {
			getBotonAgregar().setVisible(false);
			getBotonEliminar().setVisible(false);
			agregarBoton(getBtnImprimir());
			agregarElementos(rs.getPiezas());
		}

		public void handlePiezaEncontrada(PiezaODT piezaODT) {
			int row = findFilaPiezaRSByPiezaODT(piezaODT);
			PiezaRemito pr = getElemento(row);
			mapYaControladas.put(pr, true);
			getTabla().setValueAt(piezaODT.getMetros(), row, COL_METROS_PIEZA_ODT);
			if(pr.getMetros().equals(piezaODT.getMetros())) {//Ok pinto de verde
				getTabla().setValueAt("<html><div style=\"padding:2px 0px; background-color:green\"><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;OK&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></div></html>", row, COL_STATUS_CONTROL);
			} else {//Diferente => pinto de rojo y pregunto si quiere imprimir
				getTabla().setValueAt("<html><body style=\"background-color:red\"><div style=\"padding:2px 0px\"><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NO OK &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></div></body></html>", row, COL_STATUS_CONTROL);
				alertMostrandose = true;
				if(FWJOptionPane.showQuestionMessage(JDialogEntregaMercaderiaRemitoSalida.this, StringW.wordWrap("Los metros de la pieza difieren con respecto a los metros de salida ¿Desea imprimirla nuevamente?"), "Atención") == FWJOptionPane.YES_OPTION) {
					getPanTablaPieza().handleImprimir(pr.getMetros(), piezaODT);
					return;
				}
			}
		}

		private int findFilaPiezaRSByPiezaODT(PiezaODT piezaODT) {
			for(int row=0; row < getTabla().getRowCount(); row++) {
				PiezaRemito pr = getElemento(row);
				if(pr.getPiezaPadreODT().equals(piezaODT)) {
					return row;
				}
			}
			throw new IllegalArgumentException("La pieza " + piezaODT + " no es compatible con el remito de salida " + rs);
		}

		public String validar() {
			if(!todasFueronControladas()) {
				return "Faltan controlar piezas.";
			}
			if(!todasLasErroneasFueronReImpresas()) {
				return "Existen piezas con metros diferentes a los de salida que no fueron re impresas";
			}
			return null;
		}

		@Override
		protected void agregarElemento(PiezaRemito elemento) {
			Object[] row = getRow(elemento);
			getTabla().addRow(row);
		}

		private Object[] getRow(PiezaRemito pr) {
			PiezaODT elemento = pr.getPiezaPadreODT();
			Object[] row = new Object[CANT_COLS];
			row[COL_NRO_PIEZA] = elemento.toString();
			row[COL_METROS_PIEZA_SALIDA] = pr.getMetros();
			row[COL_METROS_PIEZA_ODT] = null;
			row[COL_STATUS_CONTROL] = "<html><div style=\"padding:2px 0px; background-color:gray\"><b>PENDIENTE</b></div></html>";
			row[COL_ES_DE_2DA] = elemento.getEsDeSegunda() != null && elemento.getEsDeSegunda();
			row[COL_OBJ] = pr;
			return row;
		}

		@Override
		protected FWJTable construirTabla() {

			FWJTable tablaPiezaEntrada = new FWJTable(0, CANT_COLS) {

				private static final long serialVersionUID = 1L;

				@Override
				public void newRowSelected(int newRow, int oldRow) {
					if(newRow != -1) {
						getBtnImprimir().setEnabled(true);
					} else {
						getBtnImprimir().setEnabled(false);
					}
				}

			};
			
			tablaPiezaEntrada.setStringColumn(COL_NRO_PIEZA, "NRO. DE PIEZA", 120, 120, true);
			tablaPiezaEntrada.setAlignment(COL_NRO_PIEZA, FWJTable.CENTER_ALIGN);
			
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ODT, "METROS ODT", 100, true);
			tablaPiezaEntrada.setAlignment(COL_METROS_PIEZA_ODT, FWJTable.CENTER_ALIGN);

			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_SALIDA, "METROS SALIDA", 100, true);
			tablaPiezaEntrada.setAlignment(COL_METROS_PIEZA_SALIDA, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setCheckColumn(COL_ES_DE_2DA, "2DA", 40, true);
			tablaPiezaEntrada.setHeaderAlignment(COL_ES_DE_2DA, FWJTable.CENTER_ALIGN);
			
			tablaPiezaEntrada.setMultilineColumn(COL_STATUS_CONTROL, "Control", 65, true, true);			
			tablaPiezaEntrada.setAlignment(COL_STATUS_CONTROL, FWJTable.CENTER_ALIGN);

			tablaPiezaEntrada.setStringColumn(COL_OBJ, "", 0, 0, true);
			tablaPiezaEntrada.setSelectionMode(FWJTable.SINGLE_SELECTION);

			return tablaPiezaEntrada;
		}

		public void handleImprimir(BigDecimal metrosSalida, PiezaODT piezaODT) {
			BigDecimal mtsOriganles = piezaODT.getMetros();
			int row = findFilaPiezaRSByPiezaODT(piezaODT);
			PiezaRemito pr = getElemento(row);
			boolean teniaDiferenciaEnMts = !pr.getMetros().equals(pr.getPiezaPadreODT().getMetros());
			if(teniaDiferenciaEnMts) {//cuento las re impresas solo si tienen diferencia en los metros
				mapReImpresas.put(pr, true);
			}
			piezaODT.setMetros(metrosSalida); //solo para que se refleje en la impresión
			new ImprimirCodigoPiezaODTHandler(piezaODT).imprimir();
			piezaODT.setMetros(mtsOriganles); //undo
			if(teniaDiferenciaEnMts) {
				getTabla().setValueAt("<html><div style=\"padding:2px 0px; background-color:green\"><b>OK-IMPR.&nbsp;&nbsp;&nbsp;&nbsp;</b></div></html>", row, COL_STATUS_CONTROL);
			}
		}

		@Override
		protected void botonQuitarPresionado() {
		}

		@Override
		public boolean validarAgregar() {
			return false;
		}

		@Override
		public boolean validarQuitar() {
			return false;
		}

		@Override
		protected PiezaRemito getElemento(int fila) {
			return (PiezaRemito)getTabla().getValueAt(fila, COL_OBJ);
		}

		@Override
		protected String validarElemento(int fila) {
			return null;
		}

		private JButton getBtnImprimir() {
			if(btnImprimir == null) {
				btnImprimir = new JButton("Imprimir");
				btnImprimir.setEnabled(false);
				btnImprimir.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						PiezaRemito elemento = getElemento(getTabla().getSelectedRow());
						handleImprimir(elemento.getMetros(), elemento.getPiezaPadreODT());
					}

				});
			}
			return btnImprimir;
		}

		private boolean todasFueronControladas() {
			return mapYaControladas.keySet().size() == rs.getPiezas().size();
		}

		private boolean todasLasErroneasFueronReImpresas() {
			int cantErroneas = 0;
			for(int i=0; i < getTabla().getRowCount(); i++) {
				PiezaRemito pr = getElemento(i);
				cantErroneas += !pr.getMetros().equals(pr.getPiezaPadreODT().getMetros()) ? 1 : 0;
			}
			return cantErroneas == mapReImpresas.keySet().size();
		}
		
	}

}