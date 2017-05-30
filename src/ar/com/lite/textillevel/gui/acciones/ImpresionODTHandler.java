package ar.com.lite.textillevel.gui.acciones;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import ar.com.fwcommon.util.DateUtil;
import ar.com.fwcommon.util.StringUtil;
import ar.com.lite.textillevel.util.JasperHelper;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;

public class ImpresionODTHandler {
	
	private static final String ARCHIVO_JASPER = "/ar/com/lite/textillevel/reportes/odt-reverso.jasper";

	private final OrdenDeTrabajo odt;

	public ImpresionODTHandler(OrdenDeTrabajo odt, JDialog frameOwner) {
		this.odt = odt;
	}

	public void imprimir() {
		JasperReport reporte = JasperHelper.loadReporte(ARCHIVO_JASPER);
		ODTTO odtto = new ODTTO(this.odt);
		try {
			Map<String, Object> mapa = odtto.getMapaParametros();
			JasperPrint jasperPrint = JasperHelper.fillReport(reporte, mapa , Collections.singletonList(odtto));
			JasperHelper.imprimirReporte(jasperPrint, true, true, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class ODTTO implements Serializable {

		private static final long serialVersionUID = -2142756333577045834L;

		private static final int CANT_PIEZAS_COLUMNA = 27;
		
		private final String proceso;
		private final String codigo;
		private final String cliente;
		private final String articulo;
		private final String anchoFinal;
		private final String anchoCrudo;
		private final String tarima;
		private List<PiezaTablaImpresion> piezas1;
		private List<PiezaTablaImpresion> piezas2;

		public static class PiezaTablaImpresion implements Serializable {

			private static final long serialVersionUID = -2518421002298666124L;

			private Short nroPieza;
			private String metros;

			public PiezaTablaImpresion(Short nroPieza, String metros) {
				this.nroPieza = nroPieza;
				this.metros = metros;
			}

			public Short getNroPieza() {
				return nroPieza;
			}

			public void setNroPieza(Short nroPieza) {
				this.nroPieza = nroPieza;
			}

			public String getMetros() {
				return metros;
			}

			public void setMetros(String metros) {
				this.metros = metros;
			}
		}

		public ODTTO(final OrdenDeTrabajo odt) {
			ODTDatosMostradoHelper odtDatosHelper = new ODTDatosMostradoHelper(odt);
			this.codigo = odt.getCodigo();
			this.proceso = odt.getIProductoParaODT().toString();
			this.cliente = odtDatosHelper.getDescCliente();
			this.articulo = odtDatosHelper.getDescArticulo();
			this.tarima = odtDatosHelper.getDescTarima();
			this.anchoFinal = odtDatosHelper.getDescAnchoFinal();
			this.anchoCrudo = odtDatosHelper.getDescAnchoCrudo();
			crearPiezas(odt);
		}

		private void crearPiezas(OrdenDeTrabajo odt) {
			Collections.sort(odt.getPiezas(), new Comparator<PiezaODT>() {
				public int compare(PiezaODT o1, PiezaODT o2) {
					if (o1.getOrden() == null || o2.getOrden() == null) {
						return o1.getPiezaRemito().getOrdenPieza().compareTo(o2.getPiezaRemito().getOrdenPieza());
					}
					return o1.getOrden().compareTo(o2.getOrden());
				}
			});
			piezas1 = new ArrayList<ImpresionODTHandler.ODTTO.PiezaTablaImpresion>();
			piezas2 = new ArrayList<ImpresionODTHandler.ODTTO.PiezaTablaImpresion>();
			int i = 1;
			for (PiezaODT podt : odt.getPiezas()) {
				if (i <= CANT_PIEZAS_COLUMNA) {
					piezas1.add(new PiezaTablaImpresion(podt.getOrden().shortValue(), podt.getPiezaRemito().getMetros().toString()));
				} else {
					piezas2.add(new PiezaTablaImpresion(podt.getOrden().shortValue(), podt.getPiezaRemito().getMetros().toString()));
				}
				i++;
			}
		}

		public Map<String, Object> getMapaParametros() throws IOException {
			Map<String, Object> mapa = new HashMap<String, Object>();
			mapa.put("CLIENTE", this.cliente);
			mapa.put("ARTICULO", this.articulo);
			mapa.put("ANCHO_CRUDO", this.anchoCrudo);
			mapa.put("ANCHO_FINAL", this.anchoFinal);
			mapa.put("TARIMA", StringUtil.isNullOrEmpty(this.tarima) ? " --- " : this.tarima);
			mapa.put("CODIGO_ODT", this.codigo);
			mapa.put("FECHA", DateUtil.dateToString(DateUtil.getHoy(), DateUtil.SHORT_DATE));
			mapa.put("PROCESO", this.proceso);
			mapa.put("piezasDS1", new JRBeanCollectionDataSource(piezas1));
			mapa.put("piezasDS2", new JRBeanCollectionDataSource(piezas2));
			mapa.put("SUBREPORT_DIR", "ar/com/lite/textillevel/reportes/");
			mapa.put("con", new JRBeanCollectionDataSource(Collections.singletonList(this)));
			return mapa;
		}
	}
}
