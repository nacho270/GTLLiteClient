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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import ar.com.fwcommon.componentes.FWDateField;
import ar.com.fwcommon.componentes.FWJNumericTextField;
import ar.com.fwcommon.componentes.FWJTable;
import ar.com.fwcommon.componentes.FWJTextField;
import ar.com.fwcommon.componentes.PanelTabla;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.entidades.documentos.remito.PiezaRemito;
import ar.com.textillevel.entidades.documentos.remito.RemitoEntrada;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;

public class JDialogOrdenarPiezasRemitoEntrada extends JDialog {

	private static final long serialVersionUID = -1276054822090966716L;

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

	public JDialogOrdenarPiezasRemitoEntrada(Frame owner, RemitoEntrada remitoEntrada) {
		super(owner);
		this.remitoEntrada = remitoEntrada;
		setSize(new Dimension(630, 750));
		setTitle("Remito de entrada");
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
		add(getPanDetalle(), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 1, 1, 0, 1));
		add(getPanTotales(), GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
		add(getPanelBotones(), GenericUtils.createGridBagConstraints(0, 2, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
	}

	private JPanel getPanTotales() {
		if (panTotales == null) {
			panTotales = new JPanel();
			panTotales.setLayout(new GridBagLayout());
			panTotales.add(new JLabel(" PIEZAS:"), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panTotales.add(getTxtPiezas(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			panTotales.add(new JLabel(" METROS:"), GenericUtils.createGridBagConstraints(2, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panTotales.add(getTxtMetros(), GenericUtils.createGridBagConstraints(3, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
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

			panDetalle.add(getPanelDatosCliente(), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 6, 1, 0, 0));

			panDetalle.add(getPanelDatosFactura(), GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 6, 1, 0, 0));

			panDetalle.add(new JLabel(" ODT:"), GenericUtils.createGridBagConstraints(0, 2, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panDetalle.add(getTxtProducto(), GenericUtils.createGridBagConstraints(1, 2, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));

			panDetalle.add(getPanTablaPieza(), GenericUtils.createGridBagConstraints(0, 3, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 6, 1, 1, 1));
		}

		GuiUtil.setEstadoPanel(panDetalle, true);

		return panDetalle;
	}

	private JPanel getPanelDatosCliente() {
		if (panelDatosCliente == null) {
			panelDatosCliente = new JPanel();
			panelDatosCliente.setLayout(new GridBagLayout());
			panelDatosCliente.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			panelDatosCliente.add(new JLabel("Señor/es: "), GenericUtils.createGridBagConstraints(0, 0,
					GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtRazonSocial(), GenericUtils.createGridBagConstraints(1, 0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 3, 1, 1, 0));
			panelDatosCliente.add(new JLabel("Direccion: "), GenericUtils.createGridBagConstraints(0, 1,
					GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtDireccion(), GenericUtils.createGridBagConstraints(1, 1,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
			panelDatosCliente.add(new JLabel("Localidad: "), GenericUtils.createGridBagConstraints(2, 1,
					GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosCliente.add(getTxtLocalidad(), GenericUtils.createGridBagConstraints(3, 1,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
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
			panelDatosFactura.add(new JLabel("Remito Nº: "), GenericUtils.createGridBagConstraints(0, 0,
					GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtNroRemito(), GenericUtils.createGridBagConstraints(1, 0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
			panelDatosFactura.add(new JLabel(" FECHA:"), GenericUtils.createGridBagConstraints(2, 0,
					GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelDatosFactura.add(getTxtFechaEmision(), GenericUtils.createGridBagConstraints(3, 0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.5, 0));
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
					acepto = false;
					dispose();
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
		return true;
	}

	private PanelTablaPieza getPanTablaPieza() {
		if (panTablaPieza == null) {
			panTablaPieza = new PanelTablaPieza(remitoEntrada);
		}
		return panTablaPieza;
	}

	public boolean isAceptpo() {
		return acepto;
	}

	private class PanelTablaPieza extends PanelTabla<PiezaRemito> {

		private static final long serialVersionUID = 1L;

		private static final int CANT_COLS = 5;
		private static final int COL_NRO_PIEZA = 0;
		private static final int COL_METROS_PIEZA = 1;
		private static final int COL_OBSERVACIONES = 2;
		private static final int COL_ODT = 3;
		private static final int COL_OBJ = 4;

		private static final int CANT_PIEZAS_INICIALES = 30;

		private RemitoEntrada remitoEntrada;
		private Map<PiezaRemito, OrdenDeTrabajo> odtPiezaMap;

		public PanelTablaPieza(RemitoEntrada remitoEntrada) {
			this.remitoEntrada = remitoEntrada;
			if (remitoEntrada.getPiezas().isEmpty()) {
				addRowsInTabla(CANT_PIEZAS_INICIALES);
			} else {
				agregarElementos(remitoEntrada.getPiezas());
			}
		}

		@Override
		protected void agregarElemento(PiezaRemito elemento) {
			Object[] row = getRow(elemento);
			getTabla().addRow(row);
		}

		private Object[] getRow(PiezaRemito elemento) {
			String nroPieza = null;
			nroPieza = elemento.getOrdenPieza().toString();
			Object[] row = new Object[CANT_COLS];
			row[COL_NRO_PIEZA] = nroPieza;
			row[COL_METROS_PIEZA] = elemento.getMetros() == null ? null : elemento.getMetros().toString();
			row[COL_OBSERVACIONES] = elemento.getObservaciones();
			row[COL_ODT] = getODTFromPieza(elemento);
			row[COL_OBJ] = elemento;
			return row;
		}

		private OrdenDeTrabajo getODTFromPieza(PiezaRemito pr) {
			if (pr.getId() == null || pr.getId() == 0) {
				return odtPiezaMap.get(pr);
			}
			for (Entry<PiezaRemito, OrdenDeTrabajo> entry : odtPiezaMap.entrySet()) {
				if (entry.getKey().getId().equals(pr.getId())) {
					return entry.getValue();
				}
			}
			return null;
		}

		@SuppressWarnings("serial")
		@Override
		protected FWJTable construirTabla() {
			FWJTable tablaPiezaEntrada = new FWJTable(0, CANT_COLS) {

				@Override
				public void cellEdited(int cell, int row) {
					if (cell == COL_METROS_PIEZA) {

					}
				}

			};
			tablaPiezaEntrada.setStringColumn(COL_NRO_PIEZA, "NUMERO", 80, 80, true);
			tablaPiezaEntrada.setFloatColumn(COL_METROS_PIEZA, "METROS", 80, false);
			tablaPiezaEntrada.setStringColumn(COL_OBSERVACIONES, "OBSERVACIONES", 205, 205, false);
			tablaPiezaEntrada.setStringColumn(COL_ODT, "ODT", 220, 220, true);
			tablaPiezaEntrada.setStringColumn(COL_OBJ, "", 0, 0, true);

			return tablaPiezaEntrada;
		}

		private void addRowsInTabla(Integer cantFilas) {
			if (cantFilas != null) {
				for (int i = 0; i < cantFilas; i++) {
					getTabla().addRow();
					PiezaRemito piezaEntrada = new PiezaRemito();
					remitoEntrada.getPiezas().add(piezaEntrada);
					int rowCount = remitoEntrada.getPiezas().size();
					piezaEntrada.setOrdenPieza(rowCount);
					getTabla().setValueAt(piezaEntrada.getOrdenPieza(), rowCount - 1, COL_NRO_PIEZA);
					getTabla().setValueAt(piezaEntrada, rowCount - 1, COL_OBJ);
				}
			}
		}

		@Override
		public boolean validarQuitar() {
			table2Objects();
			int[] selectedRows = getTabla().getSelectedRows();
			List<PiezaRemito> piezaRemitoList = new ArrayList<PiezaRemito>();
			for (int sr : selectedRows) {
				piezaRemitoList.add(getElemento(sr));
			}
			for (PiezaRemito elemento : piezaRemitoList) {
				remitoEntrada.getPiezas().remove(elemento);

				for (OrdenDeTrabajo odt : odtPiezaMap.values()) {
					List<PiezaODT> podtToBorrarList = new ArrayList<PiezaODT>();
					for (PiezaODT podt : odt.getPiezas()) {
						if (podt.getPiezaRemito() != null && podt.getPiezaRemito().equals(elemento)) {
							podtToBorrarList.add(podt);
						}
					}
					odt.getPiezas().removeAll(podtToBorrarList);
				}

				odtPiezaMap.remove(elemento);
			}
			remitoEntrada.recalcularOrdenes();
			return true;
		}

		private void table2Objects() {
			for (int i = 0; i < getTabla().getRowCount(); i++) {
				PiezaRemito elemento = getElemento(i);
				String metrosStr = (String) getTabla().getValueAt(i, COL_METROS_PIEZA);
				if (metrosStr != null) {
					elemento.setMetros(new BigDecimal(metrosStr));
				}
				elemento.setObservaciones((String) getTabla().getValueAt(i, COL_OBSERVACIONES));
			}
		}

		@Override
		protected PiezaRemito getElemento(int fila) {
			return (PiezaRemito) getTabla().getValueAt(fila, COL_OBJ);
		}

		@Override
		protected String validarElemento(int fila) {
			return null;
		}
	}
}
