package ar.com.lite.textillevel.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.util.BeanFactoryRemoteAbstract;
import ar.com.fwcommon.util.StringUtil;
import ar.com.textillevel.entidades.documentos.remito.RemitoSalida;
import ar.com.textillevel.entidades.portal.UsuarioSistema;
import ar.com.textillevel.entidades.to.TerminalServiceResponse;
import ar.com.textillevel.facade.api.remote.EntregaReingresoDocumentosFacadeRemote;
import ar.com.textillevel.facade.api.remote.RemitoSalidaFacadeRemote;
import ar.com.textillevel.facade.api.remote.UsuarioSistemaFacadeRemote;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;
import ar.com.textillevel.modulos.odt.enums.EAvanceODT;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;
import ar.com.textillevel.modulos.odt.facade.api.remote.OrdenDeTrabajoFacadeRemote;
import ar.com.textillevel.modulos.odt.to.InfoAsignacionMaquinaTO;
import ar.com.textillevel.modulos.terminal.entidades.Terminal;
import ar.com.textillevel.modulos.terminal.facade.api.remote.TerminalFacadeRemote;
import ar.com.textillevel.util.GestorTerminalBarcode;
import main.GTLLiteGlobalCache;

public class GTLLiteRemoteService {

	private static GTLLiteBeanFactory gtlBeanFactory1 = GTLLiteBeanFactory.getInstance();
	private static GTLLiteOtroSistemaBeanFactory gtlBeanFactory2 = GTLLiteOtroSistemaBeanFactory.getInstance();

	public static OrdenDeTrabajo getODTByCodigo(final String codigo) throws RemoteException {
		OrdenDeTrabajo odt = gtlBeanFactory1.getBean2(OrdenDeTrabajoFacadeRemote.class).getODTEagerByCodigo(codigo);
		if (odt != null) {
			return odt;
		} else {// busco en el segundo
			return gtlBeanFactory2.getBean2(OrdenDeTrabajoFacadeRemote.class).getODTEagerByCodigo(codigo);
		}
	}

	public static OrdenDeTrabajo grabarAndRegistrarCambioEstadoAndAvance(OrdenDeTrabajo odt, EEstadoODT estado, EAvanceODT avance) {
		// consulto en el primero
		UsuarioSistema usuarioSistema = GTLLiteGlobalCache.getInstance().getUsuarioSistema();
		OrdenDeTrabajo odtCheck = gtlBeanFactory1.getBean2(OrdenDeTrabajoFacadeRemote.class).getODTEagerByCodigo(odt.getCodigo());
		if (odtCheck != null) {// estaba en el primero => grabo ahí
			return gtlBeanFactory1.getBean2(OrdenDeTrabajoFacadeRemote.class).grabarAndRegistrarCambioEstadoAndAvance(odt, estado, avance, usuarioSistema == null ? getTerminalData() : null, usuarioSistema);
		}
		// consulto en el segundo
		odtCheck = gtlBeanFactory2.getBean2(OrdenDeTrabajoFacadeRemote.class).getODTEagerByCodigo(odt.getCodigo());
		if (odtCheck != null) {// estaba en el segundo => grabo ahí
			return gtlBeanFactory2.getBean2(OrdenDeTrabajoFacadeRemote.class).grabarAndRegistrarCambioEstadoAndAvance(odt, estado, avance, usuarioSistema == null ? getTerminalData() : null, usuarioSistema);
		}
		throw new IllegalArgumentException("La ODT " + odt + " no está en ningún sistema...");
	}

	public static InfoAsignacionMaquinaTO getMaquinaAndProximoOrdenBySector(ESectorMaquina sector) {
		return gtlBeanFactory1.getBean2(OrdenDeTrabajoFacadeRemote.class).getMaquinaAndProximoOrdenBySector(sector); //esta info no hace falta ir a buscarla al 2 
	}

