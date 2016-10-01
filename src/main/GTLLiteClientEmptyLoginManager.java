package main;

import java.util.List;

import com.google.common.collect.Lists;

import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.entidades.Modulo;
import ar.com.fwcommon.templates.main.login.EmptyLoginManager;

public class GTLLiteClientEmptyLoginManager extends EmptyLoginManager {

	public GTLLiteClientEmptyLoginManager(int idAplicacion) {
		super(idAplicacion);
	}

	@Override
	public List<Modulo> getModulosUsuario() throws FWException {
		return Lists.newArrayList( //
			new Modulo(1, "Lector", "main.acciones.VerLectorRemitoEntradaClienteAction", -1, true),
			new Modulo(2, "Ordenador piezas remito entrada", "main.acciones.VerFrameOrdenarRemitoEntradaClienteAction", -1, true) //
		);
	}

}
