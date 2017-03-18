package ar.com.lite.textillevel.gui.acciones;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import main.GTLLiteGlobalCache;

import org.apache.taglibs.string.util.StringW;

import ar.com.fwcommon.componentes.FWDateField;
import ar.com.fwcommon.componentes.FWJNumericTextField;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.componentes.FWJTable;
import ar.com.fwcommon.componentes.FWJTextField;
import ar.com.fwcommon.componentes.PanelTablaSubirBajarModificar;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.fwcommon.util.StringUtil;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.entidades.documentos.remito.RemitoEntrada;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;
import ar.com.textillevel.modulos.odt.enums.EAvanceODT;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;

import com.google.common.collect.Lists;

public class JDialogOrdenarPiezasODT extends JDialog {

	private static final long serialVersionUID = 7959751463399871737L;

	private static final int MAX_LONGITUD_RAZ_SOCIAL = 50;

	private JPanel panDetalle;
	private FWJTextField txtRazonSocial;
	private JTextField txtNroCliente;
	private PanelTablaPieza panTablaPieza;
	private JPanel pnlBotones;
	private JButton btnAceptar;
	private JButton btnCancelar;
	private JButton btnImprimir;
	private FWJNumericTextField txtNroRemito;
	private FWDateField txtFechaEmision;
	private FWJTextField txtProducto;
	private OrdenDeTrabajo odt;
	private RemitoEntrada remitoEntrada;
	private JPanel panTotales;
	private JTextField txtMetros;
	private JTextField txtPiezas;
	private FWJTextField txtTotalPiezasEntrada;
	private FWJTextField txtTotalMetrosEntrada;
	private JPanel panelDatosCliente;
	private JPanel panelDatosFactura;
	
	private FWJNumericTextField txtMetrosABuscar;
	private JButton btnBuscarPorMetros;
	
	private boolean estadoModificado = false;
	private int ultimoOrdenIngresado = 0;
	
	private static final Comparator<PiezaODT> piezasComparator = new Comparator<PiezaODT>() {
		public int compare(PiezaODT o1, PiezaODT o2) {
			if (o1.getOrden() != null && o2.getOrden() != null) {
				return o1.getOrden().compareTo(o2.getOrden());
			}
			if (o1.getOrden() == null) {
				return 1;
			}
			return -1;
		}
	};

	public JDialogOrdenarPiezasODT(Frame owner, OrdenDeTrabajo odt) {
		super(owner);
		this.odt = odt;
		this.remitoEntrada = odt.getRemito();
//		setSize(new Dimension(590, 600)); //para rasperry
		setSize(new Dimension(630, 750));
		setTitle("Cosido");
		construct();
		setDatos();
		setModal(true);
		getPanTablaPieza().habilitarBotones(0);
	}

	private void setDatos() {
		remitoEntrada.getProductoArticuloList().clear();
		Cliente cliente = remitoEntrada.getCliente();
		getTxtRazonSocial().setText(cliente.getRazonSocial());
		getTxtFechaEmision().setFecha(remitoEntrada.getFechaEmision());
		getTxtProducto().setText(odt.getCodigo() + " / " + odt.getProductoArticulo().toString());
		if (cliente.getDireccionReal() != null) {
			getTxtNroCliente().setText(cliente.getNroCliente()+"");
		}
		refreshTable();
	}

	private void refreshTable() {
		getPanTablaPieza().limpiar();
		Collections.sort(odt.getPiezas(), piezasComparator);
		getPanTablaPieza().agregarElementos(odt.getPiezas());
	}