	public static void grabarAndRegistrarAvanceEnEstadoEnProceso(OrdenDeTrabajo odt, ESectorMaquina sector) {
		// consulto en el primero
		OrdenDeTrabajo odtCheck = gtlBeanFactory1.getBean2(OrdenDeTrabajoFacadeRemote.class).getODTEagerByCodigo(odt.getCodigo());
		if (odtCheck != null) {// estaba en el primero => grabo ahí
			gtlBeanFactory1.getBean2(OrdenDeTrabajoFacadeRemote.class).grabarAndRegistrarAvanceEnEstadoEnProceso(odtCheck.getId(), odt.getMaquinaActual(), sector, getTerminalData(), null);
			return;
		}
		// consulto en el segundo
		odtCheck = gtlBeanFactory2.getBean2(OrdenDeTrabajoFacadeRemote.class).getODTEagerByCodigo(odt.getCodigo());
		if (odtCheck != null) {// estaba en el segundo => grabo ahí
			gtlBeanFactory2.getBean2(OrdenDeTrabajoFacadeRemote.class).grabarAndRegistrarAvanceEnEstadoEnProceso(odtCheck.getId(), odt.getMaquinaActual(), sector, getTerminalData(), null);
			return;
		}
		throw new IllegalArgumentException("La ODT " + odt + " no está en ningún sistema...");
	}

	public static List<OrdenDeTrabajo> getAllNoFinalizadasBySector(ESectorMaquina sector) {
		return gtlBeanFactory2.getBean2(OrdenDeTrabajoFacadeRemote.class).getAllNoFinalizadasBySector(sector);
	}

	public static Terminal getTerminalData() {
		String hostAddress = null;
		Terminal terminal = null;
		try {
			hostAddress = getFirstNonLoopbackAddress().getHostAddress();
			terminal = gtlBeanFactory1.getBean2(TerminalFacadeRemote.class).getByIP(hostAddress);
		} catch (Exception e) {
			throw new IllegalArgumentException("Hubo un problema al calcular la IP del dispositivo " + hostAddress + " msg: " + e.getMessage());
		}
		if(terminal == null) {
			List<Terminal> all = gtlBeanFactory1.getBean2(TerminalFacadeRemote.class).getAll();
			List<String> terminalDATA = new ArrayList<String>(all.size());
			for(Terminal t : all) {
				terminalDATA.add(t.getNombre() + " - " + t.getIp());
			}
			throw new IllegalArgumentException("No existe en la DB una Terminal con IP " + hostAddress + ".\nLas terminales habilitadas son: \n" + StringUtil.getCadena(terminalDATA, "\n"));
		}
		return terminal;
	}

