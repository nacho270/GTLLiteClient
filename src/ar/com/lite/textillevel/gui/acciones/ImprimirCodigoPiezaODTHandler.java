package ar.com.lite.textillevel.gui.acciones;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.JasperHelper;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;

public class ImprimirCodigoPiezaODTHandler {

	private PiezaODT pieza;

	private static final String ARCHIVO_JASPER = "/ar/com/lite/textillevel/reportes/pieza_odt_codigo.jasper";

	public ImprimirCodigoPiezaODTHandler(PiezaODT pieza) {
		this.pieza = pieza;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void imprimir() {
		JasperReport reporte = JasperHelper.loadReporte(ARCHIVO_JASPER);
		try {
			OrdenDeTrabajo odt = pieza.getOdt();
			Map mapaParams = new HashMap();
			mapaParams.put("IMAGEN", GenericUtils.createBarCode(odt.getCodigo() + "" + pieza.getOrden()));
			mapaParams.put("CLIENTE", String.valueOf(odt.getRemito().getCliente().getNroCliente()));
			mapaParams.put("ODT_CODIGO", odt.getCodigo());
			mapaParams.put("NRO_REMITO", String.valueOf(odt.getRemito().getNroRemito()));
			mapaParams.put("ARTICULO", odt.getProductoArticulo().getArticulo().toString());			
			mapaParams.put("PRODUCTO", odt.getProductoArticulo().getProducto().toString());			
			mapaParams.put("PIEZA", String.valueOf(pieza.getOrden()));
			mapaParams.put("METROS", GenericUtils.getDecimalFormat().format(pieza.getMetros()));			
			mapaParams.put("ES_2DA", "1");//TODO:
			JasperPrint jasperPrint = JasperHelper.fillReport(reporte, mapaParams, Collections.singletonList(pieza));
			JasperHelper.imprimirReporte(jasperPrint, false, false, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}