	private void construct() {
		setLayout(new GridBagLayout());
		add(getPanDetalle(), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 1, 1, 0, 1));
		add(getPanTotales(), GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
		add(getPanelBotones(), GenericUtils.createGridBagConstraints(0, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
        setFocusTraversalPolicy(crearPoliticaFocus());
	}

	private JPanel getPanTotales() {
		if (panTotales == null) {
			panTotales = new JPanel();
			panTotales.setLayout(new GridBagLayout());
			panTotales.add(new JLabel(" PIEZAS:"), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panTotales.add(getTxtPiezas(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			panTotales.add(new JLabel(" METROS:"), GenericUtils.createGridBagConstraints(2, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panTotales.add(getTxtMetros(), GenericUtils.createGridBagConstraints(3, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
		}
		return panTotales;
	}

	private JTextField getTxtPiezas() {
		if (txtPiezas == null) {
			txtPiezas = new JTextField();
			txtPiezas.setEditable(false);
			if (remitoEntrada.getId() != null) {
				getTxtPiezas().setText(String.valueOf(remitoEntrada.getPiezas().size()));
			}
		}
		return txtPiezas;
	}

	private JTextField getTxtMetros() {
		if (txtMetros == null) {
			txtMetros = new JTextField();
			txtMetros.setEditable(false);
			if (remitoEntrada.getId() != null) {
				txtMetros.setText(remitoEntrada.getTotalMetros().toString());
			}
		}
		return txtMetros;
	}

	private FWJNumericTextField getTxtMetrosABuscar() {
		if (txtMetrosABuscar == null) {
			txtMetrosABuscar = new FWJNumericTextField(0l, 99999l);
			txtMetrosABuscar.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						getBtnBuscarPorMetros().doClick();
					}
				}
			});
		}
		return txtMetrosABuscar;
	}

	private JButton getBtnBuscarPorMetros() {
		if (btnBuscarPorMetros == null) {
			btnBuscarPorMetros = new JButton("Ingresar");
			btnBuscarPorMetros.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(StringUtil.isNullOrEmpty(getTxtMetrosABuscar().getText())) {
						FWJOptionPane.showErrorMessage(JDialogOrdenarPiezasODT.this, "Debe ingresar los metros a buscar", "Error");
						return;
					}
					BigDecimal metrosABuscar = new BigDecimal(getTxtMetrosABuscar().getText());
					boolean piezaEncontrada = false;
					for(PiezaODT podt : odt.getPiezas()) {
						if (podt.getOrden() != null) {
							continue;
						}
						if (podt.getPiezaRemito().getMetros().compareTo(metrosABuscar) == 0) {
							podt.setOrden(++ultimoOrdenIngresado);
							piezaEncontrada = true;
							break;
						}
					}
					if (!piezaEncontrada) {
						FWJOptionPane.showErrorMessage(JDialogOrdenarPiezasODT.this, "No se ha encontrado una pieza con los metros ingresados", "Error");
						return;
					}
					getTxtMetrosABuscar().setText("");
					odt = GTLLiteRemoteService.grabarAndRegistrarCambioEstadoAndAvance(odt, EEstadoODT.EN_PROCESO, EAvanceODT.POR_COMENZAR, GTLLiteGlobalCache.getInstance().getUsuarioSistema());
					refreshTable();
				}
			});
		}
		return btnBuscarPorMetros;
	}

	private JPanel getPanDetalle() {
		if (panDetalle == null) {
			panDetalle = new JPanel();
			panDetalle.setLayout(new GridBagLayout());
			panDetalle.add(getPanelDatosCliente(), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 6, 1, 0, 0));
			panDetalle.add(getPanelDatosFactura(), GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 6, 1, 0, 0));
			panDetalle.add(new JLabel(" ODT:"), GenericUtils.createGridBagConstraints(0, 2, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panDetalle.add(getTxtProducto(), GenericUtils.createGridBagConstraints(1, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 2, 1, 0.5, 0));
			panDetalle.add(new JLabel("Metros: "), GenericUtils.createGridBagConstraints(0, 3, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panDetalle.add(getTxtMetrosABuscar(), GenericUtils.createGridBagConstraints(1, 3, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.3, 0));
			panDetalle.add(getBtnBuscarPorMetros(), GenericUtils.createGridBagConstraints(2, 3, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
			panDetalle.add(getPanTablaPieza(), GenericUtils.createGridBagConstraints(0, 4, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 6, 1, 1, 1));
		}
		GuiUtil.setEstadoPanel(panDetalle, true);
		return panDetalle;
	}

	private JPanel getPanelDatosCliente() {
		if (panelDatosCliente == null) {
			panelDatosCliente = new JPanel();
			panelDatosCliente.setLayout(new GridBagLayout());
			panelDatosCliente.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			panelDatosCliente.add(new JLabel("Cliente: "), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtNroCliente(), GenericUtils.createGridBagConstraints(1, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
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
		if (panelDatosFactura == null) {
			panelDatosFactura = new JPanel();
			panelDatosFactura.setLayout(new GridBagLayout());
			panelDatosFactura.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			panelDatosFactura.add(new JLabel("Remito Nº: "), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtNroRemito(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			panelDatosFactura.add(new JLabel(" FECHA:"), GenericUtils.createGridBagConstraints(2, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtFechaEmision(), GenericUtils.createGridBagConstraints(3, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
		
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
		if (txtProducto == null) {
			txtProducto = new FWJTextField();
			txtProducto.setEditable(false);
		}
		return txtProducto;
	}

	private FWDateField getTxtFechaEmision() {
		if (txtFechaEmision == null) {
			txtFechaEmision = new FWDateField();
			if (remitoEntrada.getId() != null) {
				txtFechaEmision.setFecha(remitoEntrada.getFechaEmision());
			}
			txtFechaEmision.setEditable(false);
		}
		return txtFechaEmision;
	}

	private FWJNumericTextField getTxtNroRemito() {
		if (txtNroRemito == null) {
			txtNroRemito = new FWJNumericTextField(new Long(0), Long.MAX_VALUE);
			if (remitoEntrada.getId() != null) {
				getTxtNroRemito().setText(remitoEntrada.getNroRemito().toString());
			}
		}
		txtNroRemito.setEditable(false);
		return txtNroRemito;
	}

	private FWJTextField getTxtRazonSocial() {
		if (txtRazonSocial == null) {
			txtRazonSocial = new FWJTextField(MAX_LONGITUD_RAZ_SOCIAL);
			txtRazonSocial.setEditable(false);
		}
		return txtRazonSocial;
	}

	private JPanel getPanelBotones() {
		if (pnlBotones == null) {
			pnlBotones = new JPanel();
			pnlBotones.setLayout(new FlowLayout(FlowLayout.CENTER));
			pnlBotones.add(getBtnAceptar());
			pnlBotones.add(getBtnCancelar());
			pnlBotones.add(getBtnImprimir());
			getBtnCancelar().setEnabled(true);
			getBtnImprimir().setVisible(this.odt.getPiezas().get(0).getOrden() != null);
			getBtnAceptar().setVisible(this.odt.getPiezas().get(0).getOrden() == null);
		}
		return pnlBotones;
	}

	private JButton getBtnCancelar() {
		if (btnCancelar == null) {
			btnCancelar = new JButton("Cancelar");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int respuesta = FWJOptionPane.showQuestionMessage(JDialogOrdenarPiezasODT.this, StringW.wordWrap("Ya se han impreso piezas, si cierra la ventana los datos ingresados no seran guardados. Desea continuar"), "Pregunta");
					if (respuesta == FWJOptionPane.YES_OPTION) {
						dispose();
					}
				}
			});
		}
		return btnCancelar;
	}

	private JButton getBtnAceptar() {
		if (btnAceptar == null) {
			btnAceptar = new JButton("Aceptar");
			btnAceptar.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (validar()) {
						grabarEImprimir();
						dispose();
					}
					return;
				}

			});
		}
		return btnAceptar;
	}

	private void grabarEImprimir() {
		if (estadoModificado) {
			if (validar()) {
				odt = GTLLiteRemoteService.grabarAndRegistrarCambioEstadoAndAvance(odt, EEstadoODT.EN_PROCESO, EAvanceODT.FINALIZADO, GTLLiteGlobalCache.getInstance().getUsuarioSistema());
				estadoModificado = false;
				new ImpresionODTHandler(odt, this).imprimir();
			}
		} else if (validar()) {
			new ImpresionODTHandler(odt, this).imprimir();
		}
	}

	private JButton getBtnImprimir() {
		if (btnImprimir == null) {
			btnImprimir = new JButton("Imprimir");
			btnImprimir.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (estadoModificado) {
						int resp = FWJOptionPane.showQuestionMessage(JDialogOrdenarPiezasODT.this, StringW.wordWrap("Ha modificado el orden de las piezas. El mismo sera grabado antes de imprimir. Desea continuar?"), "Pregunta");
						if (resp == FWJOptionPane.NO_OPTION) {
							return;
						}
					}
					grabarEImprimir();
				}
			});
		}
		return btnImprimir;
	}
	
	private boolean validar() {
		String msgValidacionPiezas = getPanTablaPieza().validar();
		if (msgValidacionPiezas != null) {
			FWJOptionPane.showErrorMessage(JDialogOrdenarPiezasODT.this, StringW.wordWrap(msgValidacionPiezas), "Error");
			return false;
		}
		return true;
	}

	private PanelTablaPieza getPanTablaPieza() {
		if (panTablaPieza == null) {
			panTablaPieza = new PanelTablaPieza(odt);
		}
		return panTablaPieza;
	}

	private class PanelTablaPieza extends PanelTablaSubirBajarModificar<PiezaODT> {

		private static final long serialVersionUID = 5214170341731673564L;

		private static final int CANT_COLS = 4;
		private static final int COL_NRO_PIEZA_ENT = 0;
		private static final int COL_METROS_PIEZA_ENT = 1;
		private static final int COL_ORDEN = 2;
		private static final int COL_OBJ = 3;

		public PanelTablaPieza(OrdenDeTrabajo odt) {
			getBotonModificar().setVisible(false);
		}

		@Override
		protected FWJTable construirTabla() {
			final FWJTable tablaPiezaEntrada = new FWJTable(0, CANT_COLS);
			tablaPiezaEntrada.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					habilitarBotones(getTabla().getSelectedRow());
				}
			});
			tablaPiezaEntrada.setStringColumn(COL_NRO_PIEZA_ENT, "PIEZA(S) ENT.", 150, 150, true);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ENT, "METROS ENT.", 150, true);
			tablaPiezaEntrada.setIntColumn(COL_ORDEN, "ORDEN", 90, true);
			tablaPiezaEntrada.setAlignment(COL_NRO_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setAlignment(COL_METROS_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setAlignment(COL_ORDEN, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setHeaderAlignment(COL_NRO_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setHeaderAlignment(COL_METROS_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setHeaderAlignment(COL_ORDEN, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setStringColumn(COL_OBJ, "", 0, 0, true);
			tablaPiezaEntrada.setReorderingAllowed(false);
			tablaPiezaEntrada.setAllowSorting(true);
//			tablaPiezaEntrada.setCellSelectionEnabled(true);
			return tablaPiezaEntrada;
		}

		@Override
		protected void botonBajarPresionado() {
			int selectedRow = getTabla().getSelectedRow();
			PiezaODT piezaABajar = getElemento(selectedRow);
			piezaABajar.setOrden(piezaABajar.getOrden() + 1);
			PiezaODT piezaASubir = getElemento(selectedRow + 1);
			piezaASubir.setOrden(piezaASubir.getOrden() - 1);
			odt.getPiezas().set(selectedRow, piezaASubir);
			odt.getPiezas().set(selectedRow + 1, piezaABajar);
			odt = GTLLiteRemoteService.grabarAndRegistrarCambioEstadoAndAvance(odt, EEstadoODT.EN_PROCESO, EAvanceODT.POR_COMENZAR, GTLLiteGlobalCache.getInstance().getUsuarioSistema());
			refreshTable();
			getTabla().setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
			habilitarBotones(selectedRow + 1);
		}

		@Override
		protected void botonSubirPresionado() {
			int selectedRow = getTabla().getSelectedRow();
			PiezaODT piezaASubir = getElemento(selectedRow);
			piezaASubir.setOrden(piezaASubir.getOrden() - 1);
			PiezaODT piezaABajar = getElemento(selectedRow - 1);
			piezaABajar.setOrden(piezaABajar.getOrden() + 1);
			odt.getPiezas().set(selectedRow, piezaASubir);
			odt.getPiezas().set(selectedRow - 1, piezaABajar);
			odt = GTLLiteRemoteService.grabarAndRegistrarCambioEstadoAndAvance(odt, EEstadoODT.EN_PROCESO, EAvanceODT.POR_COMENZAR, GTLLiteGlobalCache.getInstance().getUsuarioSistema());
			refreshTable();
			getTabla().setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
			habilitarBotones(selectedRow - 1);
		}
		
		private void habilitarBotones(int rowSelected) {
			getBotonSubir().setEnabled(rowSelected>0);
			getBotonBajar().setEnabled(rowSelected<getTabla().getRowCount()-1 && 
					getElemento(rowSelected + 1) != null && 
					getElemento(rowSelected + 1).getOrden() != null);
		}
		
		public String validar() {
			String ret = null;
			List<Integer> ordenes = Lists.newArrayList();
			for (int i = 0; i < getTabla().getRowCount(); i++) {
				Object valueAt = getTabla().getValueAt(i, COL_ORDEN);
				if (valueAt instanceof String) {
					String orden = (String) valueAt;
					if (orden == null || orden.equals("0")) {
						getTabla().changeSelection(0, COL_ORDEN, false, false);
						getTabla().requestFocus();
						return "Falta cargar el orden en algunas piezas";
					}
					ordenes.add(Integer.valueOf(orden));
				} else {
					Integer orden = (Integer) valueAt;
					if (orden == null || orden.equals(0)) {
						getTabla().changeSelection(0, COL_ORDEN, false, false);
						getTabla().requestFocus();
						return "Falta cargar el orden en algunas piezas";
					}
					ordenes.add(orden);
				}
			}
			Collections.sort(ordenes);
			if (!ordenes.get(0).equals(1)) {
				return "Los ordenes deben comenzar en 1.";
			}
			for(int i = 0; i<ordenes.size() -1;i++) {
				Integer orden = ordenes.get(i);
				Integer ordenSiguiente = ordenes.get(i+1);
				if(!(orden + 1 == ordenSiguiente)) {
					getTabla().changeSelection(0, COL_ORDEN, false, false);
					getTabla().requestFocus();
					return "Los ordenes deben ser consecutivos. Se ha encontrado un salto entre " + orden + " y " + ordenSiguiente;
				}
			}
			return ret;
		}

		@Override
		protected void agregarElemento(PiezaODT elemento) {
			Object[] row = getRow(elemento);
			getTabla().addRow(row);
			if (elemento.getOrden() != null) {
				ultimoOrdenIngresado = elemento.getOrden();
			}
		}

		private Object[] getRow(PiezaODT elemento) {
			String nroPieza = null;
			nroPieza = elemento.getPiezaRemito().getOrdenPieza().toString();
			Object[] row = new Object[CANT_COLS];
			row[COL_NRO_PIEZA_ENT] = nroPieza;
			row[COL_METROS_PIEZA_ENT] = elemento.getPiezaRemito().getMetros().toString();
			row[COL_ORDEN] = elemento.getOrden();
			row[COL_OBJ] = elemento;
			return row;
		}

		@Override
		protected PiezaODT getElemento(int fila) {
			return (PiezaODT) getTabla().getValueAt(fila, COL_OBJ);
		}

		@Override
		protected String validarElemento(int fila) {
			return null;
		}
	}
	
	private FocusTraversalPolicy crearPoliticaFocus() {
        return new FocusTraversalPolicy() {

            @Override
            public Component getLastComponent(final Container aContainer) {
                return getTxtMetrosABuscar();
            }

            @Override
            public Component getFirstComponent(final Container aContainer) {
                return getTxtMetrosABuscar();
            }

            @Override
            public Component getDefaultComponent(final Container aContainer) {
                return getTxtMetrosABuscar();
            }

            @Override
            public Component getComponentBefore(final Container aContainer, final Component aComponent) {
                return getTxtMetrosABuscar();
            }

            @Override
            public Component getComponentAfter(final Container aContainer, final Component aComponent) {
                return getTxtMetrosABuscar();
            }
        };
    }
}