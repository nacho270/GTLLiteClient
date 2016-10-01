package main;

import java.util.ArrayList;
import java.util.List;

import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.entidades.Modulo;
import ar.com.fwcommon.templates.main.login.EmptyLoginManager;

public class GTLLiteClientEmptyLoginManager extends EmptyLoginManager {

	public GTLLiteClientEmptyLoginManager(int idAplicacion) {
		super(idAplicacion);
	}

	@Override
	public List<Modulo> getModulosUsuario() throws FWException {
		List<Modulo> modulos = new ArrayList<Modulo>();
		modulos.add(new Modulo(1, "Lector", "main.acciones.VerLectorRemitoEntradaClienteAction", -1, true));
		return modulos;
	}

}
