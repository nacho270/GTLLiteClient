package main;

import java.awt.Color;

import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.templates.main.menu.ManejadorMenues;

public class GuiBackGTLLite extends GTLLiteClientMainTemplate {

	private static final long serialVersionUID = -2538191821904835236L;
	private static final String VERSION = "1.0";

	protected GuiBackGTLLite(int idAplicacion, String version) throws FWException {
		super(idAplicacion, version);
		getDesktop().setBackground(new Color(255,255,255));
		setBackgroundImage("ar/com/lite/textillevel/imagenes/logogtl.jpg");
		setIconoVentana("ar/com/lite/textillevel/imagenes/logogtl-ventana.jpg");
		
		//oculto el item cambiar usuario xq no hay login
		ManejadorMenues.getManejadorMenues(this).getMenuDefault().getMenuItemCambiarUsuario().setVisible(false);
		
	}

	public static void main(String[] args) {
		try {
			System.getProperties().setProperty("applicationName", "GTL Lite");
			System.getProperties().setProperty("va.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
			System.getProperties().setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
			System.getProperties().setProperty("cltimezone","GMT-3");
			if(System.getProperty("java.naming.provider.url")==null){
				System.getProperties().setProperty("java.naming.provider.url", "localhost:1099");
			}
			GuiBackGTLLite guiBackTextilLevel = new GuiBackGTLLite(-1, VERSION);
//			EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
//			queue.push(new EventQueueProxy(guiBackTextilLevel));
			guiBackTextilLevel.setExtendedState(MAXIMIZED_BOTH);
			guiBackTextilLevel.iniciarAplicacion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}