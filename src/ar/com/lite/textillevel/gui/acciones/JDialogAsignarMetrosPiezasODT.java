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
import java.util.ArrayList;
import java.util.List;

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
import ar.com.fwcommon.util.GuiUtil;
import ar.com.fwcommon.util.StringUtil;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.entidades.documentos.remito.PiezaRemito;
import ar.com.textillevel.entidades.documentos.remito.RemitoEntrada;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;
import edu.emory.mathcs.backport.java.util.Collections;

public class JDialogAsignarMetrosPiezasODT extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final int MAX_LONGITUD_RAZ_SOCIAL = 50;

	private JPanel panDetalle;
	private FWJTextField txtRazonSocial;
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
		getTxtRazonSocial().setText(cliente.getRazonSocial());
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
	
		GuiUtil.setEstadoPanel(panDetalle, true);

		return panDetalle;
	}

	private JPanel getPanelDatosCliente() {
		if(panelDatosCliente == null){
			panelDatosCliente = new JPanel();
			panelDatosCliente.setLayout(new GridBagLayout());
			panelDatosCliente.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			panelDatosCliente.add(new JLabel("Cliente: "), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtRazonSocial(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.7, 0));
			panelDatosCliente.add(new JLabel("Nro.: "), GenericUtils.createGridBagConstraints(2, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtNroCliente(), GenericUtils.createGridBagConstraints(3, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.3, 0));
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

	private FWJTextField getTxtRazonSocial() {
		if(txtRazonSocial == null) {
			txtRazonSocial = new FWJTextField(MAX_LONGITUD_RAZ_SOCIAL);
			txtRazonSocial.setEditable(false);
		}
		return txtRazonSocial;
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
		private static final int COL_NRO_PIEZA_ENT = 0;
		private static final int COL_METROS_PIEZA_ENT = 1;
		private static final int COL_METROS_PIEZA_ODT = 2;
		private static final int COL_OBJ = 3;

		private OrdenDeTrabajo odt;

		public PanelTablaPieza(OrdenDeTrabajo odt) {
			initializePopupMenu();
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
			elemento.setOrden(getTabla().getRowCount()-1);
			if(elemento.tieneSalida()) {
				getTabla().lockCell(getTabla().getRowCount()-1, COL_METROS_PIEZA_ODT);
			}
		}

		private Object[] getRow(PiezaODT elemento) {
			String nroPieza = null;
			nroPieza = elemento.getPiezaRemito().getOrdenPieza()+ ((elemento.getOrdenSubpieza() == null ? "" : " / " + (elemento.getOrdenSubpieza()+1)));
			Object[] row = new Object[CANT_COLS];
			row[COL_NRO_PIEZA_ENT] = nroPieza;
			row[COL_METROS_PIEZA_ENT] = elemento.getPiezaRemito().getMetros().toString();
			//sugiero los metros de la pieza de entrada
			if(elemento.getMetros() == null) {
				elemento.setMetros(elemento.getPiezaRemito().getMetros());
			}
			row[COL_METROS_PIEZA_ODT] = elemento.getMetros().toString(); 
			row[COL_OBJ] = elemento;
			return row;
		}

		@Override
		protected FWJTable construirTabla() {

			FWJTable tablaPiezaEntrada = new FWJTable(0, CANT_COLS) {

				private static final long serialVersionUID = 1L;

				@Override
				public void newRowSelected(int newRow, int oldRow) {
					PiezaODT piezaODT = getElemento(newRow);
					boolean habilitarAgregar = newRow != -1 && piezaODT.getOrdenSubpieza() == null && !piezaODT.tieneSalida();
					boolean habilitarEliminar = newRow != -1 && piezaODT.getOrdenSubpieza() != null && !piezaODT.tieneSalida();
					getBotonAgregar().setEnabled(habilitarAgregar);
					getBotonEliminar().setEnabled(habilitarEliminar);
				}

				@Override
				public void cellEdited(int cell, int row) {
					if(cell == COL_METROS_PIEZA_ODT) {
						String metrosStr = (String)getTabla().getValueAt(row, COL_METROS_PIEZA_ODT);
						PiezaODT elemento = getElemento(row);
						if(!StringUtil.isNullOrEmpty(metrosStr)) {
							elemento.setMetros(new BigDecimal(metrosStr));
						} else {
							elemento.setMetros(new BigDecimal(0));
						}
						actualizarTotales();
					}
				}

			};
			tablaPiezaEntrada.setStringColumn(COL_NRO_PIEZA_ENT, "NRO. DE PIEZA ENTRADA", 200, 200, true);
			tablaPiezaEntrada.setAlignment(COL_NRO_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ENT, "METROS ENT.", 120, true);
			tablaPiezaEntrada.setAlignment(COL_METROS_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ODT, "METROS SALIDA", 120, false);
			tablaPiezaEntrada.setAlignment(COL_METROS_PIEZA_ODT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setStringColumn(COL_OBJ, "", 0, 0, true);

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
				menuItemEliminarFilas = new JMenuItem("Eliminar Fila(s)");
				menuItemEliminarFilas.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						getBotonEliminar().doClick();
					}
				});
			}
			return menuItemEliminarFilas;
		}

		private JMenuItem getMenuItemAgregarPiezas() {
			if(menuItemAgregarPiezas == null) {
				menuItemAgregarPiezas = new JMenuItem("Agregar Pieza(s)");
				menuItemAgregarPiezas.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						getBotonAgregar().doClick();
					}
				});
			}
			return menuItemAgregarPiezas;
		}

		private void actualizarTotales() {
			BigDecimal tm = new BigDecimal(0);
			int piezas = 0;
			for(PiezaODT pe : odt.getPiezas()) {
				tm = tm.add(pe.getMetros());
				if(piezaValida(pe)) {
					piezas++;
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
			getTabla().setNumRows(0);
			agregarElementos(odt.getPiezas());
			actualizarTotales();
		}

		@Override
		public boolean validarAgregar() {
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
				List<PiezaODT> allPiezasODT = odt.getPiezas();
				int selectedRow = getTabla().getSelectedRow();
				PiezaODT elemento = getElemento(selectedRow);
				elemento.setMetros(elemento.getMetros().divide(new BigDecimal(cantSubpiezas)));
				elemento.setOrdenSubpieza(0); //es la primera de las subpiezas!
				List<PiezaODT> antesDeElemSelected = new ArrayList<>(allPiezasODT.subList(0, selectedRow));
				List<PiezaODT> enElemSelected = new ArrayList<>(allPiezasODT.subList(selectedRow, selectedRow+1));
				List<PiezaODT> despuesDeElemSelected = new ArrayList<>(allPiezasODT.subList(selectedRow+1, allPiezasODT.size()));
				for(int i=0; i < cantSubpiezas-1; i++) {
					PiezaODT pODT = new PiezaODT();
					pODT.setPiezaRemito(elemento.getPiezaRemito());
					pODT.setMetros(elemento.getMetros());
					pODT.setOrdenSubpieza(i+1);
					enElemSelected.add(pODT);
				}
				allPiezasODT.clear();
				allPiezasODT.addAll(antesDeElemSelected);
				allPiezasODT.addAll(enElemSelected);
				allPiezasODT.addAll(despuesDeElemSelected);
				limpiar();
				agregarElementos(allPiezasODT);
				actualizarTotales();
			}
			return false;
		}

		@Override
		public boolean validarQuitar() {
			int selectedRow = getTabla().getSelectedRow();
			if(selectedRow != -1) {
				PiezaODT piezaODT = getElemento(selectedRow);
				PiezaRemito piezaEntrada = piezaODT.getPiezaRemito();
				odt.getPiezas().remove(piezaODT);
				List<PiezaODT> piezasHijas = getPiezasHijas(piezaEntrada);
				if(piezasHijas.size() == 1) {
					piezasHijas.get(0).setOrdenSubpieza(null);
				} else {
					for(int i=0; i < piezasHijas.size(); i++) {
						piezasHijas.get(i).setOrdenSubpieza(i);
					}
				}
				limpiar();
				agregarElementos(odt.getPiezas());
			}
			return true;
		}

		private List<PiezaODT> getPiezasHijas(PiezaRemito piezaEntrada) {
			List<PiezaODT> hijas = new ArrayList<>(); 
			for(PiezaODT pODT : odt.getPiezas()) {
				if(pODT.getPiezaRemito().equals(piezaEntrada)) {
					hijas.add(pODT);
				}
			}
			return hijas;
		}

		@Override
		protected PiezaODT getElemento(int fila) {
			return (PiezaODT)getTabla().getValueAt(fila, COL_OBJ);
		}

		@Override
		protected String validarElemento(int fila) {
			return null;
		}

	}

}