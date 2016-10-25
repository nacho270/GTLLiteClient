package main;

import java.util.List;

import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.entidades.Modulo;
import ar.com.fwcommon.templates.main.login.EmptyLoginManager;

import com.google.common.collect.Lists;

public class GTLLiteClientEmptyLoginManager extends EmptyLoginManager {

	public GTLLiteClientEmptyLoginManager(int idAplicacion) {
		super(idAplicacion);
	}

	@Override
	public List<Modulo> getModulosUsuario() throws FWException {
		return Lists.newArrayList( //
			new Modulo(1, "Cosido", "main.acciones.VerDialogoOrdenarPiezasODTAction", -1, true) ,
			new Modulo(2, "Asignar metros piezas ODT", "main.acciones.VerLectorODTAction", -1, true)
		);
	}

}
