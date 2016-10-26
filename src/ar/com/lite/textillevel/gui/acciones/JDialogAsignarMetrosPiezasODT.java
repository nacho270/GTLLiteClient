package ar.com.lite.textillevel.gui.acciones;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import ar.com.textillevel.entidades.documentos.remito.RemitoEntrada;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.emory.mathcs.backport.java.util.Collections;

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
	private JMenuItem menuItemEliminarFilas;
	private JMenuItem menuItemAgregarPiezas;

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

		private static final int CANT_COLS = 4;
		private static final int COL_NRO_ORDEN_PIEZA_ODT = 0;
		private static final int COL_METROS_PIEZA_ENT = 1;
		private static final int COL_METROS_PIEZA_ODT = 2;
		private static final int COL_OBJ = 3;

		private OrdenDeTrabajo odt;
		private boolean reversed = false;		
		private JButton btnRevertir;
		private JButton btnDividir;
		private JButton btnEliminarPiezas;
		
		private Map<Integer, Boolean> mapaPiezasImpresas = Maps.newHashMap();

		public PanelTablaPieza(OrdenDeTrabajo odt) {
			initializePopupMenu();
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
			String nroPieza = null;
			if(elemento.getOrdenSubpieza() == null) {
				nroPieza = elemento.getOrden()+"";
			} else {
				nroPieza = elemento.getOrden() + " / " + (elemento.getOrdenSubpieza()+1);
			}
			Object[] row = new Object[CANT_COLS];
			row[COL_NRO_ORDEN_PIEZA_ODT] = nroPieza;
			row[COL_METROS_PIEZA_ENT] = elemento.getOrdenSubpieza() == null ||  elemento.getOrdenSubpieza().intValue() == 0 ?  elemento.getPiezaRemito().getMetros().toString() : "";
			row[COL_METROS_PIEZA_ODT] = elemento.getMetros() == null ? null : elemento.getMetros().toString(); 
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
				public void cellEdited(int cell, int row) {
					if(cell == COL_METROS_PIEZA_ODT) {
						String metrosStr = (String)getTabla().getValueAt(row, COL_METROS_PIEZA_ODT);
						PiezaODT piezaODT = getElemento(row);
						if(!StringUtil.isNullOrEmpty(metrosStr) && !metrosStr.equals("0")) {
							piezaODT.setMetros(new BigDecimal(metrosStr));
							boolean piezaImpresa = Optional.fromNullable(mapaPiezasImpresas.get(piezaODT.getId())).or(false).booleanValue();
							if (piezaImpresa) {
								int respuesta = FWJOptionPane.showQuestionMessage(JDialogAsignarMetrosPiezasODT.this, StringW.wordWrap("La pieza Nro. " + piezaODT.getOrden().toString() + " ya fue impresa.\nDesea imprimirla nuevamente?"), "Pregunta");
								if (respuesta == FWJOptionPane.YES_OPTION) {
									imprimir(piezaODT);
								}
							} else {
								imprimir(piezaODT);
							}
						} else {
							piezaODT.setMetros(new BigDecimal(0));
						}
						
						actualizarTotales();
					}
				}

				private void imprimir(PiezaODT pieza) {
					new ImprimirCodigoPiezaODTHandler(pieza).imprimir();
					mapaPiezasImpresas.put(pieza.getId(), true);
				}

			};
			tablaPiezaEntrada.setStringColumn(COL_NRO_ORDEN_PIEZA_ODT, "NRO. DE PIEZA ODT", 120, 120, true);
			tablaPiezaEntrada.setAlignment(COL_NRO_ORDEN_PIEZA_ODT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ENT, "METROS ENTRADA", 100, true);
			tablaPiezaEntrada.setAlignment(COL_METROS_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ODT, "METROS SALIDA", 100, false);
			tablaPiezaEntrada.setAlignment(COL_METROS_PIEZA_ODT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setStringColumn(COL_OBJ, "", 0, 0, true);
			tablaPiezaEntrada.setSelectionMode(FWJTable.SINGLE_SELECTION);

			tablaPiezaEntrada.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					handleMouseEvent(e);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					handleMouseEvent(e);
				}

				private void handleMouseEvent(MouseEvent e) {
					if(e.isPopupTrigger()) {
						int newRow = getTabla().getSelectedRow();
						boolean habilitarAgregar = newRow != -1 && getElemento(newRow).getOrdenSubpieza() == null;
						boolean habilitarEliminar = newRow != -1 && getElemento(newRow).getOrdenSubpieza() != null;
						getMenuItemAgregarPiezas().setEnabled(habilitarAgregar);
						getMenuItemEliminarFilas().setEnabled(habilitarEliminar);
						getComponentPopupMenu().show(e.getComponent(), e.getX(), e.getY());
					}
				}

			});

			return tablaPiezaEntrada;
		}

		private void initializePopupMenu() {
			setComponentPopupMenu(new JPopupMenu());
			getComponentPopupMenu().add(getMenuItemAgregarPiezas());
			getComponentPopupMenu().add(getMenuItemEliminarFilas());
		}

		private JMenuItem getMenuItemEliminarFilas() {
			if(menuItemEliminarFilas == null) {
				menuItemEliminarFilas = new JMenuItem("Borrar Pieza");
				menuItemEliminarFilas.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						getBtnEliminarPieza().doClick();
					}
				});
			}
			return menuItemEliminarFilas;
		}

		private JMenuItem getMenuItemAgregarPiezas() {
			if(menuItemAgregarPiezas == null) {
				menuItemAgregarPiezas = new JMenuItem("Dividir");
				menuItemAgregarPiezas.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						getBtnDividir().doClick();
					}
				});
			}
			return menuItemAgregarPiezas;
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
							boolean ok = false;
							Integer cantSubpiezas = null;
							do {
								String input = JOptionPane.showInputDialog(JDialogAsignarMetrosPiezasODT.this, "Ingrese la cantidad de sub piezas: ", "Agregar Sub piezas", JOptionPane.INFORMATION_MESSAGE);
								if(input == null){
									break;
								}
								if (input.trim().length()==0 || !GenericUtils.esNumerico(input)) {
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
							agregarSubpiezas(getTabla().getSelectedRow(), 2);
						}
					}

					private void agregarSubpiezas(int rowToSplit, Integer cantSubpiezas) {
						Collections.sort(odt.getPiezas());
						Integer allPiezasSizeOriginal = odt.getPiezas().size();
						PiezaODT elemento = getElemento(rowToSplit);
						//seteo los datos de subpieza solo si la acción es "DIVIDIR" i.e. está habilitada sobre una piezaODT que NO es una subpieza 
						if(elemento.getOrdenSubpieza() == null) {
							elemento.setOrdenSubpieza(0);
							getTabla().setValueAt(elemento.getOrden() + " / " + 1, rowToSplit, COL_NRO_ORDEN_PIEZA_ODT);
						}
						for(int i=0; i<allPiezasSizeOriginal; i++) {
							if(i == rowToSplit) {
								for(int j=0; j < cantSubpiezas-1; j++) {
									PiezaODT pODT = new PiezaODT();
									pODT.setPiezaRemito(elemento.getPiezaRemito());
									pODT.setOrden(elemento.getOrden());
									pODT.setOrdenSubpieza(j+1);
									odt.getPiezas().add(i+j+1, pODT);
									getTabla().insertRow(i+j+1, getRow(pODT));
								}
							}
						}
						ajustarSubordenes(elemento.getOrden());
						
						actualizarTotales();
					}

				});
			}
			return btnDividir;
		}

		private void ajustarSubordenes(Integer orden) {
			for(int i=0; i<odt.getPiezas().size(); i++) {
				PiezaODT pODT = odt.getPiezas().get(i);
				if(pODT.getOrden().equals(orden)) {
					int cantAjustes=0;
					for(int j=i; pODT != null && pODT.getOrdenSubpieza() != null && pODT.getOrden().equals(orden) && j<odt.getPiezas().size(); j++) {
						pODT.setOrdenSubpieza(j-i);
						getTabla().setValueAt(pODT.getOrden() + " / " + (pODT.getOrdenSubpieza()+1), j, COL_NRO_ORDEN_PIEZA_ODT);
						if(pODT.getOrdenSubpieza() == 0) {
							getTabla().setValueAt(pODT.getPiezaRemito().getMetros(), i, COL_METROS_PIEZA_ENT);
						}
						cantAjustes++;
						if(j+1 < odt.getPiezas().size()) {
							pODT = odt.getPiezas().get(j+1);
						}
					}
					if(cantAjustes==1) {//i.e. cantidad de supiezas == 1
						getTabla().setValueAt(odt.getPiezas().get(i).getOrden(), i, COL_NRO_ORDEN_PIEZA_ODT);
						getTabla().setValueAt(odt.getPiezas().get(i).getPiezaRemito().getMetros(), i, COL_METROS_PIEZA_ENT);
						odt.getPiezas().get(i).setOrdenSubpieza(null);
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