	@SuppressWarnings("rawtypes")
	private static InetAddress getFirstNonLoopbackAddress() throws SocketException {
		Enumeration en = NetworkInterface.getNetworkInterfaces();
	    while (en.hasMoreElements()) {
	        NetworkInterface i = (NetworkInterface) en.nextElement();
	        for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
	            InetAddress addr = (InetAddress) en2.nextElement();
	            if (!addr.isLoopbackAddress()) {
	                if (addr instanceof Inet4Address) {
	                    return addr;
	                }
	            }
	        }
	    }
	    return null;
	}
	public static TerminalServiceResponse marcarEntregado(String codigo) {
		if (codigo.endsWith("0")) {
			return gtlBeanFactory1.getBean2(EntregaReingresoDocumentosFacadeRemote.class).marcarEntregado(codigo, GTLLiteGlobalCache.getInstance().getTerminalData().getNombre());
		}
		return gtlBeanFactory2.getBean2(EntregaReingresoDocumentosFacadeRemote.class).marcarEntregado(codigo, GTLLiteGlobalCache.getInstance().getTerminalData().getNombre());
	}

	public static TerminalServiceResponse reingresar(String codigo) {
		if (codigo.endsWith("0")) {
			return gtlBeanFactory1.getBean2(EntregaReingresoDocumentosFacadeRemote.class).reingresar(codigo, GTLLiteGlobalCache.getInstance().getTerminalData().getNombre());
		}
		return gtlBeanFactory2.getBean2(EntregaReingresoDocumentosFacadeRemote.class).reingresar(codigo, GTLLiteGlobalCache.getInstance().getTerminalData().getNombre());
	}

	public static PiezaODT getPiezaODTByCodigo(String idSistema, String codPiezaODT) {
		if("0".equals(idSistema)) {//es la A
			return gtlBeanFactory1.getBean2(OrdenDeTrabajoFacadeRemote.class).getPiezaODTByCodigo(codPiezaODT);
		}
		if("1".equals(idSistema)) {//es la B
			return gtlBeanFactory1.getBean2(OrdenDeTrabajoFacadeRemote.class).getPiezaODTByCodigo(codPiezaODT);
		}
		return null;
	}

	public static RemitoSalida getRemitoSalidaByCodigo(String codigo) {
		String idSistema = codigo.substring(codigo.length()-1, codigo.length());
		Integer nroRemitoSalida = Integer.valueOf(GestorTerminalBarcode.extraer(codigo));
		if("0".equals(idSistema)) {//es la A
			List<RemitoSalida> remitos = gtlBeanFactory1.getBean2(RemitoSalidaFacadeRemote.class).getRemitosByNroRemitoConPiezasYProductos(nroRemitoSalida);
			if(!remitos.isEmpty()) {
				return remitos.get(0);
			}
		}
		if("1".equals(idSistema)) {//es la B
			List<RemitoSalida> remitos = gtlBeanFactory2.getBean2(RemitoSalidaFacadeRemote.class).getRemitosByNroRemitoConPiezasYProductos(nroRemitoSalida);
			if(!remitos.isEmpty()) {
				return remitos.get(0);
			}
		}
		return null;
	}
	
	public static void marcarRemitoSalidaComoControlado(String idSistema, RemitoSalida rs) {
		String nombreTerminal = GTLLiteGlobalCache.getInstance().getTerminalData().getNombre();
		if("0".equals(idSistema)) {//es la A
			gtlBeanFactory1.getBean2(RemitoSalidaFacadeRemote.class).marcarRemitoSalidaComoControlado(rs, nombreTerminal);
		}
		if("1".equals(idSistema)) {//es la B
			gtlBeanFactory2.getBean2(RemitoSalidaFacadeRemote.class).marcarRemitoSalidaComoControlado(rs, nombreTerminal);
		}
	}

	public static RemitoSalida guardarRemitoSalida(String idSistema, RemitoSalida rs) {
		if("0".equals(idSistema)) {//es la A
			return gtlBeanFactory1.getBean2(RemitoSalidaFacadeRemote.class).guardarRemito(rs);
		}
		if("1".equals(idSistema)) {//es la B
			return gtlBeanFactory2.getBean2(RemitoSalidaFacadeRemote.class).guardarRemito(rs);
		}
		return null;
	}
	
	public static class GTLLiteBeanFactory extends BeanFactoryRemoteAbstract {

		private static GTLLiteBeanFactory instance;

		protected GTLLiteBeanFactory() throws FWException {
			super("GTL");
			addJndiName(OrdenDeTrabajoFacadeRemote.class);
			addJndiName(TerminalFacadeRemote.class);
			addJndiName(EntregaReingresoDocumentosFacadeRemote.class);
			addJndiName(RemitoSalidaFacadeRemote.class);
			addJndiName(UsuarioSistemaFacadeRemote.class);
		}

		public static GTLLiteBeanFactory getInstance() {
			if (instance == null) {
				try {
					instance = new GTLLiteBeanFactory();
				} catch (FWException e) {
					e.printStackTrace();
				}
			}
			return instance;
		}
	}

	private static class GTLLiteOtroSistemaBeanFactory extends BeanFactoryRemoteAbstract {

		private static GTLLiteOtroSistemaBeanFactory instance;

		protected GTLLiteOtroSistemaBeanFactory() throws FWException {
			super("GTL", createPropertiesOtroSistema());
			addJndiName(OrdenDeTrabajoFacadeRemote.class);
			addJndiName(TerminalFacadeRemote.class);
			addJndiName(EntregaReingresoDocumentosFacadeRemote.class);
			addJndiName(RemitoSalidaFacadeRemote.class);
		}

		private static Properties createPropertiesOtroSistema() {
			Properties properties = new Properties();
			properties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
			properties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
			properties.put("java.naming.provider.url", System.getProperties().get("java.naming.provider.url_otro_sistema"));
			return properties;
		}

		public static GTLLiteOtroSistemaBeanFactory getInstance() {
			if (instance == null) {
				try {
					instance = new GTLLiteOtroSistemaBeanFactory();
				} catch (FWException e) {
					e.printStackTrace();
				}
			}
			return instance;
		}

	}

}