package main;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.UIManager;

import org.apache.taglibs.string.util.StringW;

import ar.com.fwcommon.boss.BossEstilos;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.templates.main.FWMainTemplate;
import ar.com.fwcommon.templates.main.config.IConfigClienteManager;
import ar.com.fwcommon.templates.main.menu.MenuAyuda;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.fwcommon.util.MiscUtil;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.modulos.terminal.entidades.Terminal;

public class GTLLiteClientMainTemplate extends FWMainTemplate<GTLLiteClientLoginManager, GTLLiteConfigClienteManager> {

	private static final long serialVersionUID = -7589061723941536496L;
	protected MenuAyuda menuAyuda;
	private Action newInstance;

	static {
		try {
			initLookAndFeel(null);
		} catch (FWException e) {
			e.printStackTrace();
		}
	}

	protected GTLLiteClientMainTemplate(int idAplicacion, String version) throws FWException {
		super(idAplicacion, version);
		construirMenues();
	}

	@Override
	protected IConfigClienteManager crearConfigClienteManager() {
		return new GTLLiteConfigClienteManager();
	}

	@Override
	protected GTLLiteClientLoginManager crearLoginManager() {
		return new GTLLiteClientLoginManager(idAplicacion);
	}

	private void construirMenues() {
	}

	private static void initLookAndFeel(ESkin skin) throws FWException {
		if (skin != null) {
			SkinModerno skinModerno = new SkinModerno(skin);
			BossEstilos.setDefaultSkin(skinModerno);
			BossEstilos.setDefaultFont(skinModerno.getDecorator().getDefaultFont());
			BossEstilos.setSecondaryFont(skinModerno.getDecorator().getSecondaryFont());
			if (MiscUtil.isMacOS()) {
				BossEstilos.ajustarFuenteComponentes();
			}
			skinModerno.init();

		} else {
			try {
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			} catch (Exception e) {
				SkinModerno skinModerno = new SkinModerno(ESkin.AZUL);
				BossEstilos.setDefaultSkin(skinModerno);
				BossEstilos.setDefaultFont(skinModerno.getDecorator().getDefaultFont());
				BossEstilos.setSecondaryFont(skinModerno.getDecorator().getSecondaryFont());
				if (MiscUtil.isMacOS()) {
					BossEstilos.ajustarFuenteComponentes();
				}
				skinModerno.init();
			}
		}
	}

	@Override
	protected void postConstruccion() throws FWException {
	}

	@Override
	protected final void preConstruccion() {
		super.preConstruccion();
		// Configura todos los Boss
		configurarAplicacion();
	}

	@Override
	protected final void postLogin() throws FWException {
		newInstance.actionPerformed(new ActionEvent(new Object(), 0, ""));
	}

	public void iniciarAplicacion() {
		Terminal terminalData = null;
		try {
			terminalData = GTLLiteRemoteService.getTerminalData();
			
			if (terminalData == null) {
				FWJOptionPane.showErrorMessage(this, "No se ha configurado el módulo para esta terminal", "Error");
				return;
			}
			if (terminalData.getModuloPorDefecto() == null) {
				FWJOptionPane.showErrorMessage(this, "Esta terminal no tiene un modulo configurado", "Error");
				return;
			}
			crearTitulo(terminalData.getNombre());
			GTLLiteGlobalCache.getInstance().setTerminalData(terminalData);
			this.newInstance = (Action)Class.forName(terminalData.getModuloPorDefecto().getClase()).getConstructor(new Class[] { Frame.class }).newInstance(new Object[] { FWMainTemplate.getFrameInstance() });
		} catch(Exception e) {
			e.printStackTrace();
			FWJOptionPane.showErrorMessage(this, StringW.wordWrap("Ha ocurrido un error al querer iniciar el modulo: " + e.getMessage()), "Error");
		}

		setVisible(true);
		GuiUtil.centrar(this);
		if(terminalData.getModuloPorDefecto().getRequiereLogin()) {
			verDialogoLogin();
		} else {
			newInstance.actionPerformed(new ActionEvent(new Object(), 0, ""));			
		}
	}

	
	private void crearTitulo(final String nombre) throws FWException {
		StringBuffer sb = new StringBuffer("Textil Level - Terminal [");
		sb.append(nombre);
		sb.append("]");
		if (version != null) {
			sb.append(" - v").append(version);
		}
		setTitle(sb.toString());
	}

	private void configurarAplicacion() {

	}
}
