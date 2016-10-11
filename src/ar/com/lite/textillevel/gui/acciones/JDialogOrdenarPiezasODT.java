package ar.com.lite.textillevel.gui.acciones;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.entidades.documentos.remito.RemitoEntrada;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class JDialogOrdenarPiezasODT extends JDialog {

	private static final long serialVersionUID = 7959751463399871737L;

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
	private JPanel panelDatosCliente;
	private JTextField txtLocalidad;
	private JTextField txtDireccion;
	private JPanel panelDatosFactura;
	private boolean acepto;

	public JDialogOrdenarPiezasODT(Frame owner, OrdenDeTrabajo odt) {
		super(owner);
		this.odt = odt;
		this.remitoEntrada = odt.getRemito();
		setSize(new Dimension(630, 750));
		setTitle("Ordenar piezas Orden De Trabajo");
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
		if (cliente.getDireccionReal() != null) {
			getTxtDireccion().setText(cliente.getDireccionReal().getDireccion());
			if (cliente.getDireccionReal().getLocalidad() != null) {
				getTxtLocalidad().setText(cliente.getDireccionReal().getLocalidad().getNombreLocalidad());
			}
		}
	}

	private void construct() {
		setLayout(new GridBagLayout());
		add(getPanDetalle(), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 1, 1, 0, 1));
		add(getPanTotales(), GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
		add(getPanelBotones(), GenericUtils.createGridBagConstraints(0, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
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

	private JPanel getPanDetalle() {
		if (panDetalle == null) {
			panDetalle = new JPanel();
			panDetalle.setLayout(new GridBagLayout());

			panDetalle.add(getPanelDatosCliente(), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 6, 1, 0, 0));

			panDetalle.add(getPanelDatosFactura(), GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 6, 1, 0, 0));

			panDetalle.add(new JLabel(" ODT:"), GenericUtils.createGridBagConstraints(0, 2, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panDetalle.add(getTxtProducto(), GenericUtils.createGridBagConstraints(1, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));

			panDetalle.add(getPanTablaPieza(), GenericUtils.createGridBagConstraints(0, 3, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 6, 1, 1, 1));
		}

		GuiUtil.setEstadoPanel(panDetalle, true);

		return panDetalle;
	}

	private JPanel getPanelDatosCliente() {
		if (panelDatosCliente == null) {
			panelDatosCliente = new JPanel();
			panelDatosCliente.setLayout(new GridBagLayout());
			panelDatosCliente.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			panelDatosCliente.add(new JLabel("Señor/es: "), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtRazonSocial(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 3, 1, 1, 0));
			panelDatosCliente.add(new JLabel("Direccion: "), GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtDireccion(), GenericUtils.createGridBagConstraints(1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
			panelDatosCliente.add(new JLabel("Localidad: "), GenericUtils.createGridBagConstraints(2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtLocalidad(), GenericUtils.createGridBagConstraints(3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
		}
		return panelDatosCliente;
	}

	private JTextField getTxtLocalidad() {
		if (txtLocalidad == null) {
			txtLocalidad = new JTextField();
			txtLocalidad.setEditable(false);
		}
		return txtLocalidad;
	}

	private JTextField getTxtDireccion() {
		if (txtDireccion == null) {
			txtDireccion = new JTextField();
			txtDireccion.setEditable(false);
		}
		return txtDireccion;
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
		}
		return panelDatosFactura;
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
			getBtnCancelar().setEnabled(true);
		}
		return pnlBotones;
	}

	private JButton getBtnCancelar() {
		if (btnCancelar == null) {
			btnCancelar = new JButton("Cancelar");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean hayPiezasImpresas = getPanTablaPieza().hayPiezasImpresas();
					if (!hayPiezasImpresas) {
						acepto = false;
						dispose();
					} else {
						int respuesta = FWJOptionPane.showQuestionMessage(JDialogOrdenarPiezasODT.this, StringW.wordWrap("Ya se han impreso piezas, si cierra la ventana los datos ingresados no seran guardados. Desea continuar"), "Pregunta");
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
		if (btnAceptar == null) {
			btnAceptar = new JButton("Aceptar");
			btnAceptar.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (validar()) {
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

	public boolean isAceptpo() {
		return acepto;
	}

	private class PanelTablaPieza extends PanelTabla<PiezaODT> {

		private static final long serialVersionUID = 5214170341731673564L;

		private static final int CANT_COLS = 4;
		private static final int COL_NRO_PIEZA_ENT = 0;
		private static final int COL_METROS_PIEZA_ENT = 1;
		private static final int COL_ORDEN = 2;
		private static final int COL_OBJ = 3;

		private boolean reversed = false;
		private Map<Integer, Boolean> mapaPiezasImpresas = Maps.newHashMap();

		private JButton btnRevertir;

		public PanelTablaPieza(OrdenDeTrabajo odt) {
			agregarElementos(odt.getPiezas());
			getBotonAgregar().setVisible(false);
			getBotonEliminar().setVisible(false);
			agregarBoton(getBtnRevertir());
		}

		public JButton getBtnRevertir() {
			if (btnRevertir == null) {
				btnRevertir = new JButton("Revertir");
				btnRevertir.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						limpiar();
						if (reversed) {
							agregarElementos(odt.getPiezas());
						} else {
							List<PiezaODT> piezas = Lists.reverse(odt.getPiezas());
							agregarElementos(piezas);
						}
						reversed = !reversed;
					}
				});
			}
			return btnRevertir;
		}

		public boolean hayPiezasImpresas() {
			return !mapaPiezasImpresas.isEmpty();
		}

		public String validar() {
			String ret = null;
			for (int i = 0; i < getTabla().getRowCount(); i++) {
				String orden = (String) getTabla().getValueAt(i, COL_ORDEN);
				if (orden == null || orden.equals("0")) {
					ret = "Falta cargar el orden en algunas piezas";
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
			row[COL_ORDEN] = elemento.getOrden();
			row[COL_OBJ] = elemento;
			return row;
		}

		@Override
		protected FWJTable construirTabla() {
			final FWJTable tablaPiezaEntrada = new FWJTable(0, CANT_COLS) {

				private static final long serialVersionUID = -2327372268374179171L;

				public void cellEdited(int cell, int row) {
					if (cell == COL_ORDEN) {
						String valor = (String) getTabla().getValueAt(row, cell);
						Integer orden = Integer.valueOf((String) valor);
						boolean ordenRepetido = false;
						for (PiezaODT podt : odt.getPiezas()) {
							if (podt.getOrden() != null && podt.getOrden().equals(orden)) {
								ordenRepetido = true;
								break;
							}
						}
						if (orden <= 0) {
							blanquearCelda(cell, row);
							return;
						}
						if (ordenRepetido) {
							FWJOptionPane.showErrorMessage(JDialogOrdenarPiezasODT.this, "El orden " + orden + " ya ha sido ingresado.", "Error");
							blanquearCelda(cell, row);
							return;
						}
						PiezaODT piezaODT = getElemento(row);
						piezaODT.setOrden(orden);
						boolean piezaImpresa = Optional.fromNullable(mapaPiezasImpresas.get(piezaODT.getId())).or(false).booleanValue();
						if (piezaImpresa == false) {
							imprimir(piezaODT);
						} else {
							int respuesta = FWJOptionPane.showQuestionMessage(JDialogOrdenarPiezasODT.this, StringW.wordWrap("La pieza Nro " + piezaODT.getPiezaRemito().getOrdenPieza().toString() + " ya fue impresa.\nDesea imprimirla nuevamente?"), "Pregunta");
							if (respuesta == FWJOptionPane.YES_OPTION) {
								imprimir(piezaODT);
							}
						}
					}
				}

				private void blanquearCelda(int cell, int row) {
					try {
						getTabla().setValueAt(null, row, cell);
					} catch (Exception e) {
						// ESTO TIRA EXCEPTION, PERO NO PASA NADA.
						// HAGO ESTO PARA OCULTAR EL STACKTRACE
					}
					PiezaODT pieza = getElemento(row);
					pieza.setOrden(null);
					mapaPiezasImpresas.put(pieza.getId(), false);
				}

				private void imprimir(PiezaODT pieza) {
					new ImprimirCodigoPiezaODTHandler(pieza).imprimir();
					mapaPiezasImpresas.put(pieza.getId(), true);
				}
			};
			tablaPiezaEntrada.setStringColumn(COL_NRO_PIEZA_ENT, "PIEZA(S) ENT.", 150, 150, true);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA_ENT, "METROS ENT.", 150, true);
			tablaPiezaEntrada.setIntColumn(COL_ORDEN, "ORDEN", 90, false);
			tablaPiezaEntrada.setAlignment(COL_NRO_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setAlignment(COL_METROS_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setAlignment(COL_ORDEN, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setHeaderAlignment(COL_NRO_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setHeaderAlignment(COL_METROS_PIEZA_ENT, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setHeaderAlignment(COL_ORDEN, FWJTable.CENTER_ALIGN);
			tablaPiezaEntrada.setStringColumn(COL_OBJ, "", 0, 0, true);
			tablaPiezaEntrada.setReorderingAllowed(false);
			tablaPiezaEntrada.setAllowSorting(true);
			return tablaPiezaEntrada;
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
}