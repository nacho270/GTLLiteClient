package main;

import javax.swing.UIManager;

import ar.com.fwcommon.boss.BossEstilos;
import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.templates.main.FWMainTemplate;
import ar.com.fwcommon.templates.main.config.IConfigClienteManager;
import ar.com.fwcommon.templates.main.login.EmptyLoginManager;
import ar.com.fwcommon.templates.main.menu.MenuAyuda;
import ar.com.fwcommon.util.MiscUtil;

public class GTLLiteClientMainTemplate extends FWMainTemplate<EmptyLoginManager, GTLLiteConfigClienteManager> {

	private static final long serialVersionUID = -7589061723941536496L;
	protected MenuAyuda menuAyuda;

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
	protected EmptyLoginManager crearLoginManager() {
		return new EmptyLoginManager(idAplicacion);
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
		crearTitulo();
	}

	@Override
	protected final void preConstruccion() {
		super.preConstruccion();
		// Configura todos los Boss
		configurarAplicacion();
	}

	@Override
	protected final void postLogin() throws FWException {
		super.postLogin();
	}

	private void crearTitulo() throws FWException {
		StringBuffer sb = new StringBuffer("Textil Level - Terminal simple");
		if (version != null) {
			sb.append(" v").append(version);
		}
		setTitle(sb.toString());
	}

	private void configurarAplicacion() {

	}
}
