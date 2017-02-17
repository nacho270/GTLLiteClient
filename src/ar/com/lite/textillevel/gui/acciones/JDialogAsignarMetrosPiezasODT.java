package ar.com.lite.textillevel.gui.acciones;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import org.apache.taglibs.string.util.StringW;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ar.com.fwcommon.componentes.FWDateField;
import ar.com.fwcommon.componentes.FWJNumericTextField;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.componentes.FWJTable;
import ar.com.fwcommon.componentes.FWJTextField;
import ar.com.fwcommon.componentes.PanelTabla;
import ar.com.fwcommon.util.StringUtil;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.entidades.documentos.remito.RemitoEntrada;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;

public class JDialogAsignarMetrosPiezasODT extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel panDetalle;
	private PanelTablaPieza panTablaPieza;
	private JPanel pnlBotones;
	private JButton btnAceptar;
	private JButton btnCancelar;
	private FWJNumericTextField txtNroRemito;
	
	private FWJTextField txtTotalPiezasEntrada;
	private FWJTextField txtTotalMetrosEntrada;
	
	private FWDateField txtFechaEmision;
	private FWJTextField txtProducto;
	private OrdenDeTrabajo odt;
	private RemitoEntrada remitoEntrada;
	private JPanel panTotales; 
	private JTextField txtMetros;
	private JTextField txtPiezas;

	private JPanel panelDatosCliente; 

	private JTextField txtNroCliente;
	private JPanel panelDatosFactura;
	private boolean acepto;
	
	private static final String TEXTO_DIVIDIR="Dividir";
	private static final String TEXTO_AGREGAR_SUBPIEZA="Agregar";

	public JDialogAsignarMetrosPiezasODT(Frame owner, OrdenDeTrabajo odt) {
		super(owner);
		this.odt = odt;
		this.remitoEntrada = odt.getRemito();
		setSize(new Dimension(630, 750));
		setTitle("Orden De Trabajo");
		construct();
		setDatos();
		setModal(true);
	}

	private void setDatos() {
		remitoEntrada.getProductoArticuloList().clear();
		Cliente cliente = remitoEntrada.getCliente();
		getTxtFechaEmision().setFecha(remitoEntrada.getFechaEmision());
		getTxtProducto().setText(odt.getProductoArticulo().toString());
		if(cliente.getDireccionReal() != null) {
			getTxtNroCliente().setText(cliente.getNroCliente()+"");
		}
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

			panDetalle.add(new JLabel("ODT:"), GenericUtils.createGridBagConstraints(0, 2,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panDetalle.add(getTxtProducto(), GenericUtils.createGridBagConstraints(1, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));

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
			
			panelDatosFactura.add(new JLabel("Total Piezas Entrada: "), GenericUtils.createGridBagConstraints(0, 1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtTotalPiezasEntrada(), GenericUtils.createGridBagConstraints(1, 1,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			panelDatosFactura.add(new JLabel("Total Metros Entrada: "), GenericUtils.createGridBagConstraints(2, 1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtTotalMetrosEntrada(), GenericUtils.createGridBagConstraints(3, 1,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
		}
		return panelDatosFactura;
	}

	private FWJTextField getTxtTotalPiezasEntrada() {
		if(txtTotalPiezasEntrada == null) {
			txtTotalPiezasEntrada = new FWJTextField();
			txtTotalPiezasEntrada.setEditable(false);
			if(remitoEntrada.getId() != null) {
				txtTotalPiezasEntrada.setText(String.valueOf(remitoEntrada.getPiezas().size()));
			}
		}
		return txtTotalPiezasEntrada;
	}
	
	private FWJTextField getTxtTotalMetrosEntrada() {
		if(txtTotalMetrosEntrada == null) {
			txtTotalMetrosEntrada = new FWJTextField();
			txtTotalMetrosEntrada.setEditable(false);
			if(remitoEntrada.getId() != null) {
				txtTotalMetrosEntrada.setText(String.valueOf(remitoEntrada.getTotalMetros()));
			}
		}
		return txtTotalMetrosEntrada;
	}

	private FWJTextField getTxtProducto() {
		if(txtProducto == null) {
			txtProducto = new FWJTextField();
			txtProducto.setEditable(false);
		}
		return txtProducto;
	}

	private FWDateField getTxtFechaEmision() {
		if(txtFechaEmision == null) {
			txtFechaEmision = new FWDateField();
			if(remitoEntrada.getId() != null) {
				txtFechaEmision.setFecha(remitoEntrada.getFechaEmision());
			}
			txtFechaEmision.setEditable(false);
		}
		return txtFechaEmision;
	}

	private FWJNumericTextField getTxtNroRemito() {
		if(txtNroRemito == null) {
			txtNroRemito = new FWJNumericTextField(new Long(0), Long.MAX_VALUE);
			if(remitoEntrada.getId() != null) {
				getTxtNroRemito().setText(remitoEntrada.getNroRemito().toString());
			}
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
					boolean hayPiezasImpresas = getPanTablaPieza().hayPiezasImpresas();
					if (!hayPiezasImpresas) {
						acepto = false;
						dispose();
					} else {
						int respuesta = FWJOptionPane.showQuestionMessage(JDialogAsignarMetrosPiezasODT.this, StringW.wordWrap("Ya se han impreso piezas, si cierra la ventana los datos ingresados no seran guardados. Desea continuar"), "Pregunta");
						if (respuesta == FWJOptionPane.YES_OPTION) {
							acepto = false;
							dispose();
						}
					}
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
						odt.setEstadoODT(EEstadoODT.EN_OFICINA);
						GTLLiteRemoteService.grabarPiezasODT(odt);
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
			FWJOptionPane.showErrorMessage(JDialogAsignarMetrosPiezasODT.this, StringW.wordWrap(msgValidacionPiezas), "Error");
			return false;
		}
		return true;
	}

	private PanelTablaPieza getPanTablaPieza() {
		if(panTablaPieza == null) {
			panTablaPieza = new PanelTablaPieza(odt);
		}
		return panTablaPieza;
	}

	public boolean isAceptpo() {
		return acepto;
	}

	private class PanelTablaPieza extends PanelTabla<PiezaODT> {

		private static final long serialVersionUID = 1L;

		private static final int CANT_COLS = 5;
		private static final int COL_NRO_ORDEN_PIEZA_ODT = 0;
		private static final int COL_METROS_PIEZA_ENT = 1;
		private static final int COL_METROS_PIEZA_ODT = 2;
		private static final int COL_ES_DE_2DA = 3;
		private static final int COL_OBJ = 4;

		private OrdenDeTrabajo odt;
		private boolean reversed = false;		
		private JButton btnRevertir;
		private JButton btnDividir;
		private JButton btnEliminarPiezas;
		
		private Map<String, Boolean> mapaPiezasImpresas = Maps.newHashMap();

		public PanelTablaPieza(OrdenDeTrabajo odt) {
			getBotonAgregar().setVisible(false);
			getBotonEliminar().setVisible(false);
			agregarBoton(getBtnDividir());
			agregarBoton(getBtnEliminarPieza());
			agregarBoton(getBtnRevertir());
			this.odt = odt;
			Collections.sort(odt.getPiezas());
			agregarElementos(odt.getPiezas());
			actualizarTotales();
		}

		public String validar() {
			String ret=null;
			for(int i = 0; i < getTabla().getRowCount(); i++) {
				String metrosStr = (String)getTabla().getValueAt(i, COL_METROS_PIEZA_ODT);
				if(metrosStr == null || metrosStr.equals("0")) {
					ret = "Faltan cargar metros en algunas piezas";
					break;
				}
			}
			return ret;
		}

		@Override
		protected void agregarElemento(PiezaODT elemento) {
			Object[] row = getRow(elemento);
			getTabla().addRow(row);
			if(elemento.tieneSalida()) {
				getTabla().lockCell(getTabla().getRowCount()-1, COL_METROS_PIEZA_ODT);
			}
		}

		private Object[] getRow(PiezaODT elemento) {
			Object[] row = new Object[CANT_COLS];
			row[COL_NRO_ORDEN_PIEZA_ODT] = elemento.toString();
			row[COL_METROS_PIEZA_ENT] = elemento.getOrdenSubpieza() == null ||  elemento.getOrdenSubpieza().intValue() == 0 ?  elemento.getPiezaRemito().getMetros().toString() : "";
			row[COL_METROS_PIEZA_ODT] = elemento.getMetros() == null ? null : elemento.getMetros().toString();
			row[COL_ES_DE_2DA] = elemento.getEsDeSegunda() != null && elemento.getEsDeSegunda();
			row[COL_OBJ] = elemento;
			return row;
		}

		@Override
		protected FWJTable construirTabla() {

			FWJTable tablaPiezaEntrada = new FWJTable(0, CANT_COLS) {

				private static final long serialVersionUID = 1L;

				@Override
				public void newRowSelected(int newRow, int oldRow) {
					if(newRow != -1) {
						PiezaODT piezaODT = getElemento(newRow);
						boolean habilitarAgregar = newRow != -1 && piezaODT.getOrdenSubpieza() == null && !piezaODT.tieneSalida();
						boolean habilitarEliminar = newRow != -1 && piezaODT.getOrdenSubpieza() != null && !piezaODT.tieneSalida();
						getBtnEliminarPieza().setEnabled(habilitarEliminar);
						if(habilitarEliminar && !habilitarAgregar) {//es una subpieza!
							getBtnDividir().setText(TEXTO_AGREGAR_SUBPIEZA);
						} else {
							getBtnDividir().setText(TEXTO_DIVIDIR);
						}
						getBtnDividir().setEnabled(true);
					} else {
						getBtnDividir().setEnabled(false);
						getBtnEliminarPieza().setEnabled(false);
						getBtnDividir().setText(TEXTO_DIVIDIR);						
					}
				}

				@Override
				public void cellEdited(int cell, final int row) {
					if(cell == COL_METROS_PIEZA_ODT) {
						String metrosStr = (String)getTabla().getValueAt(row, COL_METROS_PIEZA_ODT);
						final PiezaODT piezaODT = getElemento(row);
						if(!StringUtil.isNullOrEmpty(metrosStr) && !metrosStr.equals("0")) {
							final BigDecimal metrosNew = new BigDecimal(metrosStr);
							float dif = metrosNew.subtract(piezaODT.getPiezaRemito().getMetros()).abs().floatValue();
							if(!piezaYaDividida(piezaODT) && dif > piezaODT.getPiezaRemito().getMetros().floatValue()*0.2f) {//solo muestro la pregunta para las piezas q no fueron divididas y que cumplen con la regla del 20%
									SwingUtilities.invokeLater(new Runnable() {
										@Override
										public void run() {
											if(FWJOptionPane.showQuestionMessage(JDialogAsignarMetrosPiezasODT.this, "¿Está seguro que desea ingresar esa cantidad de metros?", "Atención") == FWJOptionPane.YES_OPTION) {
												piezaODT.setMetros(metrosNew);
												handleImprimir(piezaODT);
											} else {
												setValueAt(null, row, COL_METROS_PIEZA_ODT);
											}
										}
									});
							} else{
								piezaODT.setMetros(metrosNew);
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										handleImprimir(piezaODT);
									}
								});
							}
						} else {
							piezaODT.setMetros(new BigDecimal(0));
						}
						actualizarTotales();
					}
					if(cell == COL_ES_DE_2DA) {
						PiezaODT piezaODT = getElemento(row);
						piezaODT.setEsDeSegunda((Boolean)getTabla().getValueAt(row, COL_ES_DE_2DA));
						handleImprimir(piezaODT);
					}
				}

				private void handleImprimir(PiezaODT piezaODT) {
					boolean piezaImpresa = Optional.fromNullable(mapaPiezasImpresas.get(piezaODT.toString())).or(false).booleanValue();
					if (piezaImpresa && piezaODT.getMetros() != null && piezaODT.getMetros().floatValue() > 0f) {
						int respuesta = FWJOptionPane.showQuestionMessage(JDialogAsignarMetrosPiezasODT.this, StringW.wordWrap("La pieza Nro. " + piezaODT.toString() + " ya fue impresa.\nDesea imprimirla nuevamente?"), "Pregunta");
						if (respuesta == FWJOptionPane.YES_OPTION) {
							imprimir(piezaODT);
						}
					} else if(piezaODT.getMetros() != null && piezaODT.getMetros().floatValue() > 0f) {
						imprimir(piezaODT);
					}
				}

				private void imprimir(PiezaODT pieza) {
					new ImprimirCodigoPiezaODTHandler(pieza).imprimir();
					mapaPiezasImpresas.put(pieza.toString(), true);
				}

			};
			tablaPiezaEntrada.setStringColumn(COL_NRO_ORDEN_PIEZA_ODT, "NRO. DE PIEZA ODT", 120, 120, true);
			tablaPiezaEntrada.setAlignment(COL_NRO_ORDEN_PIEZA_ODT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ENT, "METROS ENTRADA", 100, true);
			tablaPiezaEntrada.setAlignment(COL_METROS_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ODT, "METROS SALIDA", 100, false);
			tablaPiezaEntrada.setAlignment(COL_METROS_PIEZA_ODT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setCheckColumn(COL_ES_DE_2DA, "2DA", 40, false);
			tablaPiezaEntrada.setHeaderAlignment(COL_ES_DE_2DA, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setStringColumn(COL_OBJ, "", 0, 0, true);
			tablaPiezaEntrada.setSelectionMode(FWJTable.SINGLE_SELECTION);

			return tablaPiezaEntrada;
		}

		private void actualizarTotales() {
			BigDecimal tm = new BigDecimal(0);
			int piezas = 0;
			for(PiezaODT pe : odt.getPiezas()) {
				if(pe.getMetros() != null) {
					tm = tm.add(pe.getMetros());
					if(piezaValida(pe)) {
						piezas++;
					}
				}
			}
			getTxtMetros().setText(tm.toString());
			getTxtPiezas().setText(String.valueOf(piezas));
		}

		private boolean piezaValida(PiezaODT pe) {
			BigDecimal cero = new BigDecimal(0);
			return pe.getMetros() != null && pe.getMetros().compareTo(cero) > 0;
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
		protected PiezaODT getElemento(int fila) {
			return (PiezaODT)getTabla().getValueAt(fila, COL_OBJ);
		}

		@Override
		protected String validarElemento(int fila) {
			return null;
		}

		private JButton getBtnEliminarPieza() {
			if(btnEliminarPiezas == null) {
				btnEliminarPiezas = new JButton("Borrar Piezas");
				btnEliminarPiezas.setEnabled(false);
				btnEliminarPiezas.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						int selectedRow = getTabla().getSelectedRow();
						if(selectedRow != -1) {
							PiezaODT piezaODT = getElemento(selectedRow);
							odt.getPiezas().remove(piezaODT);
							getTabla().removeRow(selectedRow);
							mapaPiezasImpresas.remove(piezaODT.toString());
							ajustarSubordenes(piezaODT.getOrden());
							actualizarTotales();
						}
					}

				});
				
			}
			return btnEliminarPiezas;
		}
		
		private JButton getBtnDividir() {
			if(btnDividir == null) {
				btnDividir = new JButton(TEXTO_DIVIDIR);
				btnDividir.setEnabled(false);
				btnDividir.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if(getBtnDividir().getText().equals(TEXTO_DIVIDIR)) {
							if(piezaYaDividida(getElemento(getTabla().getSelectedRow()))) {
								FWJOptionPane.showInformationMessage(JDialogAsignarMetrosPiezasODT.this, "Primero elimine las subpiezas y luego reintente dividir.", "Atención");
								return;
							}
							
							boolean ok = false;
							Integer cantSubpiezas = null;
							do {
								String input = JOptionPane.showInputDialog(JDialogAsignarMetrosPiezasODT.this, "Ingrese la cantidad de sub piezas: ", "Agregar Sub piezas", JOptionPane.INFORMATION_MESSAGE);
								if(input == null){
									break;
								}
								if (input.trim().length()==0 || !GenericUtils.esNumerico(input) || Integer.valueOf(input) <= 0) {
									FWJOptionPane.showErrorMessage(JDialogAsignarMetrosPiezasODT.this, "Ingreso incorrecto", "error");
								} else {
									ok = true;
									cantSubpiezas = Integer.valueOf(input);
								}
							} while (!ok);
							if(ok) {
								agregarSubpiezas(getTabla().getSelectedRow(), cantSubpiezas);
							}
						} else {
							agregarSubpiezas(getTabla().getSelectedRow(), 1);
						}
						getTabla().clearSelection();						
					}

					private void agregarSubpiezas(int rowToSplit, Integer cantSubpiezas) {
						Collections.sort(odt.getPiezas());
						Integer allPiezasSizeOriginal = odt.getPiezas().size();
						PiezaODT podtParent = findParent(getElemento(rowToSplit), odt.getPiezas());
						//seteo los datos de subpieza solo si la acción es "DIVIDIR" i.e. está habilitada sobre una piezaODT que NO es una subpieza 
						for(int i=0; i<allPiezasSizeOriginal; i++) {
							if(i == rowToSplit) {
								OrdenDeTrabajo odt = podtParent.getOdt();
								for(int j=0; j < cantSubpiezas; j++) {
									PiezaODT podtChild = new PiezaODT();
									podtChild.setPiezaRemito(podtParent.getPiezaRemito());
									podtChild.setOrden(podtParent.getOrden());
									podtChild.setOdt(odt);
									podtChild.setOrdenSubpieza(j+1);
									odt.getPiezas().add(i+j+1, podtChild);
									getTabla().insertRow(i+j+1, getRow(podtChild));
								}
							}
						}
						ajustarSubordenes(podtParent.getOrden());
						
						actualizarTotales();
					}

					private PiezaODT findParent(PiezaODT elemento, List<PiezaODT> piezas) {
						if(elemento.getOrdenSubpieza() == null) {
							return elemento;
						} else {
							for(PiezaODT pieza : piezas) {
								if(pieza.getOrden().equals(elemento.getOrden()) && pieza.getOrdenSubpieza() == null) {
									return pieza;
								}
							}
						}
						return null;
					}

				});
			}
			return btnDividir;
		}

		private boolean piezaYaDividida(PiezaODT elemento) {
			for(PiezaODT pODT : odt.getPiezas()) {
				if(pODT.getOrden() != null && elemento.getOrden() != null && pODT.getOrden().equals(elemento.getOrden()) && pODT.getOrdenSubpieza() != null) {
					return true;
				}
			}
			return false;
		}

		private void ajustarSubordenes(Integer orden) {
			for(int i=0; i<odt.getPiezas().size(); i++) {
				PiezaODT pODT = odt.getPiezas().get(i);
				if(pODT.getOrden() != null && orden != null && pODT.getOrden().equals(orden)) {
					int j = i+1;
					while(j<odt.getPiezas().size() && odt.getPiezas().get(j).getOrdenSubpieza() != null) {
						odt.getPiezas().get(j).setOrdenSubpieza(j-i);
						getTabla().setValueAt(odt.getPiezas().get(j).toString(), j, COL_NRO_ORDEN_PIEZA_ODT);
						j++;
					}
					break;
				}
			}
		}
		
		private JButton getBtnRevertir() {
			if (btnRevertir == null) {
				btnRevertir = new JButton("Revertir");
				btnRevertir.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						limpiar();
						agregarElementosODTEnBaseAlOrden();
						reversed = !reversed;
					}

				});
			}
			return btnRevertir;
		}

		private void agregarElementosODTEnBaseAlOrden() {
			if (reversed) {
				agregarElementos(odt.getPiezas());
			} else {
				Collections.sort(odt.getPiezas());
				List<PiezaODT> piezas = Lists.reverse(odt.getPiezas());
				agregarElementos(piezas);
			}
		}

		public boolean hayPiezasImpresas() {
			return !mapaPiezasImpresas.isEmpty();
		}

	}

}