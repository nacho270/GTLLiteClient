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
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
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
import ar.com.textillevel.entidades.documentos.remito.RemitoEntrada;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;

public class JDialogOrdenarPiezasODT extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final int MAX_LONGITUD_RAZ_SOCIAL = 50;

	private JPanel panDetalle;
	private FWJTextField txtRazonSocial;
	private PanelTablaPieza panTablaPieza;
	private JPanel pnlBotones;
	private JButton btnAceptar;
	private JButton btnCancelar;
	private FWJNumericTextField txtNroRemito;
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

	private JTextField txtLocalidad;
	private JTextField txtDireccion;
	private JPanel panelDatosFactura;
	private JMenu menuODT;
	private boolean acepto;

	public JDialogOrdenarPiezasODT(Frame owner, OrdenDeTrabajo odt) {
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
			getTxtDireccion().setText(cliente.getDireccionReal().getDireccion());
			if(cliente.getDireccionReal().getLocalidad() != null) {
				getTxtLocalidad().setText(cliente.getDireccionReal().getLocalidad().getNombreLocalidad());
			}
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
			panTotales.add(new JLabel(" PIEZAS:"), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panTotales.add(getTxtPiezas(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			panTotales.add(new JLabel(" METROS:"), GenericUtils.createGridBagConstraints(2, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panTotales.add(getTxtMetros(), GenericUtils.createGridBagConstraints(3, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
		}
		return panTotales;
	}

	private JTextField getTxtPiezas() {
		if(txtPiezas == null) {
			txtPiezas = new JTextField();
			txtPiezas.setEditable(false);
			if(remitoEntrada.getId() != null) {
				getTxtPiezas().setText(String.valueOf(remitoEntrada.getPiezas().size()));
			}
		}
		return txtPiezas;
	}

	private JTextField getTxtMetros() {
		if(txtMetros == null) {
			txtMetros = new JTextField();
			txtMetros.setEditable(false);
			if(remitoEntrada.getId() != null) {
				txtMetros.setText(remitoEntrada.getTotalMetros().toString());
			}
		}
		return txtMetros;
	}

	private JPanel getPanDetalle() {
		if(panDetalle == null) {
			panDetalle = new JPanel();
			panDetalle.setLayout(new GridBagLayout());
			
			panDetalle.add(getPanelDatosCliente(), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 6, 1, 0, 0));

			panDetalle.add(getPanelDatosFactura(), GenericUtils.createGridBagConstraints(0, 1,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 6, 1, 0, 0));

			panDetalle.add(new JLabel(" ODT:"), GenericUtils.createGridBagConstraints(0, 2,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
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
			panelDatosCliente.add(new JLabel("Se�or/es: "), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtRazonSocial(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 3, 1, 1, 0));
			panelDatosCliente.add(new JLabel("Direccion: "), GenericUtils.createGridBagConstraints(0, 1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtDireccion(), GenericUtils.createGridBagConstraints(1, 1,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
			panelDatosCliente.add(new JLabel("Localidad: "), GenericUtils.createGridBagConstraints(2, 1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtLocalidad(), GenericUtils.createGridBagConstraints(3, 1,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
		}
		return panelDatosCliente;
	}

	private JTextField getTxtLocalidad() {
		if(txtLocalidad == null) {
			txtLocalidad = new JTextField();
			txtLocalidad.setEditable(false);
		}
		return txtLocalidad;
	}

	private JTextField getTxtDireccion() {
		if(txtDireccion == null) {
			txtDireccion = new JTextField();
			txtDireccion.setEditable(false);
		}
		return txtDireccion;
	}

	private JPanel getPanelDatosFactura() {
		if(panelDatosFactura == null){
			panelDatosFactura = new JPanel();
			panelDatosFactura.setLayout(new GridBagLayout());
			panelDatosFactura.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			panelDatosFactura.add(new JLabel("Remito N�: "), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtNroRemito(), GenericUtils.createGridBagConstraints(1, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			panelDatosFactura.add(new JLabel(" FECHA:"), GenericUtils.createGridBagConstraints(2, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtFechaEmision(), GenericUtils.createGridBagConstraints(3, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
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
			FWJOptionPane.showErrorMessage(JDialogOrdenarPiezasODT.this, StringW.wordWrap(msgValidacionPiezas), "Error");
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

	private class ODTSelectedAction implements Action {
		
		private OrdenDeTrabajo odt;
		
		public ODTSelectedAction(OrdenDeTrabajo odt) {
			this.odt = odt;
		}

		public void addPropertyChangeListener(PropertyChangeListener listener) {
		}

		public Object getValue(String key) {
			return null;
		}

		public boolean isEnabled() {
			return true;
		}

		public void putValue(String key, Object value) {
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
		}

		public void setEnabled(boolean b) {
		}

		public void actionPerformed(ActionEvent e) {
			
		}

		public String toString() {
			return odt.toString();
		}

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
			agregarElementos(odt.getPiezas());
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
		}

		private Object[] getRow(PiezaODT elemento) {
			String nroPieza = null;
			nroPieza = elemento.getPiezaRemito().getOrdenPieza().toString();
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

		@SuppressWarnings("serial")
		@Override
		protected FWJTable construirTabla() {
			FWJTable tablaPiezaEntrada = new FWJTable(0, CANT_COLS) {

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
			tablaPiezaEntrada.setStringColumn(COL_NRO_PIEZA_ENT, "PIEZA(S) ENT.", 200, 200, true);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ENT, "METROS ENT.", 120, true);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ODT, "METROS ODT", 120, false);
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
						getMenuItemEliminarFilas().setEnabled(getTabla().getSelectedRow() != -1);
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
			getComponentPopupMenu().add(getMenuODT());
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
			return false;
		}

		@Override
		public boolean validarQuitar() {
//			table2Objects();
//			int[] selectedRows = getTabla().getSelectedRows();
//			List<PiezaRemito> piezaRemitoList = new ArrayList<PiezaRemito>();
//			for(int sr : selectedRows) {
//				piezaRemitoList.add(getElemento(sr));
//			}
//			for(PiezaRemito elemento : piezaRemitoList) {
//				remitoEntrada.getPiezas().remove(elemento);
//
//				for(OrdenDeTrabajo odt : odtPiezaMap.values()) {
//					List<PiezaODT> podtToBorrarList = new ArrayList<PiezaODT>();
//					for(PiezaODT podt : odt.getPiezas()) {
//						if(podt.getPiezaRemito() != null && podt.getPiezaRemito().equals(elemento)) {
//							podtToBorrarList.add(podt);
//						}
//					}
//					odt.getPiezas().removeAll(podtToBorrarList);
//				}
//				
//				odtPiezaMap.remove(elemento);
//			}
//			remitoEntrada.recalcularOrdenes();
			//TODO:
			return true;
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

	private JMenu getMenuODT() {
		if(menuODT == null) {
			menuODT = new JMenu("ODTs");
			ODTSelectedAction odtAction = new ODTSelectedAction(null);
			JMenuItem menuItem = new JMenuItem(odtAction);
			menuItem.setText("Crear ODT");
			menuODT.add(menuItem);
		}
		return menuODT;
	}

}