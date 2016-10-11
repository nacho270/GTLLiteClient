package ar.com.lite.textillevel.gui.acciones;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.JasperHelper;
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
			Map mapaParams = new HashMap();
			mapaParams.put("IMAGEN", GenericUtils.createBarCode(pieza.getOdt().getCodigo() + "" + pieza.getOrden()));
			JasperPrint jasperPrint = JasperHelper.fillReport(reporte, mapaParams, Collections.singletonList(pieza));
			JasperHelper.imprimirReporte(jasperPrint, false, false, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
