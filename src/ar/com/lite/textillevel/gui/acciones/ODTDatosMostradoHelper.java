package ar.com.lite.textillevel.gui.acciones;

import java.math.BigDecimal;

import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.textillevel.entidades.enums.ETipoProducto;
import ar.com.textillevel.entidades.ventas.articulos.Color;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;

public class ODTDatosMostradoHelper {

	private final OrdenDeTrabajo odt;
	
	public ODTDatosMostradoHelper(OrdenDeTrabajo odt) {
		this.odt = odt;
	}

	public String getDescTarima() {
		String descrTarima = odt.getRemito().getTarima() != null ? odt.getRemito().getTarima().toString() : null;
		String descrEnPalet = odt.getRemito().getEnPalet() != null && odt.getRemito().getEnPalet() ? "EN PALET" : null;
		if(descrTarima != null && descrEnPalet != null) {
			return descrTarima + " / " + descrEnPalet;
		} else if(descrTarima != null && descrEnPalet == null) {
			return descrTarima;
		} else if(descrTarima == null && descrEnPalet != null) {
			return descrEnPalet;
		} else {
			return "";
		}
	}

	public String getDescColor() {
		if(getColor() != null) {
			return getColor().getNombre();
		} else {
			return "Sin Definir";
		}
	}

	public Color getColor() {
		ETipoProducto tipo = odt.getIProductoParaODT().getTipo();
		if(tipo == ETipoProducto.TENIDO) {
			return odt.getProductoArticulo() == null ? null : odt.getProductoArticulo().getColor();
		} else if(tipo == ETipoProducto.ESTAMPADO) {
			return odt.getProductoArticulo() == null ? null : odt.getProductoArticulo().getVariante().getColorFondo();
		} else {
			return null;
		}
	}

	public String getDescArticulo() {
		return odt.getIProductoParaODT().getArticulo().getNombre();
	}

	public String getDescGramaje() {
		return GenericUtils.getDecimalFormat3().format(odt.getRemito().getTotalMetros().floatValue()/odt.getRemito().getPesoTotal().floatValue());
	}

	public String getDescAnchoFinal() {
		BigDecimal anchoFinal = odt.getRemito().getAnchoFinal();
		return anchoFinal == null ? "" : GenericUtils.getDecimalFormat().format(anchoFinal.doubleValue());
	}

	public String getDescAnchoCrudo() {
		BigDecimal anchoCrudo = odt.getRemito().getAnchoCrudo();
		return anchoCrudo == null ? "" : GenericUtils.getDecimalFormat().format(anchoCrudo.doubleValue());
	}

	public String getDescCliente() {
		return String.valueOf(odt.getRemito().getCliente().getNroCliente());
		//TODO: Ver caso 01
	}

}