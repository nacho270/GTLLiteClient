package ar.com.lite.textillevel.util;

import java.rmi.RemoteException;
import java.util.Properties;

import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.util.BeanFactoryRemoteAbstract;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.facade.api.remote.OrdenDeTrabajoFacadeRemote;

public class GTLLiteRemoteService {

	private static GTLLiteBeanFactory gtlBeanFactory1 = GTLLiteBeanFactory.getInstance();
	private static GTLLiteOtroSistemaBeanFactory gtlBeanFactory2 = GTLLiteOtroSistemaBeanFactory.getInstance();  
	
    public static OrdenDeTrabajo getODTByCodigo(final String codigo) throws RemoteException {
        if (codigo.endsWith("0")) {
            return gtlBeanFactory1.getBean2(OrdenDeTrabajoFacadeRemote.class).getODTEagerByCodigo(codigo);
        }
        return gtlBeanFactory2.getBean2(OrdenDeTrabajoFacadeRemote.class).getODTEagerByCodigo(codigo);
    }

	public static OrdenDeTrabajo grabarPiezasODT(OrdenDeTrabajo odt) {
        if (odt.getCodigo().endsWith("0")) {
            return gtlBeanFactory1.getBean2(OrdenDeTrabajoFacadeRemote.class).grabarPiezasODT(odt);
        }
        return gtlBeanFactory2.getBean2(OrdenDeTrabajoFacadeRemote.class).grabarPiezasODT(odt);
	}

    public static class GTLLiteBeanFactory extends BeanFactoryRemoteAbstract {

    	private static GTLLiteBeanFactory instance;
    	
    	protected GTLLiteBeanFactory() throws FWException {
    		super("GTL");
    		addJndiName(OrdenDeTrabajoFacadeRemote.class);
    	}

    	public static GTLLiteBeanFactory getInstance() {
    		if(instance == null) {
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
    	}

    	private static Properties createPropertiesOtroSistema() {
    		Properties properties = new Properties();
    		properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
    		properties.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
    		properties.put("java.naming.provider.url", System.getProperties().get("java.naming.provider.url_otro_sistema"));
    		return properties;
    	}

    	public static GTLLiteOtroSistemaBeanFactory getInstance() {
    		if(instance == null) {
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